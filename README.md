# Editor y Simulador de Autómatas Finitos (Método de Arden) 🤖💻

¡Bienvenido al repositorio oficial del proyecto **Editor y Simulador de Autómatas Finitos**! Esta aplicación de escritorio está desarrollada en **Java 21 (Swing + JGraphX)** bajo una arquitectura limpia **Modelo-Vista-Controlador (MVC)**, permitiendo el diseño, simulación y reducción algebraica de autómatas de forma interactiva.

---

## Datos del Proyecto
* **Institución:** Universidad Mariano Gálvez de Guatemala
* **Facultad:** Ingeniería en Sistemas de Información
* **Curso:** Autómatas y Lenguajes Formales (Sección C)
* **Catedrático:** Ing. Danilo Andrés Escolín Rodríguez
* **Fecha:** Octubre de 2026

### Integrantes del Grupo
1. **Pablo Andres Pichardo Galindo** — Carné: 3190-24-19124 (Administrador / GitHub: Papichardog)
2. **Henry Homar Torres Franco** — Carné: 3190-24-13372 (GitHub: nano2633)
3. **Jeshua Andreé Girón Pérez** — Carné: 3190-24-2965 (GitHub: Gironjeshua4-alt)
4. **Diego Alejandro López Sequeira** — Carné: 3190-24-1653 (GitHub: Dlopez2205)
5. **Hartwin Alexander Botello López** — Carné: 3190-24-1865 (GitHub: BibliotecaVirtualProg)

---

## Requerimientos Funcionales Implementados

El sistema cubre con rigor los cuatro pilares solicitados en la guía del proyecto, sumando extensiones avanzadas de la asignatura:

1. **Editor e Interfaz Gráfica (Lienzo Interactivo):** Permite agregar estados con doble clic, trazar transiciones dirigidas con etiquetas del alfabeto y definir visualmente el Estado Inicial (\(q_0\)) y los Estados de Aceptación (\(F\)).
2. **Motor de Simulación y Evaluación:** Espacio interactivo para evaluar cadenas con un recorrido animado paso a paso gobernado por un `javax.swing.Timer` que resalta el estado activo.
3. **Algoritmo de Eliminación de Estados (Conversión a Regex):** Normaliza el autómata e implementa la fórmula recursiva del **Lema de Arden**:
   \[R_{ik}' = R_{ik} \cup (R_{ij} \cdot (R_{jj})^* \cdot R_{jk})\]
   Mostrando una animación en el lienzo de cómo colapsan los nodos hasta obtener la Expresión Regular final.
4. **Verificación de Equivalencia:** Módulo automatizado que contrasta la cadena sobre el AFD contra el motor de `java.util.regex` de la Regex calculada para validar su exactitud.
5. **Propiedades de Cierre de la Clase 6 (Extras):** Botones integrados para generar de forma inmediata el **Autómata Complemento**, aplicar el algoritmo de **Minimización de Estados** por partición y realizar la **Comparación de Equivalencia** entre dos archivos `.afd` distintos.

---

##  Estructura de Arquitectura (MVC)

El código fuente se encuentra rígidamente estructurado en el paquete raíz `com.umg.automatas`, dividiéndose en subpaquetes lógicos para asegurar la modularidad:
* `com.umg.automatas.model`: Representación formal del grafo (`Automata`, `Estado`, `Transicion`, `ExpresionRegular`).
* `com.umg.automatas.view`: Paneles Swing y componentes gráficos del lienzo (`LienzoAutomata`, `PanelReduccion`, `PanelSimulacion`, `PanelExtras`).
* `com.umg.automatas.controller`: Coordinador de eventos e interacciones (`ControladorPrincipal`).
* `com.umg.automatas.simulation`: Estado de la animación y observadores del temporizador (`SimuladorAnimado`).
* `com.umg.automatas.algorithm`: Los algoritmos de transformación y el cerebro matemático de Arden (`ReductorArden`, `MinimizadorAFD`, `GeneradorComplemento`, `VerificadorEquivalencia`).

---

## Instrucciones de Compilación y Ejecución

El proyecto está gestionado por **Apache Maven** y viene configurado para generar un **Fat-JAR** ejecutable e independiente que incluye la librería JGraphX embebida en su interior.

### 1. Clonar el repositorio
```bash
git clone https://github.com/Papichardog/AutomatasSimulador.git
cd AutomatasSimulador
```

###  2. Limpiar y Empaquetar el ejecutable (Generar el .jar)
```bash
mvn clean package
```
*Este comando compilará el código fuente y creará un archivo ejecutable unificado de aproximadamente **892 KB** en la ruta `target/AutomatasSimulador-1.0-SNAPSHOT.jar`.*

###  3. Ejecutar desde la terminal
```bash
java -jar target/AutomatasSimulador-1.0-SNAPSHOT.jar
```
