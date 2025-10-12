package com.ProyectoPrograIII.prograIII_TP.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Nodo Producto en Neo4j.
 * - sku: identificador de producto.
 * - precio: valor numérico del producto.
 * - categoria: relación PERTENECE_A hacia una Categoria.
 */
@Node("Producto")
public class Producto {
  @Id @GeneratedValue
  private Long id;

  @Property("sku")
  private String sku;

  @Property("precio")
  private double precio;

  @Relationship(type="PERTENECE_A", direction = Relationship.Direction.OUTGOING)
  private Categoria categoria;

  public Producto() {}

  public Producto(String sku, double precio, Categoria categoria) {
    this.sku = sku;
    this.precio = precio;
    this.categoria = categoria;
  }

  public Producto(Long id, String sku, double precio, Categoria categoria) {
    this.id = id;
    this.sku = sku;
    this.precio = precio;
    this.categoria = categoria;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getSku() { return sku; }
  public void setSku(String sku) { this.sku = sku; }

  public double getPrecio() { return precio; }
  public void setPrecio(double precio) { this.precio = precio; }

  public Categoria getCategoria() { return categoria; }
  public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
