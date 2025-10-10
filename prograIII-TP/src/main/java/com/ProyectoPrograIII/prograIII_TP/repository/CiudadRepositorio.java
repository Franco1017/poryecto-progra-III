package com.ProyectoPrograIII.prograIII_TP.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.ProyectoPrograIII.prograIII_TP.model.Ciudad;

/** Acceso a datos de Ciudad en Neo4j */
public interface CiudadRepositorio extends Neo4jRepository<Ciudad, Long> {
  Optional<Ciudad> findByNombre(String nombre);
}
