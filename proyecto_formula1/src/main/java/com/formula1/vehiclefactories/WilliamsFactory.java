package com.formula1.vehiclefactories;

import com.formula1.factories.VehicleFactory;
import com.formula1.vehicles.Vehicle;

public class WilliamsFactory implements VehicleFactory {
    @Override
    public Vehicle crearVehiculo(int id_vehiculo) {
        return new Vehicle(id_vehiculo, "Williams", "FW46", 2.9, 345);
    }
}
