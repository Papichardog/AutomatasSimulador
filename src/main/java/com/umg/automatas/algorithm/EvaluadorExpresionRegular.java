/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.algorithm;

import com.umg.automatas.model.ExpresionRegular;

import java.util.regex.Pattern;

public class EvaluadorExpresionRegular implements IEvaluable {

    private final ExpresionRegular expresionOriginal;
    private final Pattern patron;

    public EvaluadorExpresionRegular(ExpresionRegular expresionOriginal) {
        this.expresionOriginal = expresionOriginal;
        this.patron = Pattern.compile("^" + convertirAJavaRegex() + "$");
    }

    @Override
    public boolean evaluar(String cadena) {
        return patron.matcher(cadena).matches();
    }

    private String convertirAJavaRegex() {
        return expresionOriginal.toJavaRegex();
    }

    public ExpresionRegular getExpresionOriginal() {
        return expresionOriginal;
    }
}
