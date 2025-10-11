package com.ProyectoPrograIII.prograIII_TP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ProyectoPrograIII.prograIII_TP.service.BFSService;
import com.ProyectoPrograIII.prograIII_TP.service.DFSService;
import com.ProyectoPrograIII.prograIII_TP.service.DijkstraService;
import com.ProyectoPrograIII.prograIII_TP.service.PrimService;

/**
 * Pruebas unitarias de algoritmos (BFS, DFS, Dijkstra, Prim) totalmente en memoria.
 * - No inicia Spring ni se conecta a Neo4j.
 * - Valida la lógica pura de los algoritmos con grafos pequeños.
 */
public class AlgorithmUnitTests {

    // Instantiate algorithm services directly so tests don't start Spring or try to
    // connect to Neo4j. These services are simple stateless components.
    BFSService bfsService = new BFSService();
    DFSService dfsService = new DFSService();
    DijkstraService dijkstraService = new DijkstraService();
    PrimService primService = new PrimService();

    /**
     * Construye un grafo simple en memoria:
     * A -> B (1), A -> C (5), B -> C (1), C -> D (2)
     */
    private Map<String, List<Map.Entry<String, Double>>> sampleGraph() {
        Map<String, List<Map.Entry<String, Double>>> g = new HashMap<>();
        g.put("A", new ArrayList<>(List.of(Map.entry("B", 1.0), Map.entry("C", 5.0))));
        g.put("B", new ArrayList<>(List.of(Map.entry("C", 1.0))));
        g.put("C", new ArrayList<>(List.of(Map.entry("D", 2.0))));
        g.put("D", new ArrayList<>());
        return g;
    }

    /**
     * Verifica que BFS y DFS recorran todos los nodos esperados en un grafo simple.
     */
    @Test
    public void testBfsAndDfs() {
        var g = sampleGraph();
        var bfs = bfsService.bfs(g, "A");
        assertNotNull(bfs);
        // Orden BFS debe empezar en A y contener B y C y D
        assertEquals("A", bfs.get(0));
        assertTrue(bfs.containsAll(List.of("A","B","C","D")));

        var dfs = dfsService.dfs(g, "A");
        assertNotNull(dfs);
        assertTrue(dfs.containsAll(List.of("A","B","C","D")));
    }

    /**
     * Verifica que Dijkstra encuentre la distancia y el camino mínimo correctos.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testDijkstra() {
        var g = sampleGraph();
        var result = dijkstraService.dijkstra(g, "A", "D");
        assertNotNull(result);
        assertTrue(result.containsKey("distancia"));
        double distancia = ((Number) result.get("distancia")).doubleValue();
        assertEquals(4.0, distancia, 0.001);
        assertTrue(result.containsKey("camino"));
        var camino = (List<String>) result.get("camino");
        assertEquals(List.of("A","B","C","D"), camino);
    }

    /**
     * Verifica que Prim calcule un costo total mayor a 0 en un grafo no vacío.
     */
    @Test
    public void testPrim() {
        var g = sampleGraph();
        var result = primService.prim(g);
        assertNotNull(result);
        assertTrue(result.containsKey("costo") || result.containsKey("costo"));
        // costo debe ser mayor que cero para este grafo
        double costo = ((Number) result.getOrDefault("costo", 0)).doubleValue();
        assertTrue(costo > 0);
    }

    /**
     * Casos borde en grafo vacío: BFS/DFS vacíos, Dijkstra infinito, Prim costo 0.
     */
    @Test
    public void testEmptyGraphBehavior() {
        var g = new HashMap<String, List<Map.Entry<String, Double>>>();
        // BFS/DFS on empty graph -> empty order
        var bfs = bfsService.bfs(g, "A");
        var dfs = dfsService.dfs(g, "A");
        assertNotNull(bfs); assertTrue(bfs.isEmpty());
        assertNotNull(dfs); assertTrue(dfs.isEmpty());

        // Dijkstra: no path -> distancia infinite and empty camino
        var dij = dijkstraService.dijkstra(g, "A", "B");
        assertNotNull(dij);
        double dist = ((Number)dij.get("distancia")).doubleValue();
        assertTrue(Double.isInfinite(dist));
        assertTrue(((List<?>)dij.get("camino")).isEmpty());

        // Prim on empty -> costo 0 and no edges
        var prim = primService.prim(g);
        assertNotNull(prim);
        double costoPrim = ((Number) prim.getOrDefault("costo", 0)).doubleValue();
        assertEquals(0.0, costoPrim, 0.0001);
    }

    /**
     * Dijkstra cuando origen = destino: distancia 0 y camino con un solo nodo.
     */
    @Test
    public void testDijkstraSourceEqualsDestination() {
        var g = sampleGraph();
        var res = dijkstraService.dijkstra(g, "A", "A");
        assertNotNull(res);
        assertEquals(0.0, ((Number)res.get("distancia")).doubleValue(), 0.0001);
        // safely assert the camino is a List<String>
        Object caminoObj = res.get("camino");
        if (caminoObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> camino = (List<String>) caminoObj;
            assertEquals(List.of("A"), camino);
        } else {
            // Fail the test with a clear message if type is unexpected
            throw new AssertionError("Expected 'camino' to be a List<String>, but was " + (caminoObj == null ? "null" : caminoObj.getClass()));
        }
    }

    /**
     * Grafo desconectado: no hay camino entre componentes (distancia infinita).
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testDisconnectedGraph() {
        Map<String, List<Map.Entry<String, Double>>> g = new HashMap<>();
        g.put("A", new ArrayList<>(List.of(Map.entry("B", 1.0))));
        g.put("B", new ArrayList<>());
        g.put("C", new ArrayList<>(List.of(Map.entry("D", 2.0))));
        g.put("D", new ArrayList<>());

        var bfsA = bfsService.bfs(g, "A");
        assertEquals(List.of("A","B"), bfsA);

        var dij = dijkstraService.dijkstra(g, "A", "D");
        assertNotNull(dij);
        double distancia = ((Number)dij.get("distancia")).doubleValue();
        assertTrue(Double.isInfinite(distancia));
        assertTrue(((List<String>)dij.get("camino")).isEmpty());
    }
}
