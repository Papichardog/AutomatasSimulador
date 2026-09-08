/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.model;

public class Transicion {

    private final Estado origen;
    private final Estado destino;
    private String simbolo;

    public Transicion(Estado origen, Estado destino, String simbolo) {
        this.origen = origen;
        this.destino = destino;
        this.simbolo = simbolo;
    }

    public Estado getOrigen() {
        return origen;
    }

    public Estado getDestino() {
        return destino;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public boolean esEpsilon() {
        return simbolo == null || simbolo.isEmpty() || simbolo.equals("\u03B5");
    }

    public boolean contieneSimbolo(String s) {
        if (simbolo == null) return false;
        for (String parte : simbolo.split(",")) {
            if (parte.trim().equals(s)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return origen + " --" + simbolo + "--> " + destino;
    }
}