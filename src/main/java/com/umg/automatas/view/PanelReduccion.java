package com.umg.automatas.view;

import com.umg.automatas.controller.ControladorPrincipal;
import com.umg.automatas.model.Automata;
import com.umg.automatas.model.ExpresionRegular;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.List;

public class PanelReduccion extends PanelBase {

    private final ControladorPrincipal controlador;
    private final LienzoAutomata lienzo;
    private final JLabel etiquetaExpresion;
    private final JTextField campoCadenaVerificacion;
    private final JLabel etiquetaResultadoAFD;
    private final JLabel etiquetaResultadoRegex;
    private final JLabel etiquetaCoincidencia;
    private List<Automata> pasosAnimacion;
    private int pasoActual;

    public PanelReduccion(ControladorPrincipal controlador, LienzoAutomata lienzo) {
        super("Reduccion por Arden");
        this.controlador = controlador;
        this.lienzo = lienzo;

        etiquetaExpresion = new JLabel("Expresion regular: (pendiente)");
        JButton botonReducir = new JButton("Reducir");
botonReducir.addActionListener(e -> ejecutarReduccion());

JButton botonRestaurar = new JButton("Restaurar vista");
botonRestaurar.addActionListener(e -> controlador.restaurarVistaOriginal());

JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
filaBotones.add(botonReducir);
filaBotones.add(botonRestaurar);
add(filaBotones, BorderLayout.NORTH);

        campoCadenaVerificacion = new JTextField(10);
        JButton botonVerificar = new JButton("Verificar equivalencia");
        botonVerificar.addActionListener(e -> verificarEquivalencia());

        JPanel filaVerificacion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaVerificacion.add(new JLabel("Cadena:"));
        filaVerificacion.add(campoCadenaVerificacion);
        filaVerificacion.add(botonVerificar);

        etiquetaResultadoAFD = new JLabel(" ");
        etiquetaResultadoRegex = new JLabel(" ");
        etiquetaCoincidencia = new JLabel(" ");

        JPanel panelVerificacion = new JPanel();
        panelVerificacion.setLayout(new BoxLayout(panelVerificacion, BoxLayout.Y_AXIS));
        panelVerificacion.add(filaVerificacion);
        panelVerificacion.add(etiquetaResultadoAFD);
        panelVerificacion.add(etiquetaResultadoRegex);
        panelVerificacion.add(etiquetaCoincidencia);

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(etiquetaExpresion, BorderLayout.NORTH);
        panelCentro.add(panelVerificacion, BorderLayout.SOUTH);
        add(panelCentro, BorderLayout.CENTER);
    }

    public void ejecutarReduccion() {
        ExpresionRegular resultado = controlador.ejecutarReduccion();
        pasosAnimacion = controlador.getPasosIntermediosReduccion();
        pasoActual = 0;
        etiquetaExpresion.setText("Expresion regular: " + resultado.toString());
        limpiarResultadosVerificacion();

        Timer temporizador = new Timer(900, null);
        temporizador.addActionListener(e -> {
            mostrarPasoSiguiente();
            if (pasoActual >= pasosAnimacion.size()) {
                temporizador.stop();
            }
        });
        temporizador.start();
    }

    public void mostrarPasoSiguiente() {
        if (pasosAnimacion == null || pasoActual >= pasosAnimacion.size()) return;
        lienzo.setAutomata(pasosAnimacion.get(pasoActual));
        pasoActual++;
    }

    private void verificarEquivalencia() {
        String cadena = campoCadenaVerificacion.getText();
        try {
            boolean resultadoAFD = controlador.evaluarConAutomata(cadena);
            boolean resultadoRegex = controlador.evaluarConRegex(cadena);

            etiquetaResultadoAFD.setText("AFD dice: " + (resultadoAFD ? "ACEPTADA" : "RECHAZADA"));
            etiquetaResultadoAFD.setForeground(resultadoAFD ? new Color(46, 125, 50) : new Color(198, 40, 40));

            etiquetaResultadoRegex.setText("Regex dice: " + (resultadoRegex ? "ACEPTADA" : "RECHAZADA"));
            etiquetaResultadoRegex.setForeground(resultadoRegex ? new Color(46, 125, 50) : new Color(198, 40, 40));

            if (resultadoAFD == resultadoRegex) {
                etiquetaCoincidencia.setText("Coinciden \u2713");
                etiquetaCoincidencia.setForeground(new Color(46, 125, 50));
            } else {
                etiquetaCoincidencia.setText("NO coinciden - hay un bug en el pipeline");
                etiquetaCoincidencia.setForeground(new Color(198, 40, 40));
            }
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void limpiarResultadosVerificacion() {
        etiquetaResultadoAFD.setText(" ");
        etiquetaResultadoRegex.setText(" ");
        etiquetaCoincidencia.setText(" ");
    }

    @Override
    public void actualizarVista() {
        etiquetaExpresion.setText("Expresion regular: (pendiente)");
        limpiarResultadosVerificacion();
    }
}