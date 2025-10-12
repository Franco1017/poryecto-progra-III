package com.ProyectoPrograIII.prograIII_TP.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import com.ProyectoPrograIII.prograIII_TP.model.Producto;


/**
 * Servicio para gestión del catálogo de productos.
 *
 * <p>Responsabilidades principales:
 * - Crear productos y categorías (persistencia en Neo4j via Spring Data).
 * - Crear relaciones de similitud entre productos (ejecutando Cypher con {@link Neo4jClient}).
 * - Consultas convenientes sobre el grafo de productos (vecinos cercanos, árbol de categoría),
 *   implementadas mediante consultas Cypher.
 *
 * <p>Notas de diseño:
 * - Evita dependencias a servicios no existentes (AdyacenciaServicio) y a Lombok: usa POJOs
 *   para crear/guardar entidades.
 * - Si en el futuro se desea ejecutar algoritmos de grafo (BFS/DFS/Dijkstra) sobre el grafo
 *   de productos, se puede reusar los servicios {@link AdyacenciaBuilder}, {@link com.ProyectoPrograIII.prograIII_TP.service.DijkstraService}
 *   y {@link com.ProyectoPrograIII.prograIII_TP.service.BFSService} construyendo una adyacencia de productos
 *   (con una clase similar a {@code AdyacenciaBuilder} pero para Producto).
 */
@Service
public class CatalogoServicio {
  private final Neo4jClient neo4j; // para queries Cypher puntuales

  public CatalogoServicio(Neo4jClient neo4j) {
    this.neo4j = neo4j;
  }

  /**
   * Crea un producto persistiendo la categoría si no existe.
   * Usa los POJOs del modelo (sin Lombok builders).
   */
  public Producto crearProducto(String sku, double precio, String categoria) {
    // Creación con Cypher: MERGE la categoría y el producto, y la relación PERTENECE_A
    neo4j.query("""
      MERGE (c:Categoria {nombre:$cat})
      MERGE (p:Producto {sku:$sku})
      SET p.precio = $precio
      MERGE (p)-[:PERTENECE_A]->(c)
      RETURN p.sku AS sku, p.precio AS precio
    """).bindAll(Map.of("cat", categoria, "sku", sku, "precio", precio)).fetch().all();
    // Retornamos un objeto Producto simple para compatibilidad con la API
    Producto p = new Producto(); p.setSku(sku); p.setPrecio(precio);
    return p;
  }

  /**
   * Crea o actualiza una relación SIMILAR_A entre dos productos con un peso de afinidad.
   * Ejecuta Cypher directamente mediante {@link Neo4jClient} para mantener control sobre
   * las relaciones (bidireccional en este ejemplo).
   */
  public void similar(String skuA, String skuB, double afinidad) {
    neo4j.query("""
      MATCH (a:Producto {sku:$a}),(b:Producto {sku:$b})
      MERGE (a)-[r:SIMILAR_A]->(b) SET r.peso=$w
      MERGE (b)-[r2:SIMILAR_A]->(a) SET r2.peso=$w
    """).bindAll(Map.of("a", skuA, "b", skuB, "w", afinidad)).run();
  }

  /**
   * Retorna los vecinos alcanzables desde un producto hasta una profundidad dada.
   *
   * <p>Esta implementación usa APOC (apoc.path.expandConfig) para limitar niveles; si
   * APOC no está disponible en tu servidor Neo4j, reemplazar por una consulta recursiva o
   * por la construcción de adyacencia y ejecución de BFS local en memoria.
   */
  public List<String> vecinosCercanos(String sku, int profundidad) {
    var res = neo4j.query("""
      MATCH (start:Producto {sku:$sku})
      CALL apoc.path.expandConfig(start, {relationshipFilter:'SIMILAR_A>', maxLevel:$max})
      YIELD path
      WITH DISTINCT last(nodes(path)) as p
      RETURN p.sku AS sku
    """).bindAll(Map.of("sku", sku, "max", profundidad))
    .fetch().all();

    return res.stream().map(m -> (String)m.get("sku")).toList();
  }

  /**
   * Consulta productos pertenecientes a una raíz de categoría (ej. subcategorías hasta N niveles).
   * Devuelve la lista de SKUs.
   */
  public List<String> arbolCategoria(String nombreRaiz) {
    var rows = neo4j.query("""
      MATCH (c:Categoria {nombre:$raiz})<-[:PERTENECE_A*0..2]-(p:Producto)
      RETURN DISTINCT p.sku AS sku
    """).bindAll(Map.of("raiz", nombreRaiz)).fetch().all();
    return rows.stream().map(m -> (String)m.get("sku")).toList();
  }


}
