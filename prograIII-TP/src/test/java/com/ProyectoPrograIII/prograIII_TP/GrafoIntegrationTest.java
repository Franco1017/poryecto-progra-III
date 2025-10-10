package com.ProyectoPrograIII.prograIII_TP;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ProyectoPrograIII.prograIII_TP.repository.CiudadRepositorio;
import com.ProyectoPrograIII.prograIII_TP.service.AdyacenciaBuilder;
import com.ProyectoPrograIII.prograIII_TP.service.GrafoServicio;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GrafoIntegrationTest {

    @Autowired
    GrafoServicio servicio;

    @Autowired
    CiudadRepositorio ciudadRepositorio;

    @Autowired
    AdyacenciaBuilder adyacenciaBuilder;

    @BeforeEach
    void setup() {
        // Quick sanity: service must be injected
        assumeTrue(servicio != null, "GrafoServicio no inyectado, saltando tests de integración");
        // Limpia la base de datos para el test
        try {
            ciudadRepositorio.deleteAll();
        } catch (Exception ignored) {
        }
    }

    @Test
    void testInsertPersistenceOnly() {
        // Crear un pequeño grafo en la BD y verificar que las ciudades se persisten
        servicio.crearCiudad("A");
        servicio.crearCiudad("B");
        servicio.crearCiudad("C");

        // Verificar que repositorio contiene las ciudades
        assertTrue(ciudadRepositorio.findByNombre("A").isPresent(), "Ciudad A no persistida");
        assertTrue(ciudadRepositorio.findByNombre("B").isPresent(), "Ciudad B no persistida");
        assertTrue(ciudadRepositorio.findByNombre("C").isPresent(), "Ciudad C no persistida");
    }
}
