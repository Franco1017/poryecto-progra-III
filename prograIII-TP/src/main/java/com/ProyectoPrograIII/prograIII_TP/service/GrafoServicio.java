package com.ProyectoPrograIII.prograIII_TP.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ProyectoPrograIII.prograIII_TP.model.Camino;
import com.ProyectoPrograIII.prograIII_TP.model.Ciudad;
import com.ProyectoPrograIII.prograIII_TP.repository.CiudadRepositorio;

/**
 * Servicio orquestador del dominio:
 * - Persiste ciudades y caminos en Neo4j.
 * - Construye la adyacencia desde la BD para alimentar los algoritmos.
 * - Expone operaciones de BFS/DFS/Dijkstra/Prim a consumidores (controlador/tests).
 */
@Service
public class GrafoServicio {

  private final CiudadRepositorio repo;
  private final AdyacenciaBuilder adyacenciaBuilder;
  private final BFSService bfsService;
  private final DFSService dfsService;
  private final DijkstraService dijkstraService;
  private final PrimService primService;

  public GrafoServicio(CiudadRepositorio repo,
                       AdyacenciaBuilder adyacenciaBuilder,
                       BFSService bfsService,
                       DFSService dfsService,
                       DijkstraService dijkstraService,
                       PrimService primService) {
    this.repo = repo;
    this.adyacenciaBuilder = adyacenciaBuilder;
    this.bfsService = bfsService;
    this.dfsService = dfsService;
    this.dijkstraService = dijkstraService;
    this.primService = primService;
  }

  // --------- CRUD básico ----------
  /** Crea y persiste una ciudad. */
  public Ciudad crearCiudad(String nombre) {
    Ciudad c = new Ciudad();
    c.setNombre(nombre);
    return repo.save(c);
  }

  /** Crea (si no existen) y conecta dos ciudades con un CAMINO dirigido y su peso. */
  public void conectar(String origen, String destino, double peso) {
    Ciudad a = repo.findByNombre(origen).orElseGet(() -> {
      Ciudad tmp = new Ciudad();
      tmp.setNombre(origen);
      return repo.save(tmp);
    });
    Ciudad b = repo.findByNombre(destino).orElseGet(() -> {
      Ciudad tmp = new Ciudad();
      tmp.setNombre(destino);
      return repo.save(tmp);
    });
    List<Camino> salientes = (a.getCaminos() == null) ? new ArrayList<>() : a.getCaminos();
    Camino nuevo = new Camino();
    nuevo.setDestino(b);
    nuevo.setPeso(peso);
    salientes.add(nuevo);
    a.setCaminos(salientes);
    repo.save(a);
  }

  /** Lista todas las ciudades persistidas. */
  public Iterable<Ciudad> todas() { return repo.findAll(); }

  // --------- Construcción de adyacencia desde Neo4j ----------
  /** Construye el mapa de adyacencia a partir de Neo4j. */
  private Map<String, List<Map.Entry<String, Double>>> adyacencia() {
    return adyacenciaBuilder.build();
  }

  // --------- BFS ----------
  /** Ejecuta BFS desde el nodo indicado. */
  public List<String> bfs(String inicio) {
    return bfsService.bfs(adyacencia(), inicio);
  }

  // --------- DFS ----------
  /** Ejecuta DFS desde el nodo indicado. */
  public List<String> dfs(String inicio) {
    return dfsService.dfs(adyacencia(), inicio);
  }

  // --------- Dijkstra ----------
  /** Calcula camino mínimo entre origen y destino con Dijkstra. */
  public Map<String,Object> dijkstra(String origen, String destino) {
    return dijkstraService.dijkstra(adyacencia(), origen, destino);
  }

  // --------- Prim (MST sobre grafo no dirigido) ----------
  /** Calcula el MST sobre el grafo no dirigido equivalente (Prim). */
  public Map<String,Object> prim() {
    return primService.prim(adyacencia());
  }
}
