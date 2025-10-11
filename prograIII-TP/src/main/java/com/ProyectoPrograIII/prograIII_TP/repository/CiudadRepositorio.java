package com.ProyectoPrograIII.prograIII_TP.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.ProyectoPrograIII.prograIII_TP.model.Ciudad;

/**
 * Repositorio Spring Data Neo4j para acceder a nodos Ciudad.
 * Incluye búsqueda por nombre y operaciones CRUD estándar.
 */
public interface CiudadRepositorio extends Neo4jRepository<Ciudad, Long> {
  Optional<Ciudad> findByNombre(String nombre);
}
