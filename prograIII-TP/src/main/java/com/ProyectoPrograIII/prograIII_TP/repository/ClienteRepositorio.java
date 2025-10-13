package com.ProyectoPrograIII.prograIII_TP.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.ProyectoPrograIII.prograIII_TP.model.Cliente;

public interface ClienteRepositorio extends Neo4jRepository<Cliente, Long> {
  Optional<Cliente> findByEmail(String email);
}
