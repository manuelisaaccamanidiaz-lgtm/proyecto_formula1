package com.formula1.javafx;

import com.formula1.pilots.Pilot;
import com.formula1.vehicles.Vehicle;
import javafx.scene.paint.Color;

/**
 * Representa un auto en la pista: un piloto asignado a un vehiculo,
 * con su color de equipo y su estado actual dentro de la simulacion
 * (angulo recorrido en la pista y vueltas completas).
 */
public class RaceCar {

    private final Pilot piloto;
    private final Vehicle vehiculo;
    private final String equipo;
    private final Color color;
    private final int carril; // 0 = carril mas externo de la pista

    // Estado de la simulacion
    private double anguloRad = 0.0;      // posicion angular actual (0 a 2*PI)
    private double vueltasCompletas = 0; // numero de vueltas ya completadas

    public RaceCar(Pilot piloto, Vehicle vehiculo, String equipo, Color color, int carril) {
        this.piloto = piloto;
        this.vehiculo = vehiculo;
        this.equipo = equipo;
        this.color = color;
        this.carril = carril;
    }

    /** Avanza el auto segun su velocidad angular (rad/seg) durante dtSeg segundos. */
    public void avanzar(double omegaRadPorSeg, double dtSeg) {
        anguloRad += omegaRadPorSeg * dtSeg;
        while (anguloRad >= 2 * Math.PI) {
            anguloRad -= 2 * Math.PI;
            vueltasCompletas += 1;
        }
    }

    /** Distancia total recorrida expresada en "vueltas" (para ordenar el ranking). */
    public double getDistanciaTotal() {
        return vueltasCompletas + (anguloRad / (2 * Math.PI));
    }

    public Pilot getPiloto() {
        return piloto;
    }

    public Vehicle getVehiculo() {
        return vehiculo;
    }

    public String getEquipo() {
        return equipo;
    }

    public Color getColor() {
        return color;
    }

    public int getCarril() {
        return carril;
    }

    public double getAnguloRad() {
        return anguloRad;
    }

    public double getVueltasCompletas() {
        return vueltasCompletas;
    }
}
