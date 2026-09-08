/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.view;

import javax.swing.JPanel;
import java.awt.BorderLayout;

public abstract class PanelBase extends JPanel {

    protected String titulo;

    public PanelBase(String titulo) {
        super(new BorderLayout());
        this.titulo = titulo;
        setBorder(javax.swing.BorderFactory.createTitledBorder(titulo));
    }

    public abstract void actualizarVista();
}
