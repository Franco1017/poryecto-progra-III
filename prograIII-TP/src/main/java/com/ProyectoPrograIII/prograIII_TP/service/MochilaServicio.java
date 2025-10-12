package com.ProyectoPrograIII.prograIII_TP.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ProyectoPrograIII.prograIII_TP.model.Producto;
import com.ProyectoPrograIII.prograIII_TP.repository.ProductoRepositorio;

/**
 * Servicio que implementa una variante de Knapsack por programación dinámica.
 *
 * <p>Modelo: cada producto tiene un precio. Interpretamos el precio como peso y valor
 * expresado en centavos (multiplicando por 100) para usar DP entera. El objetivo es
 * maximizar el valor total sin exceder el presupuesto (capacidad).
 *
 * <p>Limitaciones:
 * - Convertimos precios a enteros (centavos). Si los precios tienen más precisión, adaptar.
 * - La complejidad depende de la capacidad en centavos; para presupuestos muy grandes puede
 *   ser costoso en memoria/tiempo. Para esos casos conviene usar heurísticos.
 */
@Service
public class KnapsackService {
  private final ProductoRepositorio productoRepo;

  public KnapsackService(ProductoRepositorio productoRepo) {
    this.productoRepo = productoRepo;
  }

  /**
   * Resuelve knapsack por programación dinámica: dado un conjunto de SKUs y un presupuesto
   * (double), devuelve el subconjunto de SKUs que maximiza la suma de precios sin superar
   * el presupuesto.
   *
   * @param skus lista de SKUs candidatos
   * @param budget presupuesto (double, en la misma unidad que Producto.precio)
   * @return Map con claves: "total" -> double costo total elegido, "items" -> List<String> SKUs seleccionados
   */
  public Map<String, Object> knapsackByBudget(List<String> skus, double budget) {
    int n = skus.size();
    // Convertir precios a centavos (int)
    int capacity = (int)Math.round(budget * 100);
    int[] wt = new int[n];
    int[] val = new int[n];
    List<Producto> products = new ArrayList<>();
    for (int i = 0; i < n; ++i) {
      String sku = skus.get(i);
      Producto p = productoRepo.findBySku(sku).orElseThrow(() -> new RuntimeException("SKU not found: " + sku));
      products.add(p);
      int cents = (int)Math.round(p.getPrecio() * 100);
      wt[i] = cents;
      val[i] = cents; // valor = precio en centavos
    }

    // DP table: (n+1) x (capacity+1)
    int[][] dp = new int[n+1][capacity+1];
    boolean[][] take = new boolean[n+1][capacity+1];

    for (int i = 1; i <= n; ++i) {
      int w = wt[i-1];
      int v = val[i-1];
      for (int c = 0; c <= capacity; ++c) {
        // don't take
        dp[i][c] = dp[i-1][c];
        if (c >= w) {
          int cand = dp[i-1][c-w] + v;
          if (cand > dp[i][c]) {
            dp[i][c] = cand;
            take[i][c] = true;
          }
        }
      }
    }

    // reconstruct
    int c = capacity;
    List<String> chosen = new ArrayList<>();
    for (int i = n; i >= 1; --i) {
      if (take[i][c]) {
        chosen.add(products.get(i-1).getSku());
        c -= wt[i-1];
      }
    }

    int totalCents = dp[n][capacity];
    double total = totalCents / 100.0;
    // reverse chosen to preserve original order (now reversed)
    List<String> ordered = new ArrayList<>();
    for (int i = chosen.size()-1; i >= 0; --i) ordered.add(chosen.get(i));

    return Map.of("total", total, "items", ordered);
  }
}
