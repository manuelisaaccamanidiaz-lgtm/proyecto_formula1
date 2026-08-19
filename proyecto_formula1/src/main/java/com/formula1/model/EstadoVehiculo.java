package com.formula1.model;

import com.formula1.simulation.Participante;

import javafx.scene.paint.Color;

/**
 * Estado DINAMICO de un {@link Participante} (piloto + vehiculo + reglaje)
 * mientras avanza la simulacion en tiempo real. Es la version en vivo de lo
 * que {@code SimuladorClasificacion} calcula de una sola vez: aqui la
 * posicion, la velocidad y el tiempo de vuelta cambian cuadro a cuadro segun
 * el {@code MotorSimulacion}.
 *
 * NOTA IMPORTANTE (leer antes de usar):
 * Los campos "combustiblePorcentaje" y "desgasteNeumaticosPorcentaje" son una
 * PROPUESTA. El proyecto original no define ninguna regla de consumo de
 * combustible ni de desgaste de neumaticos a lo largo del tiempo (solo existe
 * la ESTRATEGIA elegida como un modificador fijo del tiempo de vuelta). Aqui
 * se implementa una tasa de consumo/desgaste ilustrativa, derivada de esa
 * misma estrategia/presion, para poder mostrar el panel de telemetria que
 * pide el enunciado. Si el desgaste/combustible no debe afectar la velocidad,
 * basta con no leer estos campos en el motor (ahora mismo el motor SI los usa
 * como un factor adicional, tambien marcado como propuesta).
 */
public class EstadoVehiculo {

    private final Participante participante;
    private final String equipo;
    private final Color color;
    private final int carril; // 0 = carril mas externo de la pista

    // --- Progreso en el circuito ---
    private double anguloRad = 0.0;       // posicion angular actual (0 a 2*PI)
    private int vueltasCompletas = 0;

    // --- Velocidad y tiempos (calculados por el motor cada cuadro) ---
    private double velocidadActualKmh = 0.0;
    private double tiempoVueltaActualSeg = 0.0;
    private Double ultimaVueltaSeg = null;
    private Double mejorVueltaSeg = null;
    private double tiempoTotalSeg = 0.0;

    /**
     * Variacion aleatoria de rendimiento del vehiculo para ESTA carrera,
     * sorteada una sola vez al iniciar (mismo rango +/-2% que usa
     * SimuladorClasificacion.simular). Se fija al inicio en vez de
     * re-sortearse cada cuadro para que la velocidad no "tiemble"
     * frame a frame. PROPUESTA de diseno, no estaba definida para
     * un escenario de carrera continua.
     */
    private final double variacionCarrera;

    // --- PROPUESTA: consumibles a lo largo de la carrera ---
    private double combustiblePorcentaje = 100.0;
    private double desgasteNeumaticosPorcentaje = 0.0;

    public EstadoVehiculo(Participante participante, String equipo, Color color, int carril, double variacionCarrera) {
        this.participante = participante;
        this.equipo = equipo;
        this.color = color;
        this.carril = carril;
        this.variacionCarrera = variacionCarrera;
    }

    /** Avanza el vehiculo segun su velocidad angular (rad/seg) durante dtSeg segundos. */
    public void avanzar(double omegaRadPorSeg, double dtSeg) {
        anguloRad += omegaRadPorSeg * dtSeg;
        tiempoVueltaActualSeg += dtSeg;
        tiempoTotalSeg += dtSeg;
        while (anguloRad >= 2 * Math.PI) {
            anguloRad -= 2 * Math.PI;
            vueltasCompletas += 1;
            ultimaVueltaSeg = tiempoVueltaActualSeg;
            if (mejorVueltaSeg == null || ultimaVueltaSeg < mejorVueltaSeg) {
                mejorVueltaSeg = ultimaVueltaSeg;
            }
            tiempoVueltaActualSeg = 0.0;
        }
    }

    /** Distancia total recorrida expresada en "vueltas" (para ordenar la clasificacion). */
    public double getDistanciaTotal() {
        return vueltasCompletas + (anguloRad / (2 * Math.PI));
    }

    public void reiniciar() {
        anguloRad = 0.0;
        vueltasCompletas = 0;
        velocidadActualKmh = 0.0;
        tiempoVueltaActualSeg = 0.0;
        ultimaVueltaSeg = null;
        mejorVueltaSeg = null;
        tiempoTotalSeg = 0.0;
        combustiblePorcentaje = 100.0;
        desgasteNeumaticosPorcentaje = 0.0;
    }

    // --- Getters / setters ---

    public Participante getParticipante() {
        return participante;
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

    public int getVueltasCompletas() {
        return vueltasCompletas;
    }

    public double getVelocidadActualKmh() {
        return velocidadActualKmh;
    }

    public void setVelocidadActualKmh(double velocidadActualKmh) {
        this.velocidadActualKmh = velocidadActualKmh;
    }

    public double getTiempoVueltaActualSeg() {
        return tiempoVueltaActualSeg;
    }

    public Double getUltimaVueltaSeg() {
        return ultimaVueltaSeg;
    }

    public Double getMejorVueltaSeg() {
        return mejorVueltaSeg;
    }

    public double getTiempoTotalSeg() {
        return tiempoTotalSeg;
    }

    public double getVariacionCarrera() {
        return variacionCarrera;
    }

    public double getCombustiblePorcentaje() {
        return combustiblePorcentaje;
    }

    public void setCombustiblePorcentaje(double combustiblePorcentaje) {
        this.combustiblePorcentaje = Math.max(0.0, Math.min(100.0, combustiblePorcentaje));
    }

    public double getDesgasteNeumaticosPorcentaje() {
        return desgasteNeumaticosPorcentaje;
    }

    public void setDesgasteNeumaticosPorcentaje(double desgasteNeumaticosPorcentaje) {
        this.desgasteNeumaticosPorcentaje = Math.max(0.0, Math.min(100.0, desgasteNeumaticosPorcentaje));
    }

    public static String formatearTiempo(double segundos) {
        int minutos = (int) (segundos / 60);
        double segs = segundos % 60;
        return String.format("%d:%06.3f", minutos, segs);
    }
}
