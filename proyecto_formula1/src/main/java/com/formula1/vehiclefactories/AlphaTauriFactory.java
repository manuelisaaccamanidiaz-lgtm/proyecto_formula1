package com.formula1.vehiclefactories;

import com.formula1.factories.VehicleFactory;
import com.formula1.vehicles.Vehicle;

public class AlphaTauriFactory implements VehicleFactory {
    @Override
    public Vehicle crearVehiculo(int id_vehiculo) {
        return new Vehicle(id_vehiculo, "AlphaTauri", "AT04", 2.6, 352);
    }
}
