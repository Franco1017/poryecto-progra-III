package com.ProyectoPrograIII.prograIII_TP.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoPrograIII.prograIII_TP.model.Ciudad;
import com.ProyectoPrograIII.prograIII_TP.service.GrafoServicio;

/**
 * Controlador REST que expone los endpoints para interactuar con el grafo
 * (crear ciudades, conectar caminos y ejecutar algoritmos)
 */
@RestController
@RequestMapping("/grafo")
public class GrafoControlador {

    // Inyección del servicio principal que orquesta toda la lógica
    private final GrafoServicio servicio;

    public GrafoControlador(GrafoServicio servicio) {
        this.servicio = servicio;
    }

    // ----------------------------
    //   SECCIÓN CRUD BÁSICO
    // ----------------------------

    /** Crea una nueva ciudad en la base de datos Neo4j */
    @PostMapping("/ciudad/{nombre}")
    public Ciudad crearCiudad(@PathVariable String nombre) {
        return servicio.crearCiudad(nombre);
    }

    /** Crea un camino (una relación dirigida) entre dos ciudades */
    @PostMapping("/camino")
    public void conectar(@RequestBody CaminoReq req) {
        servicio.conectar(req.origen, req.destino, req.peso);
    }

    /** Lista todas las ciudades con sus caminos */
    @GetMapping("/ciudades")
    public Iterable<Ciudad> ciudades() {
        return servicio.todas();
    }

    // ----------------------------
    //   SECCIÓN ALGORITMOS
    // ----------------------------

    /** Recorrido en anchura (BFS) */
    @GetMapping("/bfs")
    public List<String> bfs(@RequestParam String inicio) {
        return servicio.bfs(inicio);
    }

    /** Recorrido en profundidad (DFS) */
    @GetMapping("/dfs")
    public List<String> dfs(@RequestParam String inicio) {
        return servicio.dfs(inicio);
    }

    /** Camino más corto entre dos ciudades (Dijkstra) */
    @GetMapping("/dijkstra")
    public Map<String, Object> dijkstra(@RequestParam String origen, @RequestParam String destino) {
        return servicio.dijkstra(origen, destino);
    }

    /** Árbol de expansión mínima (Prim) */
    @GetMapping("/prim")
    public Map<String, Object> prim() {
        return servicio.prim();
    }

    // Clase interna para mapear el JSON del body en /camino
    public static class CaminoReq {
        private String origen;
        private String destino;
        private double peso;

        public CaminoReq() {}

        public String getOrigen() { return origen; }
        public void setOrigen(String origen) { this.origen = origen; }

        public String getDestino() { return destino; }
        public void setDestino(String destino) { this.destino = destino; }

        public double getPeso() { return peso; }
        public void setPeso(double peso) { this.peso = peso; }
    }
}
