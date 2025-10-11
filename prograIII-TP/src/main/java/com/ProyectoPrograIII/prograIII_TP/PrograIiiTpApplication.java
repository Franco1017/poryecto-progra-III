package com.ProyectoPrograIII.prograIII_TP;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Spring Boot.
 * Inicializa el contexto e inicia el servidor embebido.
 */
@SpringBootApplication
public class PrograIiiTpApplication {

	/**
	 * Arranca la aplicación.
	 */
	public static void main(String[] args) {
		SpringApplication.run(PrograIiiTpApplication.class, args);
	}

}
