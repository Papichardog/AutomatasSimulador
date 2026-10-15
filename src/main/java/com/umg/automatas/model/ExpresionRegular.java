/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.model;

/**
 * Representa una Expresión Regular utilizando una estructura de Árbol de Sintaxis Abstracta (AST).
 * Permite construir expresiones complejas a partir de operaciones fundamentales: 
 * Unión, Concatenación y Cerradura de Kleene.
 */
public class ExpresionRegular {

    // Define qué tipo de operación o valor fundamental representa este nodo del árbol
    private final TipoOperador operador;
    
    // Almacena el carácter o texto si es un nodo de tipo SIMBOLO
    private final String simbolo;
    
    // Referencia al hijo izquierdo (usado en UNION y CONCATENACION, o como base para CERRADURA)
    private final ExpresionRegular izquierda;
    
    // Referencia al hijo derecho (usado solo en UNION y CONCATENACION)
    private final ExpresionRegular derecha;

    /**
     * Constructor privado para forzar el uso de los métodos de fábrica (Factory Methods).
     * Esto garantiza que las expresiones se construyan siguiendo las reglas formales.
     */
    private ExpresionRegular(TipoOperador operador, String simbolo,
                              ExpresionRegular izquierda, ExpresionRegular derecha) {
        this.operador = operador;
        this.simbolo = simbolo;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    // FACTORY METHODS (Nodos Hoja)

    // Crea un nodo que representa el lenguaje vacío (Ø)
    public static ExpresionRegular vacio() { 
        return new ExpresionRegular(TipoOperador.VACIO, null, null, null); 
    }
    
    // Crea un nodo que representa la palabra vacía (ε).
    public static ExpresionRegular epsilon() { 
        return new ExpresionRegular(TipoOperador.EPSILON, null, null, null); 
    }
    
    // Crea un nodo que representa un símbolo del alfabeto (ej. 'a', 'b')
    public static ExpresionRegular simbolo(String s) { 
        return new ExpresionRegular(TipoOperador.SIMBOLO, s, null, null); 
    }

    // FACTORY METHODS (Operaciones con simplificación matemática automática)

    /**
     * Aplica la operación de Unión (a U b).
     * Incluye la ley de identidad: a U Ø = a, y Ø U b = b.
     */
    public static ExpresionRegular union(ExpresionRegular a, ExpresionRegular b) {
        if (a.esVacio()) return b;
        if (b.esVacio()) return a;
        return new ExpresionRegular(TipoOperador.UNION, null, a, b);
    }

    /**
     * Aplica la operación de Concatenación (a · b).
     * Incluye leyes formales:
     * - Elemento absorbente (dominancia): a · Ø = Ø
     * - Elemento neutro (identidad): a · ε = a, y ε · b = b
     */
    public static ExpresionRegular concatenacion(ExpresionRegular a, ExpresionRegular b) {
        if (a.esVacio() || b.esVacio()) return vacio();
        if (a.esEpsilon()) return b;
        if (b.esEpsilon()) return a;
        return new ExpresionRegular(TipoOperador.CONCATENACION, null, a, b);
    }

    /**
     * Aplica la operación de Cerradura de Kleene (a*).
     * Incluye leyes formales: Ø* = ε, y ε* = ε.
     */
    public static ExpresionRegular cerradura(ExpresionRegular a) {
        if (a.esVacio() || a.esEpsilon()) return epsilon();
        return new ExpresionRegular(TipoOperador.CERRADURA, null, a, null);
    }

    // MÉTODOS DE CONSULTA

    public boolean esVacio() { return operador == TipoOperador.VACIO; }
    public boolean esEpsilon() { return operador == TipoOperador.EPSILON; }
    public TipoOperador getOperador() { return operador; }

    /**
     * Punto de extensión para aplicar teoremas de simplificación adicionales
     * (por ejemplo, a** = a*, o (a U a) = a).
     */
    public ExpresionRegular simplificar() {
        return this; // TODO: aplicar mas teoremas de simplificacion si se requiere
    }

    /**
     * Devuelve la representación en notación matemática formal.
     * Utiliza caracteres Unicode para los símbolos formales (Ø, ε, U).
     */
    @Override
    public String toString() {
        switch (operador) {
            case VACIO: return "\u2205"; // Símbolo Ø
            case EPSILON: return "\u03B5"; // Símbolo ε
            case SIMBOLO: return simbolo;
            case UNION: return "(" + izquierda + " \u222A " + derecha + ")"; // Símbolo U
            case CONCATENACION: return izquierda.toString() + derecha.toString();
            case CERRADURA: return "(" + izquierda + ")*";
            default: return "?";
        }
    }

    /**
     * Traduce el modelo matemático formal a una sintaxis compatible con el 
     * motor de expresiones regulares nativo de Java (java.util.regex.Pattern).
     */
    public String toJavaRegex() {
        switch (operador) {
            // (?!): Lookahead negativo que siempre falla, equivalente práctico a un conjunto vacío
            case VACIO: return "(?!)"; 
            
            case EPSILON: return "";
            
            // Escapa caracteres especiales (como +, *, .) para que se tomen como texto literal
            case SIMBOLO: return java.util.regex.Pattern.quote(simbolo);
            
            // (?: ... ) crea un grupo sin captura, evitando afectar el rendimiento del motor
            case UNION: return "(?:" + izquierda.toJavaRegex() + "|" + derecha.toJavaRegex() + ")";
            
            case CONCATENACION: return izquierda.toJavaRegex() + derecha.toJavaRegex();
            
            case CERRADURA: return "(?:" + izquierda.toJavaRegex() + ")*";
            
            default: return "";
        }
    }
}
