package com.ProyectoPrograIII.prograIII_TP.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoPrograIII.prograIII_TP.service.LogisticaServicio;

@RestController
@RequestMapping("/logistica")
public class LogisticaControlador {
  private final LogisticaServicio servicio;

  public LogisticaControlador(LogisticaServicio servicio) { this.servicio = servicio; }

  @GetMapping("/ruta")
  public Map<String,Object> rutaMasBarata(@RequestParam String origen, @RequestParam String destino) {
    return servicio.rutaMasBarataOTiempo(origen, destino);
  }

  @GetMapping("/cobertura")
  public List<String> cobertura(@RequestParam String ciudadInicio, @RequestParam(defaultValue = "2") int saltos) {
    return servicio.cobertura(ciudadInicio, saltos);
  }

  @GetMapping("/mst")
  public Map<String,Object> mst(@RequestParam List<String> ciudades) { return servicio.mstSobreCiudades(ciudades); }

  @PostMapping("/tsp")
  public Map<String,Object> tsp(@RequestBody List<String> nodos) { return servicio.tspBruteForce(nodos); }
}
