/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.model;

import java.awt.geom.Point2D;

/**
 * Representa un estado individual (nodo) dentro de un autómata (un elemento q ∈ Q).
 * Almacena tanto su información lógica (si es inicial o de aceptación) 
 * como su información visual (coordenadas para la interfaz gráfica).
 */
public class Estado {
    // Identificador único e inmutable del estado (ej. "q0", "q1")
    private final String id;
    // Nombre visible que se mostrará dibujado en la interfaz gráfica
    private String etiqueta;
    // Bandera que indica si es el estado de arranque del autómata
    private boolean esInicial;
    // Bandera que indica si es un estado de aceptación (pertenece a F)
    private boolean esFinal;
    // Coordenadas para el renderizado del nodo en el lienzo (canvas)
    private double posicionX;
    private double posicionY;

    /**
     * Constructor para crear un nuevo estado.
     * Por defecto, un estado no es ni inicial ni final al momento de instanciarse.
     * @param id Identificador único del estado.
     * @param x  Posición inicial en el eje X para la interfaz gráfica.
     * @param y  Posición inicial en el eje Y para la interfaz gráfica.
     */
    public Estado(String id, double x, double y) {
        this.id = id;
        this.etiqueta = id; // Por defecto, la etiqueta visual es igual al ID
        this.posicionX = x;
        this.posicionY = y;
        this.esInicial = false;
        this.esFinal = false;
    }
    
    // Getters y Setters.
    
    public String getId() { return id; }
    
    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public boolean esInicial() { return esInicial; }
    public void marcarComoInicial(boolean valor) { this.esInicial = valor; }

    public boolean esFinal() { return esFinal; }
    public void marcarComoFinal(boolean valor) { this.esFinal = valor; }

    /**
     * Retorna la posición espacial del estado encapsulada en un objeto Point2D.
     * Útil para calcular distancias o dibujar las transiciones.
     */
    public Point2D getPosicion() { return new Point2D.Double(posicionX, posicionY); }
    
    // Actualiza las coordenadas del estado (útil para funciones de arrastrar y soltar en la UI).
    public void setPosicion(double x, double y) { 
        this.posicionX = x;  
        this.posicionY = y;  
    }

    /**
     * Compara dos estados basándose exclusivamente en su ID.
     * Esto es crucial para que las listas y conjuntos (como LinkedHashSet en Automata) 
     * no permitan estados duplicados y puedan encontrar el objeto correcto en memoria.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Estado)) return false;
        return id.equals(((Estado) obj).id);
    }

    /**
     * Genera un código numérico basado en el ID para su uso eficiente en estructuras 
     * de datos basadas en hash (como HashMap o HashSet).
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Representación en texto del estado. 
     * Devuelve la etiqueta visual para que los componentes de la interfaz (como tablas o listas) 
     * muestren el nombre amigable en lugar de la referencia en memoria.
     */
    @Override
    public String toString() {
        return etiqueta;
    }
}