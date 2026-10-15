/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.umg.automatas.model;

// Documentacion tecnica de los atributos y metodos del modelo matematico
import com.umg.automatas.algorithm.IEvaluable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Representa un automata finito(AFD o AFND) mediante sus estados, transiciones,
 * alfabeto y estado inicial.
 */
public class Automata implements IEvaluable {

    // Version actual del modelo de automata.
    public static String version = "1.0";

    // Nombre con el que se identifica el automata.
    private String nombre;
    // Estados que forman el automata.
    private final List<Estado> estados = new ArrayList<>();
    // Transiciones entre los estados.
    private final List<Transicion> transiciones = new ArrayList<>();
    // Simbolos no epsilon presentes en las transiciones o el alfabeto.
    private final Set<Character> alfabeto = new LinkedHashSet<>();
    // Estado desde el que comienza la evaluacion.
    private Estado estadoInicial;

    /**
     * Crea un automata con el nombre indicado.
     * @param nombre nombre del automata
     */
    public Automata(String nombre) {
        this.nombre = nombre;
    }

    // Getters and setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Estado> getEstados() { return estados; }
    public List<Transicion> getTransiciones() { return transiciones; }
    public Set<Character> getAlfabeto() { return alfabeto; }
    public Estado getEstadoInicial() { return estadoInicial; }

    /**
     * Agrega un estado y lo registra como inicial si ya esta marcado como tal.
     * @param e estado que se agrega
     * Si el estado viene marcado como inicial, se asigna automáticamente como 
     * el estado inicial del autómata.
     */
    public void agregarEstado(Estado e) {
        estados.add(e);
        if (e.esInicial()) estadoInicial = e;
    }

    /**
     * Elimina un estado, sus transiciones incidentes y la referencia inicial
     * si corresponde.
     */
    public void eliminarEstado(Estado e) {
        estados.remove(e);
        // Elimina cualquier transición donde este estado sea origen o destino (evita referencias huérfanas)
        transiciones.removeIf(t -> t.getOrigen().equals(e) || t.getDestino().equals(e));
        // Si el estado eliminado era el inicial, el autómata se queda sin estado inicial
        if (e.equals(estadoInicial)) estadoInicial = null;
    }

    /**
     * Define cuál será el estado de arranque. 
     * Se asegura de desmarcar el estado inicial anterior (si existía) para mantener la unicidad.
     */
    public void establecerEstadoInicial(Estado nuevo) {
        if (estadoInicial != null) estadoInicial.marcarComoInicial(false);
        nuevo.marcarComoInicial(true);
        estadoInicial = nuevo;
    }

    /**
     * Agrega una transicion y registra en el alfabeto sus simbolos no epsilon.
     * Si la etiqueta contiene simbolos separados por comas, registra cada uno.
     */
    public void agregarTransicion(Transicion t) {
        transiciones.add(t);
        // Si no es transición vacía (epsilon), extrae los símbolos separados por coma
        if (!t.esEpsilon() && t.getSimbolo() != null) {
            for (String parte : t.getSimbolo().split(",")) {
        // Solo agrega caracteres individuales al alfabeto general del autómata
                if (parte.trim().length() == 1) alfabeto.add(parte.trim().charAt(0));
            }
        }
    }

    // Elimina la transicion indicada.
    public void eliminarTransicion(Transicion t) {
        transiciones.remove(t);
    }

    /**
     * Obtiene las transiciones cuyo estado de origen es el indicado.
     * Útil para calcular los caminos posibles durante la evaluación
     */
    public List<Transicion> getTransicionesDesde(Estado e) {
        List<Transicion> resultado = new ArrayList<>();
        for (Transicion t : transiciones) {
            if (t.getOrigen().equals(e)) resultado.add(t);
        }
        return resultado;
    }

    /**
     * Obtiene los estados marcados como finales.
     * retorna conjunto nuevo con los estados finales o en otras palabras
     * retorna el conjunto de estados de aceptación (F).
     */
    public Set<Estado> getEstadosFinales() {
        Set<Estado> resultado = new LinkedHashSet<>();
        for (Estado e : estados) {
            if (e.esFinal()) resultado.add(e);
        }
        return resultado;
    }

    /**
     * Evalua una cadena siguiendo desde el estado inicial la primera transicion
     * que acepta cada simbolo; la cadena se acepta si termina en un estado final.
     */
    @Override
    public boolean evaluar(String cadena) {
        // Validar que el autómata esté listo para arrancar
        if (estadoInicial == null) {
            throw new IllegalStateException("El automata no tiene estado inicial definido.");
        }
        Estado actual = estadoInicial;
        // Recorre símbolo por símbolo la cadena de entrada
        for (int i = 0; i < cadena.length(); i++) {
            String simbolo = String.valueOf(cadena.charAt(i));
            Estado siguiente = null;
        // Busca una transición válida desde el estado actual para el símbolo leído
            for (Transicion t : getTransicionesDesde(actual)) {
                if (t.contieneSimbolo(simbolo)) {
                    siguiente = t.getDestino();
                    break; // Toma el primer camino válido (comportamiento de AFD)
                }
            }
    // Si no hay transición para el símbolo, la cadena es rechazada inmediatamente
            if (siguiente == null) return false;
        // Avanza al siguiente estado
            actual = siguiente;
        }
    // Al terminar de leer toda la cadena, verifica si cayó en un estado de aceptación
        return actual.esFinal();
    }

     // Crea una copia independiente de los estados y transiciones de este automata
    public Automata clonar() {
        Automata copia = new Automata(this.nombre);
    // Usamos un mapa para relacionar los IDs antiguos con los nuevos objetos de Estado creados
        java.util.Map<String, Estado> mapaEstados = new java.util.HashMap<>();
    // 1. Clonar todos los estados
        for (Estado e : estados) {
            Estado nuevo = new Estado(e.getId(), e.getPosicion().getX(), e.getPosicion().getY());
            nuevo.setEtiqueta(e.getEtiqueta());
            nuevo.marcarComoInicial(e.esInicial());
            nuevo.marcarComoFinal(e.esFinal());
            mapaEstados.put(e.getId(), nuevo);
            copia.agregarEstado(nuevo);
        }
    // 2. Clonar las transiciones usando los nuevos estados del mapa
        for (Transicion t : transiciones) {
            copia.agregarTransicion(new Transicion(
                    mapaEstados.get(t.getOrigen().getId()),
                    mapaEstados.get(t.getDestino().getId()),
                    t.getSimbolo()));
        }
        return copia;
    }

    /**
     * Devuelve una copia de este automata donde delta esta definida para
     * TODOS los estados y TODOS los simbolos del alfabeto recibido
     * (agrega un estado trampa/sumidero si hace falta). Requisito previo
     * para Complemento y Minimizacion, que solo son correctos sobre un
     * AFD total (ver la definicion formal M=(Q,Sigma,s,F,delta) de la
     * Clase 5, donde delta: Q x Sigma -> Q es funcion total).
     */
    public Automata completar(Set<Character> alfabetoCompleto) {
        Automata copia = this.clonar();
        boolean necesitaTrampa = false;
    // Verifica si existe algún estado al que le falte procesar algún símbolo del alfabeto
        for (Estado e : copia.getEstados()) {
            for (Character simbolo : alfabetoCompleto) {
                if (!tieneTransicionPara(copia, e, simbolo)) {
                    necesitaTrampa = true;
                    break;
                }
            }
            if (necesitaTrampa) break;
        }
// Si el autómata no era total, se construye y conecta el estado trampa
        if (necesitaTrampa) {
            Estado trampa = new Estado("q_trampa", -200, 300); // Se dibuja en una coordenada específica
            List<Estado> estadosOriginales = new ArrayList<>(copia.getEstados());
            copia.agregarEstado(trampa);
// 1. El estado trampa hace un bucle hacia sí mismo para todo el alfabeto
            for (Character simbolo : alfabetoCompleto) {
                copia.agregarTransicion(new Transicion(trampa, trampa, String.valueOf(simbolo)));
            }
// 2. Conectar las transiciones faltantes de los demás estados hacia la trampa
            for (Estado e : estadosOriginales) {
                for (Character simbolo : alfabetoCompleto) {
                    if (!tieneTransicionPara(copia, e, simbolo)) {
                        copia.agregarTransicion(new Transicion(e, trampa, String.valueOf(simbolo)));
                    }
                }
            }
        }
        return copia;
    }

    // Comprueba si un estado tiene una transicion que acepta el simbolo indicado
    private boolean tieneTransicionPara(Automata a, Estado e, Character simbolo) {
        for (Transicion t : a.getTransicionesDesde(e)) {
            if (t.contieneSimbolo(String.valueOf(simbolo))) return true;
        }
        return false;
    }
}
