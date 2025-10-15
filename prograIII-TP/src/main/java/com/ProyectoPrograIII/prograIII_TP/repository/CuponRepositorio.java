package com.ProyectoPrograIII.prograIII_TP.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.ProyectoPrograIII.prograIII_TP.model.Cupon;

public interface CuponRepositorio extends Neo4jRepository<Cupon, Long> {
}
