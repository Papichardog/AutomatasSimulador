/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.simulation;

/**
* control de simulacion animada de un automata finito Determinista.
*
* Procesa una cadena de entrada simbolo por simbolo administrando el tiempo 
* entre trancisiones y notifica los cambios de estado a los observadores.
* Parte documentada por Diego Alejandro Lopez Sequeira. (Dlopez2205)
*/

import com.umg.automatas.model.Automata;
import com.umg.automatas.model.Estado;
import com.umg.automatas.model.Transicion;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;


public class SimuladorAnimado {
    private Automata automata;
    private final List<IObservadorAutomata> observadores = new ArrayList<>();
    private int velocidadMs = 700;
    private EstadoSimulacion estadoActual = EstadoSimulacion.DETENIDO;
    private Timer temporizador;

    private String cadena;
    private int posicion;
    private Estado estadoActualAFD;

    public SimuladorAnimado(Automata automata) {
        this.automata = automata;
    }

    public void setAutomata(Automata automata) {
        this.automata = automata;
    }

    public void setVelocidadMs(int velocidadMs) {
        this.velocidadMs = velocidadMs;
    }

    public void agregarObservador(IObservadorAutomata o) {
        observadores.add(o);
    }

    public void iniciar(String cadenaEntrada) {
        if (temporizador != null && temporizador.isRunning()) {
            temporizador.stop();
        }
        this.cadena = cadenaEntrada;
        this.posicion = 0;
        this.estadoActualAFD = automata.getEstadoInicial();
        this.estadoActual = EstadoSimulacion.EJECUTANDO;

        temporizador = new Timer(velocidadMs, e -> paso());
        temporizador.setInitialDelay(0);
        temporizador.start();
    }

    public void pausar() {
        if (temporizador != null && temporizador.isRunning()) {
            temporizador.stop();
            estadoActual = EstadoSimulacion.PAUSADO;
        }
    }

    public void reanudar() {
        if (temporizador != null && estadoActual == EstadoSimulacion.PAUSADO) {
            temporizador.start();
            estadoActual = EstadoSimulacion.EJECUTANDO;
        }
    }

    public void detener() {
        if (temporizador != null) {
            temporizador.stop();
        }
        estadoActual = EstadoSimulacion.DETENIDO;
    }

    private void paso() {
        if (estadoActualAFD == null || posicion >= cadena.length()) {
            finalizar();
            return;
        }

        String simbolo = String.valueOf(cadena.charAt(posicion));
        Transicion usada = null;
        for (Transicion t : automata.getTransicionesDesde(estadoActualAFD)) {
            if (t.getSimbolo().equals(simbolo)) {
                usada = t;
                break;
            }
        }

        if (usada == null) {
            estadoActualAFD = null;
            finalizar();
            return;
        }

        estadoActualAFD = usada.getDestino();
        posicion++;
        notificarCambioEstado(estadoActualAFD, usada);

        if (posicion >= cadena.length()) {
            finalizar();
        }
    }

    private void finalizar() {
        if (temporizador != null) {
            temporizador.stop();
        }
        estadoActual = EstadoSimulacion.FINALIZADO;
        
        // ==== MODIFICACIÓN SOLICITADA POR CLAUDE ====
        boolean aceptada = estadoActualAFD != null && estadoActualAFD.esFinal();
        // ============================================
        
        notificarFinAnimacion(aceptada);
    }

    private void notificarCambioEstado(Estado actual, Transicion t) {
        for (IObservadorAutomata o : observadores) {
            o.onCambioEstado(actual, t);
        }
    }

    private void notificarFinAnimacion(boolean aceptada) {
        for (IObservadorAutomata o : observadores) {
            o.onFinAnimacion(aceptada);
        }
    }

    public EstadoSimulacion getEstadoActual() {
        return estadoActual;
    }
}
// Docs: Documentación de la logica del temporizador y estados de animación.
