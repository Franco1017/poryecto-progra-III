package com.ProyectoPrograIII.prograIII_TP.service;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementación del algoritmo de Prim para árbol de expansión mínima (MST).
 * Trabaja sobre el grafo no dirigido equivalente al dirigido almacenado.
 */
@Component
public class PrimService {
  /**
   * Calcula el MST y retorna costo total y lista de aristas seleccionadas.
   */
  public Map<String,Object> prim(Map<String, List<Map.Entry<String, Double>>> dirigido) {
    var g = new HashMap<String, List<Map.Entry<String, Double>>>();
    dirigido.forEach((u, lst) -> {
      g.putIfAbsent(u, new ArrayList<>(lst));
      for (var e : lst) {
        g.putIfAbsent(e.getKey(), new ArrayList<>());
        boolean existe = g.get(e.getKey()).stream().anyMatch(p -> p.getKey().equals(u));
        if (!existe) g.get(e.getKey()).add(Map.entry(u, e.getValue()));
      }
    });
    if (g.isEmpty()) return Map.of("costo", 0, "aristas", List.of());

    String inicio = g.keySet().iterator().next();
    var enMST = new HashSet<String>();
    var aristas = new ArrayList<Map<String,Object>>();
    double total = 0;

    class Arista { String u,v; double w; Arista(String u,String v,double w){this.u=u;this.v=v;this.w=w;} }
    var pq = new PriorityQueue<Arista>(Comparator.comparingDouble(a -> a.w));

    enMST.add(inicio);
    g.getOrDefault(inicio, List.of()).forEach(e -> pq.add(new Arista(inicio, e.getKey(), e.getValue())));

    while(enMST.size() < g.size() && !pq.isEmpty()){
      Arista a = pq.poll();
      if (enMST.contains(a.v)) continue;
      enMST.add(a.v);
      aristas.add(Map.of("desde", a.u, "hasta", a.v, "peso", a.w));
      total += a.w;
      for (var e : g.getOrDefault(a.v, List.of()))
        if (!enMST.contains(e.getKey()))
          pq.add(new Arista(a.v, e.getKey(), e.getValue()));
    }
    return Map.of("costo", total, "aristas", aristas);
  }
}
