package com.ProyectoPrograIII.prograIII_TP.model;

import org.springframework.data.neo4j.core.schema.*;

/** Relación CAMINO con peso (distancia/tiempo/costo) */
@RelationshipProperties
public class Camino {
  @Id @GeneratedValue
  private Long id;

  @TargetNode
  private Ciudad destino;

  @Property("peso")
  private double peso;

  public Camino() {}

  public Camino(Ciudad destino, double peso) {
    this.destino = destino;
    this.peso = peso;
  }

  public Camino(Long id, Ciudad destino, double peso) {
    this.id = id;
    this.destino = destino;
    this.peso = peso;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Ciudad getDestino() { return destino; }
  public void setDestino(Ciudad destino) { this.destino = destino; }

  public double getPeso() { return peso; }
  public void setPeso(double peso) { this.peso = peso; }
}
