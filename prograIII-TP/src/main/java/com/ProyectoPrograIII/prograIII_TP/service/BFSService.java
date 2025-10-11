package com.ProyectoPrograIII.prograIII_TP.service;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementación del recorrido en anchura (BFS) sobre un grafo dirigido con pesos.
 * Ignora los pesos para el recorrido; retorna el orden de visita.
 */
@Component
public class BFSService {
  /**
   * Ejecuta BFS desde 'inicio' y devuelve el orden de visita.
   */
  public List<String> bfs(Map<String, List<Map.Entry<String, Double>>> g, String inicio) {
    var visitado = new HashSet<String>();
    var cola = new ArrayDeque<String>();
    var orden = new ArrayList<String>();
    if (!g.containsKey(inicio)) return orden;
    visitado.add(inicio); cola.add(inicio);
    while(!cola.isEmpty()){
      String u = cola.poll();
      orden.add(u);
      for (var e : g.getOrDefault(u, List.of())) {
        if (visitado.add(e.getKey())) cola.add(e.getKey());
      }
    }
    return orden;
  }
}
