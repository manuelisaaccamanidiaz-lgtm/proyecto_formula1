package com.formula1.simulation;

public class ResultadoClasificacion {

    private int posicion;
    private final String nombrePiloto;
    private final String equipo;
    private final double tiempoVueltaSegundos;

    public ResultadoClasificacion(String nombrePiloto, String equipo, double tiempoVueltaSegundos) {
        this.nombrePiloto = nombrePiloto;
        this.equipo = equipo;
        this.tiempoVueltaSegundos = tiempoVueltaSegundos;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getNombrePiloto() {
        return nombrePiloto;
    }

    public String getEquipo() {
        return equipo;
    }

    public double getTiempoVueltaSegundos() {
        return tiempoVueltaSegundos;
    }

    private String tiempoFormateado() {
        int minutos = (int) (tiempoVueltaSegundos / 60);
        double segundos = tiempoVueltaSegundos % 60;
        return String.format("%d:%06.3f", minutos, segundos);
    }

    @Override
    public String toString() {
        return String.format("%2d. %-20s %-25s %s", posicion, nombrePiloto, equipo, tiempoFormateado());
    }
}
