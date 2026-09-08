/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.umg.automatas.model;

import com.umg.automatas.algorithm.IEvaluable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Automata implements IEvaluable {

    public static String version = "1.0";

    private String nombre;
    private final List<Estado> estados = new ArrayList<>();
    private final List<Transicion> transiciones = new ArrayList<>();
    private final Set<Character> alfabeto = new LinkedHashSet<>();
    private Estado estadoInicial;

    public Automata(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Estado> getEstados() { return estados; }
    public List<Transicion> getTransiciones() { return transiciones; }
    public Set<Character> getAlfabeto() { return alfabeto; }
    public Estado getEstadoInicial() { return estadoInicial; }

    public void agregarEstado(Estado e) {
        estados.add(e);
        if (e.esInicial()) estadoInicial = e;
    }

    public void eliminarEstado(Estado e) {
        estados.remove(e);
        transiciones.removeIf(t -> t.getOrigen().equals(e) || t.getDestino().equals(e));
        if (e.equals(estadoInicial)) estadoInicial = null;
    }

    public void establecerEstadoInicial(Estado nuevo) {
        if (estadoInicial != null) estadoInicial.marcarComoInicial(false);
        nuevo.marcarComoInicial(true);
        estadoInicial = nuevo;
    }

    public void agregarTransicion(Transicion t) {
        transiciones.add(t);
        if (!t.esEpsilon() && t.getSimbolo() != null) {
            for (String parte : t.getSimbolo().split(",")) {
                if (parte.trim().length() == 1) alfabeto.add(parte.trim().charAt(0));
            }
        }
    }

    public void eliminarTransicion(Transicion t) {
        transiciones.remove(t);
    }

    public List<Transicion> getTransicionesDesde(Estado e) {
        List<Transicion> resultado = new ArrayList<>();
        for (Transicion t : transiciones) {
            if (t.getOrigen().equals(e)) resultado.add(t);
        }
        return resultado;
    }

    public Set<Estado> getEstadosFinales() {
        Set<Estado> resultado = new LinkedHashSet<>();
        for (Estado e : estados) {
            if (e.esFinal()) resultado.add(e);
        }
        return resultado;
    }

    @Override
    public boolean evaluar(String cadena) {
        if (estadoInicial == null) {
            throw new IllegalStateException("El automata no tiene estado inicial definido.");
        }
        Estado actual = estadoInicial;
        for (int i = 0; i < cadena.length(); i++) {
            String simbolo = String.valueOf(cadena.charAt(i));
            Estado siguiente = null;
            for (Transicion t : getTransicionesDesde(actual)) {
                if (t.contieneSimbolo(simbolo)) {
                    siguiente = t.getDestino();
                    break;
                }
            }
            if (siguiente == null) return false;
            actual = siguiente;
        }
        return actual.esFinal();
    }

    public Automata clonar() {
        Automata copia = new Automata(this.nombre);
        java.util.Map<String, Estado> mapaEstados = new java.util.HashMap<>();
        for (Estado e : estados) {
            Estado nuevo = new Estado(e.getId(), e.getPosicion().getX(), e.getPosicion().getY());
            nuevo.setEtiqueta(e.getEtiqueta());
            nuevo.marcarComoInicial(e.esInicial());
            nuevo.marcarComoFinal(e.esFinal());
            mapaEstados.put(e.getId(), nuevo);
            copia.agregarEstado(nuevo);
        }
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
        for (Estado e : copia.getEstados()) {
            for (Character simbolo : alfabetoCompleto) {
                if (!tieneTransicionPara(copia, e, simbolo)) {
                    necesitaTrampa = true;
                    break;
                }
            }
            if (necesitaTrampa) break;
        }

        if (necesitaTrampa) {
            Estado trampa = new Estado("q_trampa", -200, 300);
            List<Estado> estadosOriginales = new ArrayList<>(copia.getEstados());
            copia.agregarEstado(trampa);
            for (Character simbolo : alfabetoCompleto) {
                copia.agregarTransicion(new Transicion(trampa, trampa, String.valueOf(simbolo)));
            }
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

    private boolean tieneTransicionPara(Automata a, Estado e, Character simbolo) {
        for (Transicion t : a.getTransicionesDesde(e)) {
            if (t.contieneSimbolo(String.valueOf(simbolo))) return true;
        }
        return false;
    }
}
