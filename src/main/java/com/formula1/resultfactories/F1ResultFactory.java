package com.formula1.resultfactories;

import com.formula1.factories.ResultFactory;
import com.formula1.results.Result;

/**
 * Crea los records de vuelta historicos para los 7 circuitos del
 * documento de requerimientos (mismos datos usados en F1CircuitFactory).
 */
public class F1ResultFactory implements ResultFactory {

    // circuito, ganador, tiempo de vuelta en segundos (mm:ss.mmm convertido)
    private static final Object[][] DATOS = {
            {"Circuito de Monaco", "Lewis Hamilton", 70.166},
            {"Silverstone", "Max Verstappen", 87.097},
            {"Circuito de Spa-Francorchamps", "Valtteri Bottas", 106.286},
            {"Circuito de Monza", "Rubens Barrichello", 81.046},
            {"Interlagos", "Valtteri Bottas", 70.540},
            {"Circuito de Yas Marina", "Lewis Hamilton", 99.283},
            {"Circuito de Suzuka", "Lewis Hamilton", 90.983}
    };

    @Override
    public Result createResult(int id_resultado) {
        if (id_resultado < 1 || id_resultado > DATOS.length) {
            return null;
        }
        Object[] fila = DATOS[id_resultado - 1];
        String circuito = (String) fila[0];
        String ganador = (String) fila[1];
        double tiempo = (double) fila[2];
        return new Result(id_resultado, ganador, tiempo, tiempo, circuito);
    }
}
