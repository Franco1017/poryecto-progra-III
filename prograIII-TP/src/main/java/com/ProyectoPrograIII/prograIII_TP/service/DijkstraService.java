package com.ProyectoPrograIII.prograIII_TP.service;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DijkstraService {
  public Map<String,Object> dijkstra(Map<String, List<Map.Entry<String, Double>>> g, String origen, String destino) {
    var dist = new HashMap<String, Double>();
    var previo = new HashMap<String, String>();
    for (String v : g.keySet()) dist.put(v, Double.POSITIVE_INFINITY);
    if (!g.containsKey(origen)) return Map.of("distancia", Double.POSITIVE_INFINITY, "camino", List.of());
    dist.put(origen, 0.0);

    var pq = new PriorityQueue<String>(Comparator.comparingDouble(dist::get));
    pq.add(origen);

    while(!pq.isEmpty()){
      String u = pq.poll();
      if (u.equals(destino)) break;
      for (var e : g.getOrDefault(u, List.of())) {
        String v = e.getKey(); double w = e.getValue();
        double alt = dist.get(u) + w;
        if (alt < dist.getOrDefault(v, Double.POSITIVE_INFINITY)) {
          dist.put(v, alt); previo.put(v, u);
          pq.remove(v); pq.add(v);
        }
      }
    }
    var camino = new LinkedList<String>();
    if (origen.equals(destino)) return Map.of("distancia", 0, "camino", List.of(origen));
    String cur = destino;
    if (!previo.containsKey(cur)) return Map.of("distancia", Double.POSITIVE_INFINITY, "camino", List.of());
    camino.addFirst(cur);
    while(previo.containsKey(cur)) { cur = previo.get(cur); camino.addFirst(cur); }
    return Map.of("distancia", dist.getOrDefault(destino, Double.POSITIVE_INFINITY), "camino", camino);
  }
}
