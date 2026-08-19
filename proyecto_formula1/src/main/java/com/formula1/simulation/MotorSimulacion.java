package com.formula1.simulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.formula1.circuits.Circuit;
import com.formula1.config.EstrategiaCombustible;
import com.formula1.config.PresionNeumaticos;
import com.formula1.model.EstadoVehiculo;

/**
 * Motor de la simulacion en tiempo real. NO reemplaza a
 * {@link SimuladorClasificacion} (que sigue calculando la clasificacion de
 * una sola vuelta de clasificacion) sino que reutiliza sus mismas reglas
 * ({@link SimuladorClasificacion#calcularFactorRendimiento}) para decidir,
 * cuadro a cuadro, que tan rapido avanza cada vehiculo por el circuito.
 *
 * Reglas reutilizadas del proyecto (NO inventadas):
 *  - Velocidad maxima del vehiculo (Vehicle.getVelocidad_maxima()).
 *  - Longitud del circuito (Circuit.getLongitud_km()).
 *  - Habilidad del piloto (Pilot.getHabilidad()).
 *  - Reglaje del vehiculo: modo de conduccion, carga aerodinamica, presion
 *    de neumaticos, estrategia de combustible (ConfiguracionVehiculo).
 *  - Clima de la sesion (Clima: SECO / LLUVIOSO / EXTREMO).
 *
 * Reglas propuestas (marcadas explicitamente, no existian en el proyecto):
 *  - Consumo de combustible y desgaste de neumaticos progresivos a lo largo
 *    de la carrera, y su efecto (leve) sobre la velocidad cuando el
 *    combustible/desgaste llegan a niveles altos. Ver metodo
 *    {@link #actualizarConsumibles}.
 *  - El clima, la temperatura y el viento NO cambian durante la carrera: el
 *    proyecto solo sortea un Clima fijo por sesion (Clima.aleatorio()), no
 *    existe temperatura numerica ni viento en ninguna clase. Este motor
 *    respeta eso: el clima se fija al construir el motor y no varia.
 */
public class MotorSimulacion {

    private final Circuit circuito;
    private final List<EstadoVehiculo> vehiculos;
    private final Clima clima;
    private final int vueltaObjetivo;
    private final SimuladorClasificacion calculadorFactores = new SimuladorClasificacion();
    private final Random random = new Random();

    private boolean pausado = false;
    private boolean finalizado = false;
    private EstadoVehiculo ganador = null;
    private double multiplicadorVelocidad = 1.0;

    public MotorSimulacion(Circuit circuito, List<EstadoVehiculo> vehiculos, Clima clima, int vueltaObjetivo) {
        this.circuito = circuito;
        this.vehiculos = vehiculos;
        this.clima = clima;
        this.vueltaObjetivo = vueltaObjetivo;
    }

    /**
     * Avanza la simulacion "dtSegReal" segundos (tiempo real transcurrido
     * desde el ultimo cuadro, medido por el AnimationTimer de JavaFX).
     * Este metodo es puro calculo (sin tocar nodos de JavaFX), por lo que es
     * seguro llamarlo directamente desde el hilo de la aplicacion de JavaFX.
     */
    public void actualizar(double dtSegReal) {
        if (pausado || finalizado || dtSegReal <= 0) {
            return;
        }
        double dtSeg = dtSegReal * multiplicadorVelocidad;

        for (EstadoVehiculo estado : vehiculos) {
            double velocidadMaxima = estado.getParticipante().getVehiculo().getVelocidad_maxima();
            double factorRendimiento = calculadorFactores.calcularFactorRendimiento(estado.getParticipante(), clima);
            double factorDesgaste = factorPorConsumibles(estado); // PROPUESTA, ver mas abajo

            // factorRendimiento > 1 = mas lento (igual que en SimuladorClasificacion,
            // donde multiplica el tiempo de vuelta). Para velocidad se invierte.
            double velocidadEfectivaKmh = velocidadMaxima
                    / (factorRendimiento * estado.getVariacionCarrera() * factorDesgaste);
            estado.setVelocidadActualKmh(velocidadEfectivaKmh);

            double tiempoVueltaSeg = (circuito.getLongitud_km() / velocidadEfectivaKmh) * 3600.0;
            double omegaRadPorSeg = (2 * Math.PI) / tiempoVueltaSeg;

            estado.avanzar(omegaRadPorSeg, dtSeg);
            actualizarConsumibles(estado, dtSeg);

            if (!finalizado && estado.getVueltasCompletas() >= vueltaObjetivo) {
                finalizado = true;
                ganador = estado;
            }
        }
    }

    /**
     * PROPUESTA: hace que el combustible baje y el desgaste de neumaticos
     * suba con el tiempo. La tasa depende de la EstrategiaCombustible y la
     * PresionNeumaticos ya elegidas en la configuracion del vehiculo (no se
     * inventan reglajes nuevos, solo se usa el reglaje existente para variar
     * la velocidad de consumo). El objetivo es llegar a ~0% de combustible y
     * ~100% de desgaste aproximadamente al completar vueltaObjetivo vueltas,
     * de forma ilustrativa.
     */
    private void actualizarConsumibles(EstadoVehiculo estado, double dtSeg) {
        double duracionEstimadaCarreraSeg = Math.max(1.0,
                (circuito.getLongitud_km() / estado.getParticipante().getVehiculo().getVelocidad_maxima())
                        * 3600.0 * vueltaObjetivo);

        double factorConsumoCombustible = switch (estado.getParticipante().getConfiguracion().getEstrategiaCombustible()) {
            case AGRESIVA -> 1.25;
            case AHORRO -> 0.75;
            default -> 1.0;
        };
        double factorDesgasteNeumaticos = switch (estado.getParticipante().getConfiguracion().getPresionNeumaticos()) {
            case ALTA -> 1.15;
            case BAJA -> 0.9;
            default -> 1.0;
        };

        double consumoPorSeg = (100.0 / duracionEstimadaCarreraSeg) * factorConsumoCombustible;
        double desgastePorSeg = (100.0 / duracionEstimadaCarreraSeg) * factorDesgasteNeumaticos;

        estado.setCombustiblePorcentaje(estado.getCombustiblePorcentaje() - consumoPorSeg * dtSeg);
        estado.setDesgasteNeumaticosPorcentaje(estado.getDesgasteNeumaticosPorcentaje() + desgastePorSeg * dtSeg);
    }

    /**
     * PROPUESTA: penalizacion leve de velocidad cuando el desgaste de
     * neumaticos es muy alto o el combustible esta muy bajo (por debajo de
     * cierto umbral). No estaba definida en el proyecto original; se marca
     * aqui como una regla adicional y opcional. Devuelve un factor >= 1.0
     * (mismo sentido que factorRendimiento: mayor = mas lento).
     */
    private double factorPorConsumibles(EstadoVehiculo estado) {
        double penalizacionNeumaticos = estado.getDesgasteNeumaticosPorcentaje() > 80
                ? 1.0 + ((estado.getDesgasteNeumaticosPorcentaje() - 80) / 100.0)
                : 1.0;
        double penalizacionCombustible = estado.getCombustiblePorcentaje() < 5
                ? 1.05
                : 1.0;
        return penalizacionNeumaticos * penalizacionCombustible;
    }

    /** Genera una copia ordenada de mayor a menor distancia recorrida (P1 primero). */
    public List<EstadoVehiculo> obtenerClasificacionOrdenada() {
        List<EstadoVehiculo> ordenados = new ArrayList<>(vehiculos);
        ordenados.sort(Comparator.comparingDouble(EstadoVehiculo::getDistanciaTotal).reversed());
        return ordenados;
    }

    public void reiniciar() {
        for (EstadoVehiculo estado : vehiculos) {
            estado.reiniciar();
        }
        finalizado = false;
        ganador = null;
        pausado = false;
    }

    public boolean isPausado() {
        return pausado;
    }

    public void setPausado(boolean pausado) {
        this.pausado = pausado;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public EstadoVehiculo getGanador() {
        return ganador;
    }

    public double getMultiplicadorVelocidad() {
        return multiplicadorVelocidad;
    }

    public void setMultiplicadorVelocidad(double multiplicadorVelocidad) {
        this.multiplicadorVelocidad = multiplicadorVelocidad;
    }

    public Circuit getCircuito() {
        return circuito;
    }

    public Clima getClima() {
        return clima;
    }

    public int getVueltaObjetivo() {
        return vueltaObjetivo;
    }

    public List<EstadoVehiculo> getVehiculos() {
        return vehiculos;
    }

    public double sortearVariacionCarrera() {
        return 0.98 + random.nextDouble() * 0.04; // mismo rango que SimuladorClasificacion.simular (+/-2%)
    }
}
