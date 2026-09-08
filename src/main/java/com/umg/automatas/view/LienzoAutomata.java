/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umg.automatas.view;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;

import com.umg.automatas.controller.ControladorPrincipal;
import com.umg.automatas.model.Automata;
import com.umg.automatas.model.Estado;
import com.umg.automatas.model.Transicion;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class LienzoAutomata extends JPanel {

    private final mxGraph graficoJGraphX;
    private final mxGraphComponent graphComponent;
    private Automata automata;
    ControladorPrincipal controlador;

    private final Map<Estado, Object> celdaEstados = new HashMap<>();
    private final Map<Object, Estado> estadoPorCelda = new HashMap<>();
    private final Map<Transicion, Object> celdaTransiciones = new HashMap<>();
    private final Map<Object, Transicion> transicionPorCelda = new HashMap<>();

    private Object celdaResaltadaActual;
    private Estado estadoResaltadoActual;

    public LienzoAutomata(ControladorPrincipal controlador) {
        super(new BorderLayout());
        this.controlador = controlador;

        graficoJGraphX = new mxGraph();
        graficoJGraphX.setCellsEditable(false);
        graficoJGraphX.setCellsResizable(false);
        graficoJGraphX.setCellsDisconnectable(false);
        graficoJGraphX.setDropEnabled(false);
        graficoJGraphX.setSplitEnabled(false);

        graphComponent = new mxGraphComponent(graficoJGraphX);
        graphComponent.setConnectable(false);
        graphComponent.getGraphHandler().setCloneEnabled(false);
        add(graphComponent, BorderLayout.CENTER);

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                onClicLienzo(evento);
            }
        });

        graficoJGraphX.addListener(mxEvent.CELLS_MOVED, (Object sender, com.mxgraph.util.mxEventObject evt) -> {
            Object[] celdas = (Object[]) evt.getProperty("cells");
            if (celdas == null) return;
            for (Object celda : celdas) {
                Estado estado = estadoPorCelda.get(celda);
                if (estado == null) continue;
                mxGeometry geo = graficoJGraphX.getModel().getGeometry(celda);
                if (geo != null) {
                    estado.setPosicion(geo.getX(), geo.getY());
                }
            }
        });
    }

    public void setAutomata(Automata automata) {
        this.automata = automata;
        limpiarLienzo();
        for (Estado e : automata.getEstados()) {
            dibujarEstado(e);
        }
        for (Transicion t : automata.getTransiciones()) {
            dibujarTransicion(t);
        }
    }

    public void dibujarEstado(Estado e) {
        Object padre = graficoJGraphX.getDefaultParent();
        graficoJGraphX.getModel().beginUpdate();
        try {
            Object celda = graficoJGraphX.insertVertex(padre, e.getId(), e.getEtiqueta(),
                    e.getPosicion().getX(), e.getPosicion().getY(), 50, 50, construirEstilo(e));
            celdaEstados.put(e, celda);
            estadoPorCelda.put(celda, e);
        } finally {
            graficoJGraphX.getModel().endUpdate();
        }
    }

    public void dibujarTransicion(Transicion t) {
        Object padre = graficoJGraphX.getDefaultParent();
        Object origen = celdaEstados.get(t.getOrigen());
        Object destino = celdaEstados.get(t.getDestino());
        if (origen == null || destino == null) return;

        graficoJGraphX.getModel().beginUpdate();
        try {
            Object arista = graficoJGraphX.insertEdge(padre, null, t.getSimbolo(), origen, destino);
            celdaTransiciones.put(t, arista);
            transicionPorCelda.put(arista, t);
        } finally {
            graficoJGraphX.getModel().endUpdate();
        }
    }

    public void resaltarEstado(Estado e) {
        limpiarResaltadoAnterior();
        Object celda = celdaEstados.get(e);
        if (celda == null) return;
        graficoJGraphX.getModel().beginUpdate();
        try {
            graficoJGraphX.setCellStyle(construirEstiloResaltado(e), new Object[]{celda});
        } finally {
            graficoJGraphX.getModel().endUpdate();
        }
        celdaResaltadaActual = celda;
        estadoResaltadoActual = e;
    }

    public void limpiarResaltado() {
        limpiarResaltadoAnterior();
        celdaResaltadaActual = null;
        estadoResaltadoActual = null;
    }

    private void limpiarResaltadoAnterior() {
        if (celdaResaltadaActual == null || estadoResaltadoActual == null) return;
        graficoJGraphX.getModel().beginUpdate();
        try {
            graficoJGraphX.setCellStyle(construirEstilo(estadoResaltadoActual), new Object[]{celdaResaltadaActual});
        } finally {
            graficoJGraphX.getModel().endUpdate();
        }
    }

    private String construirEstilo(Estado e) {
        String forma = e.esFinal() ? "shape=doubleEllipse;" : "shape=ellipse;";
        String borde = e.esInicial() ? "strokeColor=#2E7D32;strokeWidth=3;" : "strokeColor=#1565C0;strokeWidth=1;";
        return forma + "fillColor=#E3F2FD;" + borde;
    }

    private String construirEstiloResaltado(Estado e) {
        String forma = e.esFinal() ? "shape=doubleEllipse;" : "shape=ellipse;";
        String borde = e.esInicial() ? "strokeColor=#2E7D32;strokeWidth=3;" : "strokeColor=#F57F17;strokeWidth=1;";
        return forma + "fillColor=#FFF176;" + borde;
    }

    public void limpiarLienzo() {
        Object padre = graficoJGraphX.getDefaultParent();
        graficoJGraphX.getModel().beginUpdate();
        try {
            graficoJGraphX.removeCells(graficoJGraphX.getChildVertices(padre));
        } finally {
            graficoJGraphX.getModel().endUpdate();
        }
        celdaEstados.clear();
        estadoPorCelda.clear();
        celdaTransiciones.clear();
        transicionPorCelda.clear();
        celdaResaltadaActual = null;
        estadoResaltadoActual = null;
    }

    public Transicion getTransicionEnCelda(Object celda) {
        return transicionPorCelda.get(celda);
    }

    public void eliminarAristaVisual(Transicion t) {
        Object celda = celdaTransiciones.get(t);
        if (celda == null) return;
        graficoJGraphX.getModel().beginUpdate();
        try {
            graficoJGraphX.removeCells(new Object[]{celda});
        } finally {
            graficoJGraphX.getModel().endUpdate();
        }
        celdaTransiciones.remove(t);
        transicionPorCelda.remove(celda);
    }

    private void onClicLienzo(MouseEvent evento) {
        Object celda = graphComponent.getCellAt(evento.getX(), evento.getY());
        if (celda == null) {
            if (evento.getClickCount() == 2) {
                controlador.crearEstadoEn(evento.getX(), evento.getY());
            }
            return;
        }
        if (celda instanceof mxCell) {
            mxCell mxc = (mxCell) celda;
            if (mxc.isVertex() && evento.getClickCount() == 2) {
                controlador.eliminarEstadoEn(mxc);
                return;
            }
            controlador.seleccionarCelda(mxc, evento);
        }
    }
}
