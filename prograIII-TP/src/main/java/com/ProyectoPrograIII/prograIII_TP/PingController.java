package com.ProyectoPrograIII.prograIII_TP;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint simple de salud para comprobar que el servicio responde.
 */
@RestController
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}