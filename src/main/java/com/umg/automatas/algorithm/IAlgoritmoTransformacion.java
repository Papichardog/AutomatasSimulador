/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.algorithm;

import com.umg.automatas.model.Automata;

/** Estrategia para algoritmos que transforman un AFD en otro AFD (a diferencia
 *  de IAlgoritmoReduccion, que produce una ExpresionRegular). */
public interface IAlgoritmoTransformacion {
    Automata transformar(Automata automata);
}
