package com.ProyectoPrograIII.prograIII_TP.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ProyectoPrograIII.prograIII_TP.model.Camino;
import com.ProyectoPrograIII.prograIII_TP.repository.CiudadRepositorio;

@Component
public class AdyacenciaBuilder {
  private final CiudadRepositorio repo;

  public AdyacenciaBuilder(CiudadRepositorio repo) {
    this.repo = repo;
  }

  public Map<String, List<Map.Entry<String, Double>>> build() {
    Map<String, List<Map.Entry<String, Double>>> g = new HashMap<>();
    repo.findAll().forEach(c -> {
      g.putIfAbsent(c.getNombre(), new ArrayList<>());
      if (c.getCaminos() != null) {
        for (Camino cam : c.getCaminos()) {
          g.get(c.getNombre()).add(Map.entry(cam.getDestino().getNombre(), cam.getPeso()));
        }
      }
    });
    return g;
  }
}
