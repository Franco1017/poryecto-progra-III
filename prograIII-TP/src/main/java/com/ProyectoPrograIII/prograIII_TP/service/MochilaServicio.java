package com.ProyectoPrograIII.prograIII_TP.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ProyectoPrograIII.prograIII_TP.model.Producto;

@Service
public class MochilaServicio {
  private final ProductoLookupService lookup;

  public MochilaServicio(ProductoLookupService lookup) { this.lookup = lookup; }

  /**
   * Simple 0/1 knapsack where item weight = price and value = price.
   * Budget and prices are treated as rounded integer units.
   */
  public Map<String,Object> knapsackByBudget(List<String> skus, double budget) {
    List<Item> items = new ArrayList<>();
    for (String sku : skus) {
      Producto p = lookup.getBySkuOrThrow(sku);
      int precio = (int)Math.round(p.getPrecio());
      items.add(new Item(sku, precio));
    }

    int n = items.size();
    int W = (int)Math.round(budget);
    if (W < 0) W = 0;

    int[][] dp = new int[n+1][W+1];
    for (int i = 1; i <= n; i++) {
      int w = items.get(i-1).precio;
      int v = items.get(i-1).precio; // value == price
      for (int cap = 0; cap <= W; cap++) {
        dp[i][cap] = dp[i-1][cap];
        if (w <= cap) dp[i][cap] = Math.max(dp[i][cap], dp[i-1][cap-w] + v);
      }
    }

    // reconstruct
    int cap = W;
    List<String> seleccion = new ArrayList<>();
    for (int i = n; i >= 1; i--) {
      if (dp[i][cap] != dp[i-1][cap]) {
        seleccion.add(items.get(i-1).sku);
        cap -= items.get(i-1).precio;
      }
    }
    Collections.reverse(seleccion);
    int total = seleccion.stream().mapToInt(s -> items.stream().filter(it -> it.sku.equals(s)).findFirst().get().precio).sum();

    return Map.of("valorMaximo", dp[n][W], "total", total, "seleccion", seleccion);
  }

  private record Item(String sku, int precio) {}
}
