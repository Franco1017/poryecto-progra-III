package com.ProyectoPrograIII.prograIII_TP.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.ProyectoPrograIII.prograIII_TP.model.Categoria;

public interface CategoriaRepositorio extends Neo4jRepository<Categoria, Long> {
  Optional<Categoria> findByNombre(String nombre);
}
