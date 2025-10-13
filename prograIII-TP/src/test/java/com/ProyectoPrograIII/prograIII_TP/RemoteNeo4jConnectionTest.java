package com.ProyectoPrograIII.prograIII_TP;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de integración opcional: intenta conectarse a una Neo4j remota si se provee la URI
 * vía variable de entorno. Para evitar que falle en entornos sin DB remota, el test se
 * salta si no se encuentra ninguna variable relevante.
 *
 * Variables que el test reconoce (en orden de preferencia):
 * - SPRING_NEO4J_URI
 * - NEO4J_REMOTE_URI
 * - NEO4J_URI
 */
@SpringBootTest
public class RemoteNeo4jConnectionTest {

  @Autowired
  private Driver driver;

  @Test
  void connectsToRemoteIfConfigured() {
    String uri = System.getenv("SPRING_NEO4J_URI");
    if (uri == null || uri.isBlank()) uri = System.getenv("NEO4J_REMOTE_URI");
    if (uri == null || uri.isBlank()) uri = System.getenv("NEO4J_URI");

    Assumptions.assumeTrue(uri != null && !uri.isBlank(), "No remote Neo4j URI configured - skipping test");

    try (Session session = driver.session()) {
      int v = session.run("RETURN 1 AS v").single().get("v").asInt();
      assertEquals(1, v);
    }
  }
}
