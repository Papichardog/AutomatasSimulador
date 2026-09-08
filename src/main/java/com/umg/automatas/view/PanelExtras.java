/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.view;

import com.umg.automatas.controller.ControladorPrincipal;

import javax.swing.*;
import java.awt.*;

public class PanelExtras extends PanelBase {

    private final ControladorPrincipal controlador;
    private final JLabel etiquetaEstado;

    public PanelExtras(ControladorPrincipal controlador) {
        super("Extras (Clase 6)");
        this.controlador = controlador;

        JButton botonComplemento = new JButton("Ver Complemento");
        botonComplemento.addActionListener(e -> controlador.mostrarComplemento());

        JButton botonMinimizar = new JButton("Ver AFD Minimo");
        botonMinimizar.addActionListener(e -> controlador.mostrarMinimizacion());

        JButton botonComparar = new JButton("Comparar con otro automata...");
        botonComparar.addActionListener(e -> compararConArchivo());

        JPanel botones = new JPanel(new GridLayout(3, 1, 4, 4));
        botones.add(botonComplemento);
        botones.add(botonMinimizar);
        botones.add(botonComparar);
        add(botones, BorderLayout.NORTH);

        etiquetaEstado = new JLabel(" ");
        add(etiquetaEstado, BorderLayout.SOUTH);
    }

    private void compararConArchivo() {
        JFileChooser selector = new JFileChooser();
        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            String resultado = controlador.compararConArchivo(selector.getSelectedFile().getAbsolutePath());
            etiquetaEstado.setText("<html><body style='width:220px'>" + resultado + "</body></html>");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al comparar: " + ex.getMessage());
        }
    }

    @Override
    public void actualizarVista() {
        etiquetaEstado.setText(" ");
    }
}
