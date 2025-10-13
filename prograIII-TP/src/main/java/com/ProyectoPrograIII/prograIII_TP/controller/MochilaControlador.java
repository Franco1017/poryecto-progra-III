package com.ProyectoPrograIII.prograIII_TP.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoPrograIII.prograIII_TP.service.MochilaServicio;

@RestController
@RequestMapping("/mochila")
public class MochilaControlador {
  private final MochilaServicio servicio;

  public MochilaControlador(MochilaServicio servicio) { this.servicio = servicio; }

  @PostMapping("/resolver")
  public Map<String,Object> resolver(@RequestBody ResolverReq req) {
    return servicio.knapsackByBudget(req.skus, req.budget);
  }

  public static class ResolverReq {
    public List<String> skus;
    public double budget;
  }
}
