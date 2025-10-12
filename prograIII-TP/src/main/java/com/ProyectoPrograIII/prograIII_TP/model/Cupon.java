package com.ProyectoPrograIII.prograIII_TP.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa un cupón/promoción aplicable sobre un carrito.
 * Clase de dominio simple (POJO). Si en el futuro se desea persistir, se puede
 * anotar con @Node("Cupon") y crear un repositorio.
 */
public class Cupon {
  private Long id;
  private String codigo;
  private double porcentaje; // entre 0..1
  private Set<String> incompatibles = new HashSet<>();

  public Cupon() {}

  public Cupon(String codigo, double porcentaje) {
    this.codigo = codigo;
    this.porcentaje = porcentaje;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getCodigo() { return codigo; }
  public void setCodigo(String codigo) { this.codigo = codigo; }

  public double getPorcentaje() { return porcentaje; }
  public void setPorcentaje(double porcentaje) { this.porcentaje = porcentaje; }

  public Set<String> getIncompatibles() { return incompatibles; }
  public void setIncompatibles(Set<String> incompatibles) { this.incompatibles = incompatibles; }

  public boolean aplicaSobre(double total) { return total > 0; }
  public double descuento(double total) { return total * porcentaje; }
  public boolean conflictaCon(Cupon otro) {
    return incompatibles.contains(otro.getCodigo()) || otro.getIncompatibles().contains(this.codigo);
  }
}
