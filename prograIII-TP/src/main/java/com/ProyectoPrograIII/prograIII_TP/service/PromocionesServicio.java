package com.ProyectoPrograIII.prograIII_TP.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ProyectoPrograIII.prograIII_TP.model.Cupon;

@Service
public class PromocionesServicio {

  // ---------- DP: Bundle óptimo (Knapsack 0/1) ----------
  public Map<String,Object> bundleOptimo(List<Item> items, int presupuesto) {
    int n = items.size();
    int[][] dp = new int[n+1][presupuesto+1];

    for(int i=1;i<=n;i++){
      int w = (int) items.get(i-1).precio; // precio como “peso”
      int v = items.get(i-1).valor;
      for(int cap=0; cap<=presupuesto; cap++){
        dp[i][cap] = dp[i-1][cap];
        if (w<=cap) dp[i][cap] = Math.max(dp[i][cap], dp[i-1][cap-w]+v);
      }
    }
    // reconstrucción
    int cap=presupuesto; List<String> seleccion=new ArrayList<>();
    for(int i=n;i>=1;i--){
      if (dp[i][cap]!=dp[i-1][cap]){
        seleccion.add(items.get(i-1).sku);
        cap -= (int)items.get(i-1).precio;
      }
    }
    Collections.reverse(seleccion);
    int total = seleccion.stream().mapToInt(sku -> (int)items.stream().filter(i -> i.sku.equals(sku)).findFirst().get().precio).sum();
    return Map.of("valorMaximo", dp[n][presupuesto], "total", total, "seleccion", seleccion);
  }

  // ---------- Backtracking: cupones/no compatibles ----------
  public Map<String,Object> aplicarCupones(List<Cupon> cupones, double totalCarrito) {
    // objetivo: maximizar descuento sin usar cupones incompatibles
    List<Cupon> mejor = new ArrayList<>();
    double[] mejorDesc = new double[] { 0.0 };

    backtrackCuponesHelper(0, new ArrayList<>(), 0.0, cupones, totalCarrito, mejor, mejorDesc);

    return Map.of("descuento", mejorDesc[0], "cupones", mejor.stream().map(x->x.getCodigo()).toList(),
      "totalConDescuento", totalCarrito - mejorDesc[0]);
  }
  
  // Helper recursive backtracking moved out of method to be valid Java
  private void backtrackCuponesHelper(int idx, List<Cupon> actual, double descAct, List<Cupon> all, double total,
                                      List<Cupon> mejor, double[] mejorDesc) {
    if (idx == all.size()) {
      if (descAct > mejorDesc[0]) {
        mejorDesc[0] = descAct;
        mejor.clear();
        mejor.addAll(actual);
      }
      return;
    }
    Cupon c = all.get(idx);
    // Omitir
    backtrackCuponesHelper(idx + 1, actual, descAct, all, total, mejor, mejorDesc);
    // Tomar si no incompatible
    if (compatible(actual, c) && c.aplicaSobre(total)) {
      actual.add(c);
      backtrackCuponesHelper(idx + 1, actual, descAct + c.descuento(total), all, total, mejor, mejorDesc);
      actual.remove(actual.size() - 1);
    }
  }
  private boolean compatible(List<Cupon> sel, Cupon nuevo){
    for (Cupon c: sel) if (c.conflictaCon(nuevo)) return false; return true;
  }

  // ---------- Fuerza bruta: cross-sell elegir K de N ----------
  public List<String> mejorCrossSell(List<Candidato> candidatos, int k) {
    // si N chico: probar todas las combinaciones, maximizar score
    // combinaciones simples (nCk) – recursión para generar todas las combinaciones
    return combinaciones(candidatos, k).stream()
      .max(Comparator.comparingDouble(this::scoreCombo))
      .map(combo -> combo.stream().map(c->c.sku).toList())
      .orElse(List.of());
  }
  private double scoreCombo(List<Candidato> combo){
    return combo.stream().mapToDouble(c -> c.score).sum();
  }

  // Generate all combinations (choose k from candidates)
  private List<List<Candidato>> combinaciones(List<Candidato> candidatos, int k) {
    List<List<Candidato>> result = new ArrayList<>();
    combinacionesRec(0, k, candidatos, new ArrayList<>(), result);
    return result;
  }

  private void combinacionesRec(int idx, int remaining, List<Candidato> cand, List<Candidato> cur, List<List<Candidato>> out) {
    if (remaining == 0) { out.add(new ArrayList<>(cur)); return; }
    if (idx >= cand.size()) return;
    // take
    cur.add(cand.get(idx));
    combinacionesRec(idx+1, remaining-1, cand, cur, out);
    cur.remove(cur.size()-1);
    // skip
    combinacionesRec(idx+1, remaining, cand, cur, out);
  }

  // ----- DTOs internos -----
  public record Item(String sku, double precio, int valor){}
  public record Candidato(String sku, double score){}
}
