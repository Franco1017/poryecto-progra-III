package com.ProyectoPrograIII.prograIII_TP.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ProyectoPrograIII.prograIII_TP.model.Producto;
import com.ProyectoPrograIII.prograIII_TP.repository.ProductoRepositorio;

/**
 * Servicio auxiliar para centralizar la búsqueda de productos por SKU.
 * Provee una única API para obtener un {@link Producto} usando el repositorio
 * o, si es necesario, un fallback que recorre los nodos existentes.
 */
@Service
public class ProductoLookupService {
  private final ProductoRepositorio repo;

  public ProductoLookupService(ProductoRepositorio repo) {
    this.repo = repo;
  }

  public Optional<Producto> findBySku(String sku) {
    Optional<Producto> first = repo.findBySku(sku);
    if (first != null && first.isPresent()) return first;
    return repo.findBySkuFallback(sku);
  }

  public Producto getBySkuOrThrow(String sku) {
    return findBySku(sku).orElseThrow(() -> new RuntimeException("SKU not found: " + sku));
  }
}
