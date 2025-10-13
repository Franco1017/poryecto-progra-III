package com.ProyectoPrograIII.prograIII_TP.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoPrograIII.prograIII_TP.model.Producto;
import com.ProyectoPrograIII.prograIII_TP.service.CatalogoServicio;

@RestController
@RequestMapping("/catalogo")
public class CatalogoControlador {
  private final CatalogoServicio servicio;

  public CatalogoControlador(CatalogoServicio servicio) { this.servicio = servicio; }

  @PostMapping("/producto")
  public Producto crearProducto(@RequestParam String sku, @RequestParam double precio, @RequestParam String categoria) {
    return servicio.crearProducto(sku, precio, categoria);
  }

  @PostMapping("/similar")
  public void relacionarSimilar(@RequestParam String a, @RequestParam String b, @RequestParam double peso) {
    servicio.similar(a, b, peso);
  }

  @GetMapping("/vecinos/{sku}")
  public List<String> vecinos(@PathVariable String sku, @RequestParam(defaultValue = "2") int profundidad) {
    return servicio.vecinosCercanos(sku, profundidad);
  }
}
