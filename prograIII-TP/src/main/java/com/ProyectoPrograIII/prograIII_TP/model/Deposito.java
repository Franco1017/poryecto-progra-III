package com.ProyectoPrograIII.prograIII_TP.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Nodo Deposito en Neo4j.
 * - codigo: identificador del depósito (por ejemplo, "DEP1").
 * - ciudad: relación UBICADO_EN hacia una Ciudad.
 */
@Node("Deposito")
public class Deposito {
  @Id @GeneratedValue
  private Long id;

  @Property("codigo")
  private String codigo;

  @Relationship(type="UBICADO_EN", direction = Relationship.Direction.OUTGOING)
  private Ciudad ciudad;

  public Deposito() {}

  public Deposito(String codigo, Ciudad ciudad) {
    this.codigo = codigo;
    this.ciudad = ciudad;
  }

  public Deposito(Long id, String codigo, Ciudad ciudad) {
    this.id = id;
    this.codigo = codigo;
    this.ciudad = ciudad;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getCodigo() { return codigo; }
  public void setCodigo(String codigo) { this.codigo = codigo; }

  public Ciudad getCiudad() { return ciudad; }
  public void setCiudad(Ciudad ciudad) { this.ciudad = ciudad; }
}
