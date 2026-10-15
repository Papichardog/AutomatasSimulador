/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.umg.automatas;
// Documentado por: BibliotecaVirtualProg
// Punto de entrada principal de la aplicación. Inicializa el Controlador Principal,
// crea el autómata inicial y despliega de forma segura el hilo de la Ventana Principal Swing.

import com.umg.automatas.controller.ControladorPrincipal;
import com.umg.automatas.view.VentanaPrincipal;


import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ControladorPrincipal controlador = new ControladorPrincipal();
            VentanaPrincipal ventana = new VentanaPrincipal(controlador);
            controlador.crearNuevoAutomata();
            ventana.mostrar();
        });
    }
}
