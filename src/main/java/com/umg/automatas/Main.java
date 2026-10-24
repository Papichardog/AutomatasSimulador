/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.umg.automatas;

// Documentado por: BibliotecaVirtualProg
// Punto de entrada principal de la aplicación. Inicializa el Look and Feel (FlatLaf),
// el Controlador Principal, crea el autómata inicial y despliega de forma segura
// el hilo de la Ventana Principal Swing.

import com.formdev.flatlaf.FlatDarculaLaf;
import com.umg.automatas.controller.ControladorPrincipal;
import com.umg.automatas.view.VentanaPrincipal;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        // 1. Configurar FlatLaf ANTES de crear cualquier componente Swing
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("No se pudo inicializar FlatLaf: " + ex.getMessage());
        }

        // 2. Decoracion moderna en la barra de titulo de las ventanas
        JFrame.setDefaultLookAndFeelDecorated(true);

        // 3. Crear y mostrar la UI en el hilo de eventos (EDT)
        SwingUtilities.invokeLater(() -> {
            ControladorPrincipal controlador = new ControladorPrincipal();
            VentanaPrincipal ventana = new VentanaPrincipal(controlador);
            controlador.crearNuevoAutomata();
            ventana.mostrar();
        });
    }
}
