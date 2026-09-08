/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.view;

import com.umg.automatas.controller.ControladorPrincipal;
import com.umg.automatas.model.Estado;
import com.umg.automatas.model.Transicion;
import com.umg.automatas.simulation.IObservadorAutomata;
import com.umg.automatas.simulation.SimuladorAnimado;

import javax.swing.*;
import java.awt.*;

public class PanelSimulacion extends PanelBase implements IObservadorAutomata {

    private final JTextField campoCadena;
    private final JLabel etiquetaResultado;
    private SimuladorAnimado simulador;
    private final ControladorPrincipal controlador;
    private final LienzoAutomata lienzo;

    public PanelSimulacion(ControladorPrincipal controlador, LienzoAutomata lienzo) {
        super("Simulacion de Cadena");
        this.controlador = controlador;
        this.lienzo = lienzo;

        campoCadena = new JTextField(15);
        etiquetaResultado = new JLabel(" ");
        JButton botonIniciar = new JButton("Simular");
        botonIniciar.addActionListener(e -> iniciarSimulacion());

        JPanel entrada = new JPanel(new FlowLayout(FlowLayout.LEFT));
        entrada.add(new JLabel("Cadena:"));
        entrada.add(campoCadena);
        entrada.add(botonIniciar);

        add(entrada, BorderLayout.NORTH);
        add(etiquetaResultado, BorderLayout.SOUTH);
    }

    private void iniciarSimulacion() {
        
        simulador = controlador.iniciarSimulacion(campoCadena.getText());
        simulador.agregarObservador(this);
        etiquetaResultado.setText("Simulando...");
    }

    @Override
    public void onCambioEstado(Estado actual, Transicion transicionUsada) {
        lienzo.resaltarEstado(actual);
    }

    @Override
    public void onFinAnimacion(boolean aceptada) {
        etiquetaResultado.setText(aceptada ? "ACEPTADA" : "RECHAZADA");
        etiquetaResultado.setForeground(aceptada ? new Color(46, 125, 50) : new Color(198, 40, 40));
    }

    @Override
    public void actualizarVista() {
        etiquetaResultado.setText(" ");
    }
}
