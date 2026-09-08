/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.umg.automatas.simulation;
import com.umg.automatas.model.Estado;
import com.umg.automatas.model.Transicion;

public interface IObservadorAutomata {
     void onCambioEstado(Estado actual, Transicion transicionUsada);
    void onFinAnimacion(boolean aceptada);
}
