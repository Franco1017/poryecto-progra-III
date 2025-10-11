package com.ProyectoPrograIII.prograIII_TP;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.ProyectoPrograIII.prograIII_TP.repository.CiudadRepositorio;
import com.ProyectoPrograIII.prograIII_TP.service.GrafoServicio;

/**
 * Integración end-to-end usando Neo4j en contenedor (Testcontainers).
 * - Inyecta dinámicamente las propiedades de conexión a la app Spring.
 * - Verifica persistencia y recorridos (BFS/Dijkstra) en un entorno aislado y reproducible.
 * - Incluye un test opcional de "visualización" que imprime URL/credenciales del Browser y pausa.
 */
@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("integration")
@EnabledIfSystemProperty(named = "runIT", matches = "true")
public class TestcontainersGrafoIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5.7")
            .withAdminPassword("valentinayfranco");

    @DynamicPropertySource
    @SuppressWarnings("unused")
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", neo4j::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", neo4j::getAdminPassword);
    }

    @Autowired
    GrafoServicio servicio;

    @Autowired
    CiudadRepositorio ciudadRepositorio;

    @BeforeEach
    /**
     * Limpia la base del contenedor antes de cada prueba para garantizar aislamiento.
     */
    public void setup() {
        // limpia DB antes de cada test
        ciudadRepositorio.deleteAll();
    }

    /**
     * Crea un pequeño grafo en el contenedor y valida persistencia + BFS + Dijkstra.
     */
    @Test
    void testPersistenceAndTraversalsWithContainer() {
        // Crear ciudades y relaciones
        servicio.crearCiudad("X");
        servicio.crearCiudad("Y");
        servicio.crearCiudad("Z");

        servicio.conectar("X","Y",2.0);
        servicio.conectar("Y","Z",3.0);

        // Confirmar persistencia
        assertTrue(ciudadRepositorio.findByNombre("X").isPresent());
        assertTrue(ciudadRepositorio.findByNombre("Y").isPresent());

        // Ejecutar BFS via servicio
        List<String> bfs = servicio.bfs("X");
        assertNotNull(bfs);
        assertEquals("X", bfs.get(0));
        assertTrue(bfs.containsAll(List.of("X","Y","Z")));

        // Dijkstra X->Z debe dar distancia 5.0
        var dij = servicio.dijkstra("X","Z");
        assertNotNull(dij);
        double distancia = ((Number)dij.get("distancia")).doubleValue();
        assertEquals(5.0, distancia, 0.001);
    }

    /**
     * Test opcional para visualizar datos en el Browser del contenedor.
     * Imprime URL/credenciales y pausa N segundos (visualizationSeconds, por defecto 30).
     */
    @Test
    @EnabledIfSystemProperty(named = "pauseForBrowser", matches = "true")
    void printBrowserInfoAndPauseForVisualization() throws InterruptedException {
        // Semilla: crea un mini grafo para visualizar
        servicio.crearCiudad("A");
        servicio.crearCiudad("B");
        servicio.crearCiudad("C");
        servicio.conectar("A","B", 1.0);
        servicio.conectar("B","C", 2.0);
        servicio.conectar("A","C", 3.0);

        // Imprime URLs útiles para abrir Neo4j Browser y credenciales
        String httpUrl = neo4j.getHttpUrl();
        String boltUrl = neo4j.getBoltUrl();
        String user = "neo4j";
        String password = neo4j.getAdminPassword();

        System.out.println("\n=== Neo4j Testcontainers – Visualización ===");
        System.out.println("Abrí Neo4j Browser en: " + httpUrl);
        System.out.println("Bolt URI: " + boltUrl);
        System.out.println("Usuario: " + user);
        System.out.println("Password: " + password);
    System.out.println("Nota: el contenedor se detendrá al terminar el test.");
    System.out.println("Sugerencia Cypher para ver datos: MATCH (n)-[r]->(m) RETURN n,r,m LIMIT 50");

        // Ventana opcional para visualizar (por defecto 30s). Configurable con -DvisualizationSeconds=60
        long seconds = 30L;
        try {
            seconds = Long.parseLong(System.getProperty("visualizationSeconds", "30"));
        } catch (NumberFormatException ignored) {}
        System.out.println("Pausando " + seconds + "s para permitir la visualización en el navegador...");
    Thread.sleep(Duration.ofSeconds(seconds).toMillis());
        System.out.println("Reanudando y finalizando el test de visualización.");
    }
}
