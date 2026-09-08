/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.controller;

import com.mxgraph.model.mxCell;

import com.umg.automatas.algorithm.EvaluadorExpresionRegular;
import com.umg.automatas.algorithm.GeneradorComplemento;
import com.umg.automatas.algorithm.MinimizadorAFD;
import com.umg.automatas.algorithm.ReductorArden;
import com.umg.automatas.algorithm.VerificadorEquivalencia;
import com.umg.automatas.model.Automata;
import com.umg.automatas.model.Estado;
import com.umg.automatas.model.ExpresionRegular;
import com.umg.automatas.model.Transicion;
import com.umg.automatas.simulation.SimuladorAnimado;
import com.umg.automatas.view.LienzoAutomata;
import com.umg.automatas.view.VentanaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControladorPrincipal {

    private Automata automata;
    private LienzoAutomata lienzo;
    private VentanaPrincipal ventana;
    private SimuladorAnimado simulador;

    private final ReductorArden reductor = new ReductorArden();
    private final GeneradorComplemento generadorComplemento = new GeneradorComplemento();
    private final MinimizadorAFD minimizador = new MinimizadorAFD();
    private final VerificadorEquivalencia verificadorEquivalencia = new VerificadorEquivalencia();

    private ExpresionRegular ultimaExpresionRegular;
    private Estado origenPendiente;
    private boolean modoSoloLectura = false;

    public void setVentana(VentanaPrincipal ventana) { this.ventana = ventana; }
    public void setLienzo(LienzoAutomata lienzo) { this.lienzo = lienzo; }
    public Automata getAutomata() { return automata; }

    // ---- Ciclo de vida del automata ----

    public void crearNuevoAutomata() {
        this.automata = new Automata("NuevoAutomata");
        this.ultimaExpresionRegular = null;
        this.modoSoloLectura = false;
        origenPendiente = null;
        if (lienzo != null) lienzo.setAutomata(automata);
    }

    public void guardarAutomata(String ruta) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(ruta))) {
            w.write("ESTADOS"); w.newLine();
            for (Estado e : automata.getEstados()) {
                w.write(e.getId() + "|" + e.getPosicion().getX() + "|" + e.getPosicion().getY()
                        + "|" + e.esInicial() + "|" + e.esFinal() + "|" + e.getEtiqueta());
                w.newLine();
            }
            w.write("TRANSICIONES"); w.newLine();
            for (Transicion t : automata.getTransiciones()) {
                w.write(t.getOrigen().getId() + "|" + t.getDestino().getId() + "|" + t.getSimbolo());
                w.newLine();
            }
        }
    }

    public void cargarAutomata(String ruta) throws IOException {
        this.automata = cargarAutomataDesdeArchivo(ruta);
        this.ultimaExpresionRegular = null;
        this.modoSoloLectura = false;
        origenPendiente = null;
        if (lienzo != null) lienzo.setAutomata(automata);
    }

    private Automata cargarAutomataDesdeArchivo(String ruta) throws IOException {
        Automata nuevo = new Automata(new File(ruta).getName());
        Map<String, Estado> mapa = new HashMap<>();
        try (BufferedReader r = new BufferedReader(new FileReader(ruta))) {
            String linea;
            String seccion = "";
            while ((linea = r.readLine()) != null) {
                if (linea.equals("ESTADOS") || linea.equals("TRANSICIONES")) { seccion = linea; continue; }
                if (linea.isBlank()) continue;
                String[] campos = linea.split("\\|", -1);
                if (seccion.equals("ESTADOS")) {
                    Estado e = new Estado(campos[0], Double.parseDouble(campos[1]), Double.parseDouble(campos[2]));
                    e.marcarComoInicial(Boolean.parseBoolean(campos[3]));
                    e.marcarComoFinal(Boolean.parseBoolean(campos[4]));
                    e.setEtiqueta(campos[5]);
                    mapa.put(e.getId(), e);
                    nuevo.agregarEstado(e);
                } else if (seccion.equals("TRANSICIONES")) {
                    nuevo.agregarTransicion(new Transicion(mapa.get(campos[0]), mapa.get(campos[1]), campos[2]));
                }
            }
        }
        return nuevo;
    }

    // ---- Edicion desde el lienzo ----

    public void crearEstadoEn(double x, double y) {
        if (bloqueadoPorSoloLectura()) return;
        String id = "q" + automata.getEstados().size();
        Estado nuevo = new Estado(id, x, y);
        if (automata.getEstados().isEmpty()) nuevo.marcarComoInicial(true);
        automata.agregarEstado(nuevo);
        lienzo.dibujarEstado(nuevo);
    }

    public void eliminarEstadoEn(mxCell celda) {
        if (bloqueadoPorSoloLectura()) return;
        Estado estado = buscarEstadoPorId(celda.getId());
        if (estado == null) return;
        int confirmacion = JOptionPane.showConfirmDialog(ventana,
                "Eliminar el estado " + estado.getEtiqueta() + " y todas sus transiciones?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        automata.eliminarEstado(estado);
        origenPendiente = null;
        lienzo.setAutomata(automata);

        if (automata.getEstadoInicial() == null && !automata.getEstados().isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "El automata se quedo sin estado inicial. Usa Shift+clic sobre un estado para asignar uno nuevo.");
        }
    }

    public void seleccionarCelda(mxCell celda, MouseEvent evento) {
        if (bloqueadoPorSoloLectura()) return;
        if (celda.isEdge()) {
            if (SwingUtilities.isRightMouseButton(evento)) {
                Transicion t = lienzo.getTransicionEnCelda(celda);
                if (t != null) {
                    automata.eliminarTransicion(t);
                    lienzo.eliminarAristaVisual(t);
                }
            }
            return;
        }
        if (!celda.isVertex()) return;
        Estado estado = buscarEstadoPorId(celda.getId());
        if (estado == null) return;

        if (SwingUtilities.isRightMouseButton(evento)) {
            estado.marcarComoFinal(!estado.esFinal());
            lienzo.setAutomata(automata);
            return;
        }
        if (evento.isShiftDown()) {
            automata.establecerEstadoInicial(estado);
            lienzo.setAutomata(automata);
            origenPendiente = null;
            return;
        }
        if (origenPendiente == null) {
            origenPendiente = estado;
        } else {
            String simbolo = JOptionPane.showInputDialog(ventana, "Simbolo de la transicion:");
            if (simbolo != null && !simbolo.isEmpty()) {
                Transicion existente = buscarTransicionEntre(origenPendiente, estado);
                if (existente != null) {
                    if (!existente.contieneSimbolo(simbolo)) {
                        existente.setSimbolo(existente.getSimbolo() + "," + simbolo);
                    }
                } else {
                    automata.agregarTransicion(new Transicion(origenPendiente, estado, simbolo));
                }
                lienzo.setAutomata(automata);
            }
            origenPendiente = null;
        }
    }

    private boolean bloqueadoPorSoloLectura() {
        if (!modoSoloLectura) return false;
        JOptionPane.showMessageDialog(ventana,
                "Estas viendo un automata derivado (Reduccion/Complemento/Minimo).\nPresiona 'Restaurar vista' para volver a editar el tuyo.");
        return true;
    }

    private Transicion buscarTransicionEntre(Estado origen, Estado destino) {
        for (Transicion t : automata.getTransicionesDesde(origen)) {
            if (t.getDestino().equals(destino)) return t;
        }
        return null;
    }

    private Estado buscarEstadoPorId(Object id) {
        for (Estado e : automata.getEstados()) {
            if (e.getId().equals(String.valueOf(id))) return e;
        }
        return null;
    }

    // ---- Simulacion ----

    public SimuladorAnimado iniciarSimulacion(String cadena) {
        simulador = new SimuladorAnimado(automata);
        simulador.iniciar(cadena);
        return simulador;
    }

    public boolean evaluarConAutomata(String cadena) { return automata.evaluar(cadena); }

    public boolean evaluarConRegex(String cadena) {
        if (ultimaExpresionRegular == null) {
            throw new IllegalStateException("Primero presiona 'Reducir' para obtener la expresion regular.");
        }
        return new EvaluadorExpresionRegular(ultimaExpresionRegular).evaluar(cadena);
    }

    // ---- Reduccion (Arden) ----

    public ExpresionRegular ejecutarReduccion() {
        ultimaExpresionRegular = reductor.reducir(automata);
        modoSoloLectura = true;
        return ultimaExpresionRegular;
    }

    public List<Automata> getPasosIntermediosReduccion() { return reductor.getPasosIntermedios(); }

    public void restaurarVistaOriginal() {
        modoSoloLectura = false;
        lienzo.setAutomata(automata);
    }

    // ---- Extras: Complemento, Minimizacion, Equivalencia ----

    public void mostrarComplemento() {
        Automata complemento = generadorComplemento.transformar(automata);
        modoSoloLectura = true;
        lienzo.setAutomata(complemento);
    }

    public void mostrarMinimizacion() {
        Automata minimo = minimizador.transformar(automata);
        modoSoloLectura = true;
        lienzo.setAutomata(minimo);
    }

    public String compararConArchivo(String ruta) throws IOException {
        Automata otro = cargarAutomataDesdeArchivo(ruta);
        VerificadorEquivalencia.Resultado r = verificadorEquivalencia.verificar(automata, otro);
        if (r.sonEquivalentes) {
            return "SI son equivalentes (aceptan el mismo lenguaje).";
        }
        return "NO son equivalentes.<br>Cadena distinguidora: \"" + r.cadenaDistinguidora + "\"";
    }
}