/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.model;

public class ExpresionRegular {

    private final TipoOperador operador;
    private final String simbolo;
    private final ExpresionRegular izquierda;
    private final ExpresionRegular derecha;

    private ExpresionRegular(TipoOperador operador, String simbolo,
                              ExpresionRegular izquierda, ExpresionRegular derecha) {
        this.operador = operador;
        this.simbolo = simbolo;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    public static ExpresionRegular vacio() { return new ExpresionRegular(TipoOperador.VACIO, null, null, null); }
    public static ExpresionRegular epsilon() { return new ExpresionRegular(TipoOperador.EPSILON, null, null, null); }
    public static ExpresionRegular simbolo(String s) { return new ExpresionRegular(TipoOperador.SIMBOLO, s, null, null); }

    public static ExpresionRegular union(ExpresionRegular a, ExpresionRegular b) {
        if (a.esVacio()) return b;
        if (b.esVacio()) return a;
        return new ExpresionRegular(TipoOperador.UNION, null, a, b);
    }

    public static ExpresionRegular concatenacion(ExpresionRegular a, ExpresionRegular b) {
        if (a.esVacio() || b.esVacio()) return vacio();
        if (a.esEpsilon()) return b;
        if (b.esEpsilon()) return a;
        return new ExpresionRegular(TipoOperador.CONCATENACION, null, a, b);
    }

    public static ExpresionRegular cerradura(ExpresionRegular a) {
        if (a.esVacio() || a.esEpsilon()) return epsilon();
        return new ExpresionRegular(TipoOperador.CERRADURA, null, a, null);
    }

    public boolean esVacio() { return operador == TipoOperador.VACIO; }
    public boolean esEpsilon() { return operador == TipoOperador.EPSILON; }
    public TipoOperador getOperador() { return operador; }

    public ExpresionRegular simplificar() {
        return this; // TODO: aplicar mas teoremas de simplificacion si se requiere
    }

    @Override
    public String toString() {
        switch (operador) {
            case VACIO: return "\u2205";
            case EPSILON: return "\u03B5";
            case SIMBOLO: return simbolo;
            case UNION: return "(" + izquierda + " \u222A " + derecha + ")";
            case CONCATENACION: return izquierda.toString() + derecha.toString();
            case CERRADURA: return "(" + izquierda + ")*";
            default: return "?";
        }
    }

    public String toJavaRegex() {
        switch (operador) {
            case VACIO: return "(?!)";
            case EPSILON: return "";
            case SIMBOLO: return java.util.regex.Pattern.quote(simbolo);
            case UNION: return "(?:" + izquierda.toJavaRegex() + "|" + derecha.toJavaRegex() + ")";
            case CONCATENACION: return izquierda.toJavaRegex() + derecha.toJavaRegex();
            case CERRADURA: return "(?:" + izquierda.toJavaRegex() + ")*";
            default: return "";
        }
    }
}
