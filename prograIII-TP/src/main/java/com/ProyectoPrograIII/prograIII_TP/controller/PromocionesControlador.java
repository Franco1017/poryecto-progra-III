package com.ProyectoPrograIII.prograIII_TP.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoPrograIII.prograIII_TP.model.Cupon;
import com.ProyectoPrograIII.prograIII_TP.service.PromocionesServicio;

@RestController
@RequestMapping("/promociones")
public class PromocionesControlador {
  private final PromocionesServicio servicio;

  public PromocionesControlador(PromocionesServicio servicio) { this.servicio = servicio; }

  @PostMapping("/aplicar")
  public Map<String,Object> aplicar(@RequestBody AplicarReq req) {
    return servicio.aplicarCupones(req.cupones, req.total);
  }

  public static class AplicarReq {
    public List<Cupon> cupones;
    public double total;
  }
}
