package com.umg.automatas.algorithm;

import com.umg.automatas.model.Automata;
import com.umg.automatas.model.Estado;
import com.umg.automatas.model.Transicion;

import java.util.*;

public class MinimizadorAFD implements IAlgoritmoTransformacion {

    @Override
    public Automata transformar(Automata original) {
        Automata completo = original.completar(original.getAlfabeto());
        List<Character> alfabeto = new ArrayList<>(completo.getAlfabeto());

        List<Estado> conexos = obtenerConexos(completo);
        List<Set<Estado>> particion = particionInicial(conexos);
        particion = refinar(particion, completo, alfabeto);

        return construirAutomataMinimo(completo, particion, alfabeto);
    }

    /** Paso 1: solo estados alcanzables desde q0. */
    private List<Estado> obtenerConexos(Automata a) {
        Set<Estado> visitados = new LinkedHashSet<>();
        Deque<Estado> pendientes = new ArrayDeque<>();
        pendientes.add(a.getEstadoInicial());
        visitados.add(a.getEstadoInicial());
        while (!pendientes.isEmpty()) {
            Estado actual = pendientes.poll();
            for (Transicion t : a.getTransicionesDesde(actual)) {
                if (visitados.add(t.getDestino())) pendientes.add(t.getDestino());
            }
        }
        return new ArrayList<>(visitados);
    }

    /** Paso 2: C1 = F, C2 = Q - F. */
    private List<Set<Estado>> particionInicial(List<Estado> estados) {
        Set<Estado> finales = new LinkedHashSet<>();
        Set<Estado> noFinales = new LinkedHashSet<>();
        for (Estado e : estados) {
            if (e.esFinal()) finales.add(e); else noFinales.add(e);
        }
        List<Set<Estado>> particion = new ArrayList<>();
        if (!finales.isEmpty()) particion.add(finales);
        if (!noFinales.isEmpty()) particion.add(noFinales);
        return particion;
    }

    /** Pasos 3-5: divide cada clase hasta que todos sus estados aterricen,
     *  para cada simbolo, en la misma clase destino. */
    private List<Set<Estado>> refinar(List<Set<Estado>> particion, Automata a, List<Character> alfabeto) {
        boolean cambio = true;
        while (cambio) {
            cambio = false;
            List<Set<Estado>> nueva = new ArrayList<>();
            for (Set<Estado> clase : particion) {
                Map<List<Integer>, Set<Estado>> subclases = new LinkedHashMap<>();
                for (Estado e : clase) {
                    List<Integer> firma = new ArrayList<>();
                    for (Character simbolo : alfabeto) {
                        firma.add(indiceClase(particion, destinoPor(a, e, simbolo)));
                    }
                    subclases.computeIfAbsent(firma, k -> new LinkedHashSet<>()).add(e);
                }
                if (subclases.size() > 1) cambio = true;
                nueva.addAll(subclases.values());
            }
            particion = nueva;
        }
        return particion;
    }

    private Estado destinoPor(Automata a, Estado origen, Character simbolo) {
        for (Transicion t : a.getTransicionesDesde(origen)) {
            if (t.contieneSimbolo(String.valueOf(simbolo))) return t.getDestino();
        }
        return null;
    }

    private int indiceClase(List<Set<Estado>> particion, Estado e) {
        for (int i = 0; i < particion.size(); i++) {
            if (particion.get(i).contains(e)) return i;
        }
        return -1;
    }

    private Automata construirAutomataMinimo(Automata completo, List<Set<Estado>> particion, List<Character> alfabeto) {
        Automata minimo = new Automata(completo.getNombre() + "_minimo");
        Map<Set<Estado>, Estado> nuevoPorClase = new LinkedHashMap<>();

        int contador = 0;
        int columnas = 4;
        double espacioX = 150, espacioY = 120;
        for (Set<Estado> clase : particion) {
            Estado representante = clase.iterator().next();
            Estado nuevo = new Estado("C" + contador,
                    (contador % columnas) * espacioX,
                    (contador / columnas) * espacioY);
            nuevo.marcarComoFinal(representante.esFinal());
            nuevo.marcarComoInicial(clase.contains(completo.getEstadoInicial()));
            nuevoPorClase.put(clase, nuevo);
            minimo.agregarEstado(nuevo);
            contador++;
        }

        Map<List<Estado>, List<String>> simbolosPorPar = new LinkedHashMap<>();
        for (Set<Estado> clase : particion) {
            Estado representante = clase.iterator().next();
            Estado origenNuevo = nuevoPorClase.get(clase);
            for (Character simbolo : alfabeto) {
                Set<Estado> claseDestino = buscarClase(particion, destinoPor(completo, representante, simbolo));
                Estado destinoNuevo = nuevoPorClase.get(claseDestino);
                simbolosPorPar.computeIfAbsent(Arrays.asList(origenNuevo, destinoNuevo), k -> new ArrayList<>())
                        .add(String.valueOf(simbolo));
            }
        }
        for (Map.Entry<List<Estado>, List<String>> entrada : simbolosPorPar.entrySet()) {
            minimo.agregarTransicion(new Transicion(entrada.getKey().get(0), entrada.getKey().get(1),
                    String.join(",", entrada.getValue())));
        }
        return minimo;
    }

    private Set<Estado> buscarClase(List<Set<Estado>> particion, Estado e) {
        for (Set<Estado> clase : particion) {
            if (clase.contains(e)) return clase;
        }
        return null;
    }
}
