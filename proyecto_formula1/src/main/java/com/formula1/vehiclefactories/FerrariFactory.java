package com.formula1.vehiclefactories;

import com.formula1.factories.VehicleFactory;
import com.formula1.vehicles.Vehicle;

public class FerrariFactory implements VehicleFactory {
    @Override
    public Vehicle crearVehiculo(int id_vehiculo) {
        return new Vehicle(id_vehiculo, "Ferrari", "SF-24", 2.5, 358);
    }
}
