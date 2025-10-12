package com.ProyectoPrograIII.prograIII_TP.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.ProyectoPrograIII.prograIII_TP.model.Producto;

public interface ProductoRepositorio extends Neo4jRepository<Producto, Long> {
  Optional<Producto> findBySku(String sku);

  // Fallback default implementation in case Spring Data derived query isn't available
  // This scans existing entities and returns the first matching SKU. It's O(N) but
  // safe and keeps services working during refactors.
  default Optional<Producto> findBySkuFallback(String sku) {
    for (Producto p : findAll()) {
      if (sku != null && sku.equals(p.getSku())) return Optional.of(p);
    }
    return Optional.empty();
  }
}
