/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.model;

import java.awt.geom.Point2D;

public class Estado {

    private final String id;
    private String etiqueta;
    private boolean esInicial;
    private boolean esFinal;
    private double posicionX;
    private double posicionY;

    public Estado(String id, double x, double y) {
        this.id = id;
        this.etiqueta = id;
        this.posicionX = x;
        this.posicionY = y;
        this.esInicial = false;
        this.esFinal = false;
    }

    public String getId() {
        return id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public boolean esInicial() {
        return esInicial;
    }

    public void marcarComoInicial(boolean valor) {
        this.esInicial = valor;
    }

    public boolean esFinal() {
        return esFinal;
    }

    public void marcarComoFinal(boolean valor) {
        this.esFinal = valor;
    }

    public Point2D getPosicion() {
        return new Point2D.Double(posicionX, posicionY);
    }

    public void setPosicion(double x, double y) {
        this.posicionX = x;
        this.posicionY = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Estado)) return false;
        return id.equals(((Estado) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
