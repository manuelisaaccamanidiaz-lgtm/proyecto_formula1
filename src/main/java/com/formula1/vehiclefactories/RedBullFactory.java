package com.formula1.vehiclefactories;

import com.formula1.factories.VehicleFactory;
import com.formula1.vehicles.Vehicle;

public class RedBullFactory implements VehicleFactory {
    @Override
    public Vehicle crearVehiculo(int iD_vehiculo){
        return new Vehicle(iD_vehiculo, "Honda","RB20",2.5,360,2);
    }
}
