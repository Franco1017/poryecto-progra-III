package com.ProyectoPrograIII.prograIII_TP.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * Nodo Categoria en Neo4j.
 * - nombre: nombre legible de la categoría (por ejemplo, "Electrónica").
 */
@Node("Categoria")
public class Categoria {
  @Id @GeneratedValue
  private Long id;

  @Property("nombre")
  private String nombre;

  public Categoria() {}

  public Categoria(String nombre) {
    this.nombre = nombre;
  }

  public Categoria(Long id, String nombre) {
    this.id = id;
    this.nombre = nombre;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
}
