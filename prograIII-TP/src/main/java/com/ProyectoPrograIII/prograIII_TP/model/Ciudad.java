package com.ProyectoPrograIII.prograIII_TP.model;

import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

/** Nodo Ciudad en Neo4j */
@Node("Ciudad")
public class Ciudad {
  @Id @GeneratedValue
  private Long id;

  @Property("nombre")
  private String nombre;

  /** Carreteras/rutas que salen de esta ciudad (aristas dirigidas) */
  @Relationship(type = "CAMINO", direction = Relationship.Direction.OUTGOING)
  private List<Camino> caminos;

  public Ciudad() {
    
  }

  public Ciudad(String nombre) {
    this.nombre = nombre;
  }

  public Ciudad(Long id, String nombre, List<Camino> caminos) {
    this.id = id;
    this.nombre = nombre;
    this.caminos = caminos;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public List<Camino> getCaminos() { return caminos; }
  public void setCaminos(List<Camino> caminos) { this.caminos = caminos; }
}
