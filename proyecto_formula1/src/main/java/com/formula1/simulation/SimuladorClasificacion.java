package com.formula1.simulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.formula1.circuits.Circuit;

/**
 * Calcula el tiempo de vuelta estimado de cada participante combinando:
 * - la velocidad maxima del vehiculo y la longitud del circuito (tiempo base)
 * - la habilidad del piloto
 * - el reglaje elegido (modo de conduccion, carga aerodinamica, presion de
 *   neumaticos, estrategia de combustible)
 * - el clima de la sesion
 * - una pequena variacion aleatoria para que la simulacion no sea identica
 *   cada vez
 * y luego ordena a los participantes de menor a mayor tiempo (pole position
 * primero).
 */
public class SimuladorClasificacion {

    private final Random random = new Random();

    /**
     * Calcula el factor de rendimiento combinado (habilidad + reglaje + clima)
     * de un participante, SIN la variacion aleatoria de {@link #simular}.
     * Un factor menor a 1.0 acelera el tiempo de vuelta; un factor mayor a 1.0
     * lo ralentiza. Se extrajo de {@link #simular} para que el motor de
     * simulacion en tiempo real (com.formula1.simulation.MotorSimulacion)
     * pueda reutilizar EXACTAMENTE las mismas reglas al calcular la velocidad
     * instantanea de cada vehiculo, en lugar de duplicar esta logica.
     */
    public double calcularFactorRendimiento(Participante participante, Clima clima) {
        double factorHabilidad = 1.0 - ((participante.getPiloto().getHabilidad() - 70) / 500.0);
        double factorModo = switch (participante.getConfiguracion().getModo()) {
            case AGRESIVA -> 0.97;
            case AHORRO_COMBUSTIBLE -> 1.05;
            default -> 1.0;
        };
        double factorAero = switch (participante.getConfiguracion().getCargaAerodinamica()) {
            case ALTA -> 0.985;
            case BAJA -> 1.01;
            default -> 1.0;
        };
        double factorNeumaticos = switch (participante.getConfiguracion().getPresionNeumaticos()) {
            case ALTA -> 0.99;
            case BAJA -> 1.02;
            default -> 1.0;
        };
        double factorCombustible = switch (participante.getConfiguracion().getEstrategiaCombustible()) {
            case AGRESIVA -> 0.985;
            case AHORRO -> 1.03;
            default -> 1.0;
        };
        double factorClima = switch (clima) {
            case LLUVIOSO -> 1.08;
            case EXTREMO -> 1.18;
            default -> 1.0;
        };
        return factorHabilidad * factorModo * factorAero * factorNeumaticos * factorCombustible * factorClima;
    }

    public List<ResultadoClasificacion> simular(Circuit circuito, List<Participante> participantes, Clima clima) {
        List<ResultadoClasificacion> resultados = new ArrayList<>();

        for (Participante participante : participantes) {
            double tiempoBase = (circuito.getLongitud_km() / participante.getVehiculo().getVelocidad_maxima()) * 3600.0;
            double factorRendimiento = calcularFactorRendimiento(participante, clima);
            double variacionAleatoria = 0.98 + random.nextDouble() * 0.04; // +/- 2%

            double tiempoVuelta = tiempoBase * factorRendimiento * variacionAleatoria;

            resultados.add(new ResultadoClasificacion(
                    participante.getPiloto().getNombre(),
                    participante.getPiloto().getEquipo(),
                    tiempoVuelta));
        }

        resultados.sort(Comparator.comparingDouble(ResultadoClasificacion::getTiempoVueltaSegundos));
        for (int i = 0; i < resultados.size(); i++) {
            resultados.get(i).setPosicion(i + 1);
        }
        return resultados;
    }
}
