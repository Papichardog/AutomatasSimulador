/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.umg.automatas.model;

/**
 * Define los tipos de nodos posibles dentro del Árbol de Sintaxis Abstracta (AST) 
 * que construye una Expresión Regular.
 * Se utiliza y es el corazon de la clase ExpresionRegular para determinar el comportamiento
 * lógico y de traducción a sintaxis Java de cada nodo.
 */
public enum TipoOperador {
    // Operación binaria: El lenguaje acepta lo del nodo izquierdo o lo del nodo derecho (a U b)
    UNION,
    // Operación binaria: El lenguaje acepta el nodo izquierdo seguido inmediatamente del derecho (a · b)
    CONCATENACION,
    // Operación unaria: Cero o más repeticiones del nodo hijo (a*)
    CERRADURA,
    // Nodo Hoja: Representa un carácter literal del alfabeto (ej. 'a')
    SIMBOLO,
    // Nodo Hoja: Representa la palabra vacía, un salto que no consume caracteres (ε)
    EPSILON,
    // Nodo Hoja: Representa el conjunto vacío, un camino que no puede ser recorrido (Ø)
    VACIO
}