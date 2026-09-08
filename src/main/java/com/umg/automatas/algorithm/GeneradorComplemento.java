/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.algorithm;

import com.umg.automatas.model.Automata;
import com.umg.automatas.model.Estado;

/** M2 acepta L(M1)^C: mismo Q, mismo alfabeto, misma delta, pero F' = Q - F
 *  (Clase 6, "AFD Complemento"). Requiere que el automata sea total primero. */
public class GeneradorComplemento implements IAlgoritmoTransformacion {

    @Override
    public Automata transformar(Automata original) {
        Automata complemento = original.completar(original.getAlfabeto());
        complemento.setNombre(original.getNombre() + "_complemento");
        for (Estado e : complemento.getEstados()) {
            e.marcarComoFinal(!e.esFinal());
        }
        return complemento;
    }
}
