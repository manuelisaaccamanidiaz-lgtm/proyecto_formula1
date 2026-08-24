package com.formula1.simulation;

import com.formula1.config.ConfiguracionVehiculo;
import com.formula1.pilots.Pilot;
import com.formula1.vehicles.Vehicle;

public class Participante {

    private final Pilot piloto;
    private final Vehicle vehiculo;
    private final ConfiguracionVehiculo configuracion;

    public Participante(Pilot piloto, Vehicle vehiculo, ConfiguracionVehiculo configuracion) {
        this.piloto = piloto;
        this.vehiculo = vehiculo;
        this.configuracion = configuracion;
    }

    public Pilot getPiloto() {
        return piloto;
    }

    public Vehicle getVehiculo() {
        return vehiculo;
    }

    public ConfiguracionVehiculo getConfiguracion() {
        return configuracion;
    }
}
