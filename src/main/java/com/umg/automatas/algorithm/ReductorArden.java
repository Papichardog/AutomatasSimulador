/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.algorithm;

// =========================================================================
// DESARROLLADO E INTEGRADO POR: Pablo Pichardo (Papichardog)
// CURSO: Autómatas y Lenguajes Formales - UMG 2026
// ALGORITMO: Reducción de Autómatas por el Método Algebraico de Arden
// =========================================================================
// Este componente procesa el grafo del autómata como un GNFA (Generalized 
// NFA) utilizando una matriz bidimensional de Expresiones Regulares.
// Aplica de forma iterativa el Lema de Arden: R = Q.R + P => R = (Q)*.P
// eliminando secuencialmente los estados intermedios y recalculando las
// aristas mediante el ciclo dinámico O(n³) de Floyd-Warshall:
// R_ik' = R_ik U (R_ij · (R_jj)* · R_jk)
// =========================================================================


import com.umg.automatas.model.Automata;
import com.umg.automatas.model.Estado;
import com.umg.automatas.model.ExpresionRegular;
import com.umg.automatas.model.Transicion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReductorArden implements IAlgoritmoReduccion {

    private ExpresionRegular[][] matrizExpresiones;
    private Automata automataNormalizado;
    private final List<Automata> pasosIntermedios = new ArrayList<>();

    @Override
    public ExpresionRegular reducir(Automata automata) {
        pasosIntermedios.clear();
        automataNormalizado = normalizar(automata);
        pasosIntermedios.add(automataNormalizado.clonar());

        List<Estado> activos = new ArrayList<>(automataNormalizado.getEstados());
        Estado inicio = automataNormalizado.getEstadoInicial();
        
        // CORREGIDO: Se invoca al método dinámico getEstadosFinales() para obtener el estado de aceptación
        Estado aceptacion = automataNormalizado.getEstadosFinales().iterator().next();

        int n = activos.size();
        Map<Estado, Integer> indice = new HashMap<>();
        for (int i = 0; i < n; i++) indice.put(activos.get(i), i);

        matrizExpresiones = new ExpresionRegular[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                matrizExpresiones[i][j] = ExpresionRegular.vacio();

        for (Transicion t : automataNormalizado.getTransiciones()) {
            int i = indice.get(t.getOrigen());
            int j = indice.get(t.getDestino());
            ExpresionRegular etiqueta = t.esEpsilon()
                    ? ExpresionRegular.epsilon()
                    : ExpresionRegular.simbolo(t.getSimbolo());
            matrizExpresiones[i][j] = ExpresionRegular.union(matrizExpresiones[i][j], etiqueta);
        }

        List<Estado> pendientes = new ArrayList<>(activos);
        pendientes.remove(inicio);
        pendientes.remove(aceptacion);

        for (Estado k : pendientes) {
            eliminarEstado(indice.get(k), n);
            activos.remove(k);
            pasosIntermedios.add(reconstruirSnapshot(activos, indice, n));
        }

        int s = indice.get(inicio);
        int f = indice.get(aceptacion);
        ExpresionRegular ciclo = ExpresionRegular.cerradura(matrizExpresiones[s][s]);
        ExpresionRegular resultado = ExpresionRegular.concatenacion(ciclo, matrizExpresiones[s][f]);
        return resultado.simplificar();
    }

    private void eliminarEstado(int k, int n) {
        ExpresionRegular ciclo = ExpresionRegular.cerradura(matrizExpresiones[k][k]);
        for (int i = 0; i < n; i++) {
            if (i == k || matrizExpresiones[i][k].esVacio()) continue;
            for (int j = 0; j < n; j++) {
                if (j == k || matrizExpresiones[k][j].esVacio()) continue;
                ExpresionRegular viaK = ExpresionRegular.concatenacion(
                        ExpresionRegular.concatenacion(matrizExpresiones[i][k], ciclo),
                        matrizExpresiones[k][j]);
                matrizExpresiones[i][j] = ExpresionRegular.union(matrizExpresiones[i][j], viaK);
            }
        }
    }

    private Automata reconstruirSnapshot(List<Estado> activos, Map<Estado, Integer> indice, int n) {
        Automata snap = new Automata("paso_reduccion");
        for (Estado e : activos) snap.agregarEstado(e);
        for (Estado i : activos) {
            for (Estado j : activos) {
                ExpresionRegular label = matrizExpresiones[indice.get(i)][indice.get(j)];
                if (!label.esVacio()) {
                    snap.agregarTransicion(new Transicion(i, j, label.toString()));
                }
            }
        }
        return snap;
    }

    private Automata normalizar(Automata original) {
        Automata copia = original.clonar();

        Estado qStart = new Estado("q_start", -80, 0);
        Estado qAccept = new Estado("q_accept", 800, 0);
        qStart.marcarComoInicial(true);
        qAccept.marcarComoFinal(true);

        for (Estado e : copia.getEstados()) e.marcarComoInicial(false);

        Estado antiguoInicial = null;
        List<Estado> antiguosFinales = new ArrayList<>(copia.getEstadosFinales());
        for (Estado e : original.getEstados()) {
            if (e.esInicial()) antiguoInicial = buscarPorId(copia, e.getId());
        }

        copia.agregarEstado(qStart);
        copia.agregarEstado(qAccept);
        copia.agregarTransicion(new Transicion(qStart, antiguoInicial, "\u03B5"));
        for (Estado f : antiguosFinales) {
            f.marcarComoFinal(false);
            copia.agregarTransicion(new Transicion(f, qAccept, "\u03B5"));
        }
        
        // BORRADO: Las líneas de .getEstadosFinales().clear() y .add() se eliminaron exitosamente
        
        return copia;
    }

    private Estado buscarPorId(Automata a, String id) {
        for (Estado e : a.getEstados()) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    public List<Automata> getPasosIntermedios() {
        return pasosIntermedios;
    }
}
