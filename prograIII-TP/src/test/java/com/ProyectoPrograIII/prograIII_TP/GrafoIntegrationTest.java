package com.ProyectoPrograIII.prograIII_TP;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ProyectoPrograIII.prograIII_TP.repository.CiudadRepositorio;
import com.ProyectoPrograIII.prograIII_TP.service.GrafoServicio;

/**
 * Integración básica contra Neo4j local.
 * - Usa la configuración por defecto (bolt://localhost:7687).
 * - Si no hay conectividad, se salta el test en lugar de fallar.
 * - Inserta un grafo "grande" y verifica persistencia via repositorio.
 */
@Tag("integration")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GrafoIntegrationTest {

    @Autowired
    @SuppressWarnings("unused")
    GrafoServicio servicio;

    @Autowired
    @SuppressWarnings("unused")
    CiudadRepositorio ciudadRepositorio;

    @Autowired
    Driver neo4jDriver;

    /**
     * Verifica conectividad a Neo4j local; si no está disponible, salta los tests.
     */
    @BeforeAll
    @SuppressWarnings("unused")
    public void ensureNeo4jIsAvailable() {
        try {
            neo4jDriver.verifyConnectivity();
        } catch (Exception e) {
            assumeTrue(false, "Neo4j no está disponible en bolt://localhost:7687. Inicia Neo4j o ejecuta los tests con Testcontainers.");
        }
    }

    // AdyacenciaBuilder removed from test since it's not used directly here

    /**
     * Limpia la base antes de cada prueba e insiste en que el servicio esté inyectado.
     */
    @BeforeEach
    @SuppressWarnings("unused")
    void setup() {
        // Quick sanity: service must be injected
        assumeTrue(servicio != null, "GrafoServicio no inyectado, saltando tests de integración");
        // Limpia la base de datos para el test
        try {
            ciudadRepositorio.deleteAll();
        } catch (Exception ignored) {
        }
    }

    /**
     * Inserta múltiples ciudades y caminos y comprueba que todas persistan.
     */
    @Test
    void testInsertPersistenceOnly() {
        // Crear un pequeño grafo en la BD y verificar que las ciudades se persisten
        servicio.crearCiudad("J");
        servicio.crearCiudad("A");
        servicio.crearCiudad("B");
        servicio.crearCiudad("C");
        servicio.crearCiudad("D");
        servicio.crearCiudad("E");
        servicio.crearCiudad("F");
        servicio.crearCiudad("G");
        servicio.crearCiudad("H");
        servicio.crearCiudad("I");  

        servicio.conectar("J", "B", 1.0);
        servicio.conectar("J", "C", 2.0);
        servicio.conectar("B", "D", 1.0);   
        servicio.conectar("C", "D", 5.0);
        servicio.conectar("D", "E", 1.0);
        servicio.conectar("E", "F", 1.0);
        servicio.conectar("B", "F", 10.0);
        servicio.conectar("A", "B", 4.0);
        servicio.conectar("A", "C", 6.0);
        servicio.conectar("B", "C", 2.0);
        servicio.conectar("C", "G", 1.0);
        servicio.conectar("D", "H", 2.0);
        servicio.conectar("H", "F", 3.0);
        servicio.conectar("G", "F", 1.0);
        servicio.conectar("G", "H", 2.0);
        servicio.conectar("A", "G", 1.0);
        servicio.conectar("F", "A", 7.0);
        servicio.conectar("I", "H", 1.0);
        servicio.conectar("I", "E", 2.0);
        servicio.conectar("I", "F", 3.0);

        // Verificar que repositorio contiene las ciudades
        assertTrue(ciudadRepositorio.findByNombre("J").isPresent(), "Ciudad J no persistida");
        assertTrue(ciudadRepositorio.findByNombre("B").isPresent(), "Ciudad B no persistida");
        assertTrue(ciudadRepositorio.findByNombre("C").isPresent(), "Ciudad C no persistida");
        assertTrue(ciudadRepositorio.findByNombre("D").isPresent(), "Ciudad D no persistida");
        assertTrue(ciudadRepositorio.findByNombre("E").isPresent(), "Ciudad E no persistida");
        assertTrue(ciudadRepositorio.findByNombre("F").isPresent(), "Ciudad F no persistida");
        assertTrue(ciudadRepositorio.findByNombre("G").isPresent(), "Ciudad G no persistida");
        assertTrue(ciudadRepositorio.findByNombre("H").isPresent(), "Ciudad H no persistida");
        assertTrue(ciudadRepositorio.findByNombre("A").isPresent(), "Ciudad A no persistida");
        assertTrue(ciudadRepositorio.findByNombre("I").isPresent(), "Ciudad I no persistida");
    }
}
