package com.formula1.simulation;

import com.formula1.circuits.Circuit;
import com.formula1.config.EstrategiaCombustible;

/**
 * Agrupa las opciones que el usuario elige en la pantalla "A rodar!" antes
 * de iniciar una carrera: circuito, clima, estrategia de combustible global
 * (aplicada a todos los vehiculos para esa carrera) y el nivel de
 * combustible inicial. Es un "value object" simple: no reemplaza
 * ConfiguracionVehiculo (que sigue siendo el reglaje guardado por
 * vehiculo), solo agrega las opciones a nivel de carrera que pide el
 * enunciado (pista, clima, gasolina).
 */
public class ConfiguracionCarrera {

    private final Circuit circuito;
    private final Clima clima;
    private final EstrategiaCombustible estrategiaCombustibleGlobal;
    private final double combustibleInicialPorcentaje;

    public ConfiguracionCarrera(Circuit circuito, Clima clima, EstrategiaCombustible estrategiaCombustibleGlobal,
                                 double combustibleInicialPorcentaje) {
        this.circuito = circuito;
        this.clima = clima;
        this.estrategiaCombustibleGlobal = estrategiaCombustibleGlobal;
        this.combustibleInicialPorcentaje = combustibleInicialPorcentaje;
    }

    public Circuit getCircuito() {
        return circuito;
    }

    public Clima getClima() {
        return clima;
    }

    public EstrategiaCombustible getEstrategiaCombustibleGlobal() {
        return estrategiaCombustibleGlobal;
    }

    public double getCombustibleInicialPorcentaje() {
        return combustibleInicialPorcentaje;
    }
}
