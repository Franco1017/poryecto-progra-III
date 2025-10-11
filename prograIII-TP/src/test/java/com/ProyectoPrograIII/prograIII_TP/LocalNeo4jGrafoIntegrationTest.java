package com.ProyectoPrograIII.prograIII_TP;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.ProyectoPrograIII.prograIII_TP.repository.CiudadRepositorio;
import com.ProyectoPrograIII.prograIII_TP.service.GrafoServicio;

/**
 * Integración contra Neo4j local (bolt://localhost:7687).
 * - Corre solo con -DrunIT=true -DuseLocalNeo4j=true y si hay conectividad.
 * - Verifica persistencia y recorridos sobre tu instancia local.
 * - Incluye un test opcional de "visualización" que imprime URL/credenciales y pausa.
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("integration")
@EnabledIfSystemProperty(named = "runIT", matches = "true")
@EnabledIfSystemProperty(named = "useLocalNeo4j", matches = "true")
public class LocalNeo4jGrafoIntegrationTest {

    @Autowired
    Driver driver;

    @Autowired
    GrafoServicio servicio;

    @Autowired
    CiudadRepositorio ciudadRepositorio;

    @Autowired
    Environment env;

    @BeforeAll
    @SuppressWarnings("unused")
    public void ensureLocalNeo4j() {
        try {
            driver.verifyConnectivity();
        } catch (Exception e) {
            assumeTrue(false, "Neo4j local no disponible en bolt://localhost:7687. Inícialo o quita -DuseLocalNeo4j.");
        }
    }

    /**
     * Limpia la base local antes de cada prueba.
     */
    @BeforeEach
    @SuppressWarnings("unused")
    public void setup() {
        ciudadRepositorio.deleteAll();
    }

    /**
     * Inserta un pequeño grafo y valida BFS + Dijkstra en Neo4j local.
     */
    @Test
    void testPersistenceAndTraversalsLocal() {
        servicio.crearCiudad("X");
        servicio.crearCiudad("Y");
        servicio.crearCiudad("Z");

        servicio.conectar("X","Y",2.0);
        servicio.conectar("Y","Z",3.0);

        assertTrue(ciudadRepositorio.findByNombre("X").isPresent());
        assertTrue(ciudadRepositorio.findByNombre("Y").isPresent());

        List<String> bfs = servicio.bfs("X");
        assertTrue(bfs.containsAll(List.of("X","Y","Z")));

        var dij = servicio.dijkstra("X","Z");
        double distancia = ((Number)dij.get("distancia")).doubleValue();
        org.junit.jupiter.api.Assertions.assertEquals(5.0, distancia, 0.001);
    }

    /**
     * Test opcional de visualización en Neo4j local: imprime URL/credenciales y pausa.
     */
    @Test
    @EnabledIfSystemProperty(named = "pauseForBrowser", matches = "true")
    void printBrowserInfoAndPauseForVisualizationLocal() throws InterruptedException {
        // Asegura que haya algo para ver
        ciudadRepositorio.deleteAll();
        servicio.crearCiudad("A");
        servicio.crearCiudad("B");
        servicio.crearCiudad("C");
        servicio.conectar("A","B", 1.0);
        servicio.conectar("B","C", 2.0);
        servicio.conectar("A","C", 3.0);

        // Información del Browser local
        String httpUrl = "http://localhost:7474";
        String boltUrl = "bolt://localhost:7687";
        String user = env.getProperty("spring.neo4j.authentication.username", "neo4j");
        String password = env.getProperty("spring.neo4j.authentication.password", "<tu contraseña de Neo4j local>");

        System.out.println("\n=== Neo4j Local – Visualización ===");
        System.out.println("Abrí Neo4j Browser en: " + httpUrl);
        System.out.println("Bolt URI: " + boltUrl);
        System.out.println("Usuario: " + user);
        System.out.println("Password: " + password);
        System.out.println("Sugerencia Cypher para ver datos: MATCH (n)-[r]->(m) RETURN n,r,m LIMIT 50");

        long seconds = 30L;
        try {
            seconds = Long.parseLong(System.getProperty("visualizationSeconds", "30"));
        } catch (NumberFormatException ignored) {}
        System.out.println("Pausando " + seconds + "s para permitir la visualización en el navegador...");
    Thread.sleep(Duration.ofSeconds(seconds).toMillis());
        System.out.println("Reanudando y finalizando el test de visualización local.");
    }
}
