/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.view;
//Ventana principal encargada de coordinar los paneles de swing
import com.umg.automatas.controller.ControladorPrincipal;

import javax.swing.*;
import java.io.File;

public class VentanaPrincipal extends JFrame {

    private LienzoAutomata lienzo;
    private PanelSimulacion panelSimulacion;
    private PanelReduccion panelReduccion;
    private PanelExtras panelExtras;
    private ControladorPrincipal controlador;

    public VentanaPrincipal(ControladorPrincipal controlador) {
        super("Simulador y Evaluador de DFA - UMG");
        this.controlador = controlador;
        this.controlador.setVentana(this);
        inicializarComponentes();
    }

    public void inicializarComponentes() {
        lienzo = new LienzoAutomata(controlador);
        controlador.setLienzo(lienzo);
        panelSimulacion = new PanelSimulacion(controlador, lienzo);
        panelReduccion = new PanelReduccion(controlador, lienzo);
        panelExtras = new PanelExtras(controlador);

        JPanel panelLateral = new JPanel();
        panelLateral.setLayout(new BoxLayout(panelLateral, BoxLayout.Y_AXIS));
        panelLateral.add(panelSimulacion);
        panelLateral.add(panelReduccion);
        panelLateral.add(panelExtras);
        JScrollPane scrollLateral = new JScrollPane(panelLateral);
        scrollLateral.setBorder(null);

        JSplitPane raiz = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lienzo, scrollLateral);
        raiz.setDividerLocation(650);

        setContentPane(raiz);
        crearMenu();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 700);
        setLocationRelativeTo(null);
    }

    private void crearMenu() {
        JMenuBar barra = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemNuevo = new JMenuItem("Nuevo automata");
        itemNuevo.addActionListener(e -> controlador.crearNuevoAutomata());

        JMenuItem itemGuardar = new JMenuItem("Guardar...");
        itemGuardar.addActionListener(e -> guardar());

        JMenuItem itemAbrir = new JMenuItem("Abrir...");
        itemAbrir.addActionListener(e -> abrir());

        menuArchivo.add(itemNuevo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemAbrir);
        barra.add(menuArchivo);
        setJMenuBar(barra);
    }

    private void guardar() {
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File("automata.afd"));
        if (selector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controlador.guardarAutomata(selector.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Guardado correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
            }
        }
    }

    private void abrir() {
        JFileChooser selector = new JFileChooser();
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controlador.cargarAutomata(selector.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage());
            }
        }
    }

    public void mostrar() { setVisible(true); }
    public LienzoAutomata getLienzo() { return lienzo; }
}
