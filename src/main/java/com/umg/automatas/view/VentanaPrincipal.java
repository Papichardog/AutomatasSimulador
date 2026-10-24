/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.view;

// Ventana principal encargada de coordinar los paneles de swing
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.umg.automatas.controller.ControladorPrincipal;

import javax.swing.*;
import java.io.File;

public class VentanaPrincipal extends JFrame {

    private LienzoAutomata lienzo;
    private PanelSimulacion panelSimulacion;
    private PanelReduccion panelReduccion;
    private PanelExtras panelExtras;
    private ControladorPrincipal controlador;

    // Items del menu Tema (para marcar cual esta activo)
    private JRadioButtonMenuItem itemClaro;
    private JRadioButtonMenuItem itemOscuro;

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

        // ----- Menu Archivo -----
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

        // ----- Menu Tema (solo 2 opciones: Claro=IntelliJ, Oscuro=Darcula) -----
        JMenu menuTema = new JMenu("Tema");
        ButtonGroup grupoTemas = new ButtonGroup();

        itemClaro = new JRadioButtonMenuItem("Claro");
        itemClaro.addActionListener(e -> cambiarTema(new FlatIntelliJLaf()));
        grupoTemas.add(itemClaro);
        menuTema.add(itemClaro);

        itemOscuro = new JRadioButtonMenuItem("Oscuro");
        itemOscuro.addActionListener(e -> cambiarTema(new FlatDarculaLaf()));
        grupoTemas.add(itemOscuro);
        menuTema.add(itemOscuro);

        marcarTemaActual();

        barra.add(menuArchivo);
        barra.add(menuTema);
        setJMenuBar(barra);
    }

    // ----- Cambia el Look and Feel en caliente -----
    private void cambiarTema(LookAndFeel laf) {
        try {
            UIManager.setLookAndFeel(laf);
            FlatLaf.updateUI();
            if (lienzo != null) {
                lienzo.actualizarEstilosTema();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cambiar tema: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----- Marca el radio button del tema que esta activo -----
    private void marcarTemaActual() {
        LookAndFeel actual = UIManager.getLookAndFeel();
        if (actual instanceof FlatDarculaLaf) itemOscuro.setSelected(true);
        else itemClaro.setSelected(true);
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
