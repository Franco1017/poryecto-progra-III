package com.ProyectoPrograIII.prograIII_TP.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.ProyectoPrograIII.prograIII_TP.model.Deposito;

public interface DepositoRepositorio extends Neo4jRepository<Deposito, Long> {
  Optional<Deposito> findByCodigo(String codigo);
}
