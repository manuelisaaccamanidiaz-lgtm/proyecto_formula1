package com.formula1.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Guarda la configuracion de reglaje (modo, aero, neumaticos, combustible)
 * de cada vehiculo, indexada por id de vehiculo. Cumple la historia de
 * usuario "Guardar configuracion del vehiculo" y permite reutilizarla en
 * futuras simulaciones.
 */
public class ConfiguracionManager {

    private final Map<Integer, ConfiguracionVehiculo> configuraciones = new LinkedHashMap<>();

    public void guardar(ConfiguracionVehiculo configuracion) {
        configuraciones.put(configuracion.getIdVehiculo(), configuracion);
    }

    /** Si el vehiculo aun no tiene configuracion guardada, devuelve una por defecto. */
    public ConfiguracionVehiculo obtener(int idVehiculo) {
        return configuraciones.getOrDefault(idVehiculo, ConfiguracionVehiculo.porDefecto(idVehiculo));
    }

    public boolean tieneConfiguracion(int idVehiculo) {
        return configuraciones.containsKey(idVehiculo);
    }
}
