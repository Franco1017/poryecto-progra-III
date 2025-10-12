package com.ProyectoPrograIII.prograIII_TP.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import com.ProyectoPrograIII.prograIII_TP.model.Producto;

/**
 * Servicio que implementa búsqueda por backtracking (retroceso) para generar promociones
 * (conjuntos de productos) que cumplan restricciones simples como rango de precio y
 * número máximo de ítems.
 *
 * <p>Este servicio realiza una enumeración recursiva (include/exclude) con poda temprana
 * (cuando la suma supera el máximo) y limita el tamaño de las combinaciones por
 * {@code maxItems}.
 */
@Service
public class PromocionesServicio {
  private final Neo4jClient neo4j;

  public PromocionesServicio(Neo4jClient neo4j) {
    this.neo4j = neo4j;
  }

  /**
   * Encuentra todas las combinaciones de productos (por SKU) dentro de un rango de precio
   * total [minTotal, maxTotal] y con a lo sumo {@code maxItems}. Usa búsqueda por backtracking.
   *
   * @param skus lista de SKUs candidatos (orden preservado para evitar duplicados equivalentes)
   * @param minTotal precio total mínimo incluido
   * @param maxTotal precio total máximo incluido
   * @param maxItems máximo número de productos en la combinación
   * @return lista de bundles (cada bundle es lista de SKUs)
   */
  public List<List<String>> findBundlesByPriceRange(List<String> skus, double minTotal, double maxTotal, int maxItems) {
    // Cargar productos y precios en el mismo orden
    List<Producto> products = new ArrayList<>();
    for (String s : skus) {
      Producto p = findBySkuOrThrow(s);
      products.add(p);
    }

    List<List<String>> results = new ArrayList<>();
    List<String> current = new ArrayList<>();

    backtrack(products, 0, 0.0, minTotal, maxTotal, maxItems, current, results);
    return results;
  }

  // Helper: query Neo4j directly for a product by sku and construct a Producto POJO.
  private Producto findBySkuOrThrow(String sku) {
    var rows = neo4j.query("""
      MATCH (p:Producto {sku:$sku})
      RETURN p.sku AS sku, p.precio AS precio
    """).bindAll(Map.of("sku", sku)).fetch().all();

  if (rows.isEmpty()) throw new RuntimeException("SKU not found: " + sku);
  @SuppressWarnings("unchecked")
  Map<String,Object> row = (Map<String,Object>)rows.iterator().next();
  Producto p = new Producto();
  p.setSku((String)row.get("sku"));
  Object precio = row.get("precio");
    if (precio instanceof Number) p.setPrecio(((Number)precio).doubleValue());
    return p;
  }

  // Recorrido recursivo: intentar incluir o excluir el producto i
  private void backtrack(List<Producto> products, int idx, double sum, double minTotal, double maxTotal, int maxItems,
                         List<String> current, List<List<String>> results) {
    if (sum > maxTotal) return; // poda: ya superamos el máximo
    if (current.size() > maxItems) return; // poda por tamaño

    // Si suma válida y no vacía, aceptar la combinación
    if (sum >= minTotal && sum <= maxTotal && !current.isEmpty()) {
      results.add(new ArrayList<>(current));
    }

    if (idx >= products.size()) return;

    // Opción 1: incluir products[idx]
    Producto p = products.get(idx);
    current.add(p.getSku());
    backtrack(products, idx + 1, sum + p.getPrecio(), minTotal, maxTotal, maxItems, current, results);
    current.remove(current.size() - 1);

    // Opción 2: excluir products[idx]
    backtrack(products, idx + 1, sum, minTotal, maxTotal, maxItems, current, results);
  }
}
