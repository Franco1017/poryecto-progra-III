package com.ProyectoPrograIII.prograIII_TP.service;

import com.ProyectoPrograIII.prograIII_TP.repository.CiudadRepositorio;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Servicio de logística de alto nivel.
 *
 * <p>Este servicio orquesta operaciones de negocio relacionadas con rutas y cobertura
 * aprovechando los servicios de grafo ya implementados en la aplicación:
 * - {@link AdyacenciaBuilder} para construir la representación de adyacencia a partir
 *   de los nodos/relaciones en Neo4j.
 * - {@link DijkstraService} para calcular caminos mínimos entre dos ciudades (por peso).
 * - {@link PrimService} para calcular el árbol de expansión mínima (MST) sobre un subgrafo.
 *
 * <p>Responsabilidades principales:
 * - Obtener/validar ciudades origen/destino usando {@link CiudadRepositorio}.
 * - Construir la adyacencia en memoria para pasar a los algoritmos.
 * - Proveer métodos de conveniencia para lógica de negocio (ruta más barata, cobertura por
 *   saltos y MST sobre un subconjunto de ciudades).
 *
 * <p>Notas de diseño:
 * - Este servicio mantiene la lógica de negocio y delega los algoritmos puros a los servicios
 *   específicos (Dijkstra/Prim). De esa forma se evita duplicar la lógica de rutas.
 * - Actualmente el método {@link #rutaMasBarataOTiempo(String, String)} espera el nombre de
 *   la ciudad origen; si se necesita resolver desde un "código de depósito" a ciudad, puede
 *   reinyectarse {@code DepositoRepositorio} y mapear el código al nodo Ciudad antes de llamar
 *   a Dijkstra.
 */
@Service
public class LogisticaServicio {
  private final CiudadRepositorio ciudadRepo;
  private final AdyacenciaBuilder adyacenciaBuilder;
  private final DijkstraService dijkstraService;
  private final PrimService primService;

  public LogisticaServicio(CiudadRepositorio ciudadRepo,
                           AdyacenciaBuilder adyacenciaBuilder,
                           DijkstraService dijkstraService,
                           PrimService primService) {
    this.ciudadRepo = ciudadRepo;
    this.adyacenciaBuilder = adyacenciaBuilder;
    this.dijkstraService = dijkstraService;
    this.primService = primService;
  }

  /**
   * Calcula y devuelve la ruta de menor coste (por peso) entre una ciudad origen y
   * una ciudad destino.
   *
   * <p>Parámetros:
   * - {@code origenOCodigo}: en la implementación actual se interpreta como el nombre de la
   *   ciudad origen. Si se desea aceptar un código de depósito, hay que inyectar
   *   {@code DepositoRepositorio} y resolver el depósito a su ciudad antes de invocar Dijkstra.
   * - {@code ciudadDestino}: nombre de la ciudad destino.
   *
   * <p>Retorna un {@code Map} con los campos que provee {@link DijkstraService} (por ejemplo,
   * "distancia" y "camino").
   *
   * <p>Comportamiento:
   * - Valida que ambas ciudades existan (lanza RuntimeException si no).
   * - Construye la adyacencia a partir de la BD (Neo4j) llamando a {@link AdyacenciaBuilder#build()}.
   * - Delegar el cálculo a {@link DijkstraService#dijkstra(Map, String, String)}.
   */
  public Map<String,Object> rutaMasBarataOTiempo(String origenOCodigo, String ciudadDestino) {
    // Validación y resolución del nombre de la ciudad origen
    String origen = ciudadRepo.findByNombre(origenOCodigo)
        .map(c -> c.getNombre())
        .orElseThrow(() -> new RuntimeException("Ciudad origen no existe: " + origenOCodigo));

    // Validación destino
    ciudadRepo.findByNombre(ciudadDestino)
        .orElseThrow(() -> new RuntimeException("Ciudad destino no existe: " + ciudadDestino));

    // Construye la representación en memoria del grafo y delega a Dijkstra
    var g = adyacenciaBuilder.build();
    return dijkstraService.dijkstra(g, origen, ciudadDestino);
  }

  /**
   * Retorna la lista de ciudades alcanzables desde {@code ciudadInicio} con un máximo de
   * {@code saltos} pasos (niveles). Implementa un BFS acotado en profundidad/ niveles.
   *
   * <p>Uso:
   * - Construye la adyacencia desde Neo4j mediante {@link AdyacenciaBuilder#build()}.
   * - Recorre el grafo en anchura, manteniendo el nivel (distancia en número de aristas)
   *   desde la ciudad origen y cortando cuando se supera {@code saltos}.
   *
   * <p>Retorna el orden de visita (incluye la ciudad origen si existe).
   */
  public List<String> cobertura(String ciudadInicio, int saltos) {
    var g = adyacenciaBuilder.build();
    var visitado = new HashSet<String>();
    var q = new ArrayDeque<String>();
    var nivel = new HashMap<String,Integer>();
    var res = new ArrayList<String>();

    if (!g.containsKey(ciudadInicio)) return res; // ciudad no existe en gráfo
    visitado.add(ciudadInicio); q.add(ciudadInicio); nivel.put(ciudadInicio, 0);
    while(!q.isEmpty()){
      String u = q.poll();
      int nu = nivel.getOrDefault(u, 0);
      if (nu > saltos) continue; // no seguimos expandiendo más allá del límite
      res.add(u);
      for (var e : g.getOrDefault(u, List.of())) {
        String v = e.getKey();
        if (visitado.add(v)) { q.add(v); nivel.put(v, nu + 1); }
      }
    }
    return res;
  }

  /**
   * Calcula el árbol de expansión mínima (MST) sobre el subgrafo inducido por
   * {@code ciudadesObjetivo}.
   *
   * <p>Pasos:
   * - Construye la adyacencia completa con {@link AdyacenciaBuilder#build()}.
   * - Filtra los vecinos para conservar únicamente las aristas entre las ciudades del
   *   subconjunto {@code ciudadesObjetivo} (subgrafo inducido).
   * - Llama a {@link PrimService#prim(Map)} pasando el subgrafo filtrado para obtener
   *   el costo total y la lista de aristas del MST.
   *
   * <p>Devuelve un Map con claves como "costo" y "aristas" (según lo que retorne PrimService).
   */
  public Map<String,Object> mstSobreCiudades(List<String> ciudadesObjetivo) {
    if (ciudadesObjetivo == null || ciudadesObjetivo.isEmpty()) {
      return Map.of("costo", 0, "aristas", List.of());
    }
    var full = adyacenciaBuilder.build();
    var set = new HashSet<>(ciudadesObjetivo);
    var sub = new HashMap<String, List<Map.Entry<String,Double>>>();
    for (String c : set) {
      if (full.containsKey(c)) {
        var filtrados = new ArrayList<Map.Entry<String,Double>>();
        for (var e : full.getOrDefault(c, List.of())) {
          if (set.contains(e.getKey())) filtrados.add(e);
        }
        sub.put(c, filtrados);
      }
    }
    if (sub.isEmpty()) return Map.of("costo", 0, "aristas", List.of());
    return primService.prim(sub);
  }

  /**
   * Solución exacta por fuerza bruta del TSP para un conjunto pequeño de ciudades.
   *
   * <p>Flujo:
   * - Valida que todas las ciudades existan.
   * - Calcula la matriz de distancias mínimas entre cada par usando Dijkstra.
   * - Prueba todas las permutaciones del orden de visita (fixando la primera ciudad como origen
   *   para evitar equivalencias rotacionales) y retorna el tour de menor coste.
   *
   * <p>Limitación práctica: el coste es factorial. Por seguridad esta implementación rechaza
   * ejecuciones para n > 10 (lanza IllegalArgumentException). Para n mayor usar heurísticos.
   *
   * <p>Retorna un Map con claves:
   * - "costo": double con el coste total del tour (Double.POSITIVE_INFINITY si no hay camino entre nodos).
   * - "tour": List<String> con el orden de visita (incluye retorno a origen al final).
   */
  public Map<String,Object> tspBruteForce(List<String> ciudades) {
    if (ciudades == null || ciudades.isEmpty()) return Map.of("costo", 0.0, "tour", List.of());
    int n = ciudades.size();
    if (n > 10) throw new IllegalArgumentException("tspBruteForce sólo para n <= 10 (evitar factorial)");

    // validar existencia
    for (String c : ciudades) {
      ciudadRepo.findByNombre(c).orElseThrow(() -> new RuntimeException("Ciudad no existe: " + c));
    }

    // construir adyacencia y matriz de distancias (usando dijkstra para pares)
    var g = adyacenciaBuilder.build();
    double[][] dist = new double[n][n];
    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        if (i == j) { dist[i][j] = 0; continue; }
        var res = dijkstraService.dijkstra(g, ciudades.get(i), ciudades.get(j));
        Object dObj = res.getOrDefault("distancia", Double.POSITIVE_INFINITY);
        double d = (dObj instanceof Number) ? ((Number)dObj).doubleValue() : Double.POSITIVE_INFINITY;
        dist[i][j] = d;
      }
    }

    // permutar (fijamos ciudades[0] como origen)
    List<Integer> idx = new ArrayList<>();
    for (int i = 1; i < n; ++i) idx.add(i);
    double bestCost = Double.POSITIVE_INFINITY;
    List<String> bestTour = new ArrayList<>();

    do {
      double cost = 0.0;
      int prev = 0; // origen
      boolean feasible = true;
      for (int k : idx) {
        double d = dist[prev][k];
        if (Double.isInfinite(d)) { feasible = false; break; }
        cost += d; prev = k;
      }
      // retorno al origen
      if (feasible) {
        double d = dist[prev][0];
        if (Double.isInfinite(d)) feasible = false;
        else cost += d;
      }
      if (feasible && cost < bestCost) {
        bestCost = cost;
        bestTour = new ArrayList<>();
        bestTour.add(ciudades.get(0));
        for (int k : idx) bestTour.add(ciudades.get(k));
        bestTour.add(ciudades.get(0)); // retorno explícito
      }
    } while (nextPermutation(idx));

    return Map.of("costo", bestCost, "tour", bestTour);
  }

  // Helper: next permutation for list of integers (lexicographic). Returns false when at last permutation.
  private static boolean nextPermutation(List<Integer> arr) {
    int i = arr.size() - 2;
    while (i >= 0 && arr.get(i) >= arr.get(i+1)) i--;
    if (i < 0) return false;
    int j = arr.size() - 1;
    while (arr.get(j) <= arr.get(i)) j--;
    Collections.swap(arr, i, j);
    int l = i+1, r = arr.size()-1;
    while (l < r) { Collections.swap(arr, l, r); l++; r--; }
    return true;
  }
}
