/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.model;

/**
 * Representa una arista o conexión direccional entre dos estados de un autómata.
 * Modela la función de transición formal matemática.
 */
public class Transicion {

    // El estado de donde sale la flecha (q)
    private final Estado origen;
    
    // El estado a donde llega la flecha (p)
    private final Estado destino;
    
    // El símbolo o conjunto de símbolos (separados por coma) que activan este camino
    private String simbolo;

    /**
     * Constructor principal de la transición.
     * 
     * @param origen  El estado de partida.
     * @param destino El estado de llegada.
     * @param simbolo El carácter que permite el paso (puede ser "a,b" para múltiples símbolos).
     */
    public Transicion(Estado origen, Estado destino, String simbolo) {
        this.origen = origen;
        this.destino = destino;
        this.simbolo = simbolo;
    }

    /* --- GETTERS Y SETTERS --- */
    
    public Estado getOrigen() { return origen; }
    public Estado getDestino() { return destino; }
    
    public String getSimbolo() { return simbolo; }
    public void setSimbolo(String simbolo) { this.simbolo = simbolo; }

    /**
     * Determina si esta transición es un "salto vacío" (Transición Epsilon).
     * Las transiciones epsilon permiten cambiar de estado sin consumir ningún carácter de la cadena de entrada.
     * 
     * @return true si el símbolo es nulo, está vacío, o es el carácter Unicode epsilon (ε).
     */
    public boolean esEpsilon() {
        return simbolo == null || simbolo.isEmpty() || simbolo.equals("\u03B5");
    }

    /**
     * Verifica si un carácter específico está incluido en esta transición.
     * Soporta transiciones agrupadas en la interfaz (por ejemplo, si la flecha dice "a,b", 
     * devolverá true tanto para "a" como para "b").
     * 
     * @param s El símbolo individual a buscar.
     * @return true si la transición permite el paso con ese símbolo.
     */
    public boolean contieneSimbolo(String s) {
        if (simbolo == null) return false;
        
        // Separa el string por comas y elimina espacios en blanco alrededor de cada símbolo
        for (String parte : simbolo.split(",")) {
            if (parte.trim().equals(s)) return true;
        }
        return false;
    }

    /**
     * Representación en texto de la transición para facilitar la depuración (debugging) en consola.
     * Ejemplo de salida: "q0 --a--> q1"
     */
    @Override
    public String toString() {
        return origen + " --" + simbolo + "--> " + destino;
    }
}