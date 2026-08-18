package com.formula1.vehiclefactories;

import com.formula1.factories.VehicleFactory;
import com.formula1.vehicles.Vehicle;

public class AlfaRomeoFactory implements VehicleFactory {
    @Override
    public Vehicle crearVehiculo(int id_vehiculo) {
        return new Vehicle(id_vehiculo, "Alfa Romeo", "C44", 2.8, 347);
    }
}
