package com.formula1.vehiclefactories;

import com.formula1.factories.VehicleFactory;
import com.formula1.vehicles.Vehicle;

public class HaasFactory implements VehicleFactory {
    @Override
    public Vehicle crearVehiculo(int id_vehiculo) {
        return new Vehicle(id_vehiculo, "Haas", "VF-24", 3.0, 344);
    }
}
