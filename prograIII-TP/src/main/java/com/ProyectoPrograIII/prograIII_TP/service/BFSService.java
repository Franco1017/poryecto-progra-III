package com.ProyectoPrograIII.prograIII_TP.service;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BFSService {
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
