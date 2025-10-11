package com.ProyectoPrograIII.prograIII_TP.service;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementación del recorrido en profundidad (DFS) sobre un grafo.
 */
@Component
public class DFSService {
  /**
   * Ejecuta DFS desde 'inicio' y devuelve el orden de visita.
   */
  public List<String> dfs(Map<String, List<Map.Entry<String, Double>>> g, String inicio) {
    var orden = new ArrayList<String>();
    var visitado = new HashSet<String>();
    dfsRec(inicio, g, visitado, orden);
    return orden;
  }

  /**
   * Paso recursivo: visita vecinos no visitados en profundidad.
   */
  private void dfsRec(String u, Map<String, List<Map.Entry<String, Double>>> g,
                      Set<String> vis, List<String> ord) {
    if (u == null || vis.contains(u) || !g.containsKey(u)) return;
    vis.add(u); ord.add(u);
    for (var e : g.getOrDefault(u, List.of())) dfsRec(e.getKey(), g, vis, ord);
  }
}
