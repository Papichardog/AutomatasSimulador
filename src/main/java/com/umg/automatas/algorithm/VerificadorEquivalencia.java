/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.algorithm;

import com.umg.automatas.model.Automata;
import com.umg.automatas.model.Estado;
import com.umg.automatas.model.Transicion;

import java.util.*;

public class VerificadorEquivalencia {

    public static class Resultado {
        public final boolean sonEquivalentes;
        public final String cadenaDistinguidora;

        public Resultado(boolean sonEquivalentes, String cadenaDistinguidora) {
            this.sonEquivalentes = sonEquivalentes;
            this.cadenaDistinguidora = cadenaDistinguidora;
        }
    }

    private record Par(Estado a, Estado b) {}

    /** L(M1) = L(M2) sii ningun par de estados alcanzable en el producto
     *  tiene un lado final y el otro no. Si se encuentra un par asi, la
     *  cadena que llevo hasta ahi es un contraejemplo (distingue a M1 de M2). */
    public Resultado verificar(Automata m1, Automata m2) {
        Set<Character> alfabetoUnido = new LinkedHashSet<>();
        alfabetoUnido.addAll(m1.getAlfabeto());
        alfabetoUnido.addAll(m2.getAlfabeto());

        Automata c1 = m1.completar(alfabetoUnido);
        Automata c2 = m2.completar(alfabetoUnido);

        Par inicio = new Par(c1.getEstadoInicial(), c2.getEstadoInicial());
        if (inicio.a().esFinal() != inicio.b().esFinal()) {
            return new Resultado(false, "\u03B5 (cadena vacia)");
        }

        Map<Par, String> caminoHasta = new LinkedHashMap<>();
        caminoHasta.put(inicio, "");
        Deque<Par> pendientes = new ArrayDeque<>();
        pendientes.add(inicio);

        while (!pendientes.isEmpty()) {
            Par actual = pendientes.poll();
            String prefijo = caminoHasta.get(actual);
            for (Character simbolo : alfabetoUnido) {
                Estado destinoA = destinoPor(c1, actual.a(), simbolo);
                Estado destinoB = destinoPor(c2, actual.b(), simbolo);
                Par siguiente = new Par(destinoA, destinoB);
                if (caminoHasta.containsKey(siguiente)) continue;

                String nuevoCamino = prefijo + simbolo;
                caminoHasta.put(siguiente, nuevoCamino);

                if (destinoA.esFinal() != destinoB.esFinal()) {
                    return new Resultado(false, nuevoCamino);
                }
                pendientes.add(siguiente);
            }
        }
        return new Resultado(true, null);
    }

    private Estado destinoPor(Automata a, Estado origen, Character simbolo) {
        for (Transicion t : a.getTransicionesDesde(origen)) {
            if (t.contieneSimbolo(String.valueOf(simbolo))) return t.getDestino();
        }
        return null;
    }
}
