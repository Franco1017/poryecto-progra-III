package com.ProyectoPrograIII.prograIII_TP.service;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DFSService {
  public List<String> dfs(Map<String, List<Map.Entry<String, Double>>> g, String inicio) {
    var orden = new ArrayList<String>();
    var visitado = new HashSet<String>();
    dfsRec(inicio, g, visitado, orden);
    return orden;
  }

  private void dfsRec(String u, Map<String, List<Map.Entry<String, Double>>> g,
                      Set<String> vis, List<String> ord) {
    if (u == null || vis.contains(u) || !g.containsKey(u)) return;
    vis.add(u); ord.add(u);
    for (var e : g.getOrDefault(u, List.of())) dfsRec(e.getKey(), g, vis, ord);
  }
}
