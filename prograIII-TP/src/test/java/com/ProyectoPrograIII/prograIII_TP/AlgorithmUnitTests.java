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
}
