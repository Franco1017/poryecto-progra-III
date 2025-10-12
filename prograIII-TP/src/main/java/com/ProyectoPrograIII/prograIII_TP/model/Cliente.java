package com.ProyectoPrograIII.prograIII_TP.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Nodo Cliente en Neo4j.
 * - email: identificador de contacto.
 * - ciudad: relación VIVE_EN hacia una Ciudad.
 */
@Node("Cliente")
public class Cliente {
  @Id @GeneratedValue
  private Long id;

  @Property("email")
  private String email;

  @Relationship(type="VIVE_EN", direction = Relationship.Direction.OUTGOING)
  private Ciudad ciudad;

  public Cliente() {}

  public Cliente(String email, Ciudad ciudad) {
    this.email = email;
    this.ciudad = ciudad;
  }

  public Cliente(Long id, String email, Ciudad ciudad) {
    this.id = id;
    this.email = email;
    this.ciudad = ciudad;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public Ciudad getCiudad() { return ciudad; }
  public void setCiudad(Ciudad ciudad) { this.ciudad = ciudad; }
}
