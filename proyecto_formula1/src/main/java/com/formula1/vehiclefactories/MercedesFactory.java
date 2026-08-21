package com.formula1.vehiclefactories;

import com.formula1.factories.VehicleFactory;
import com.formula1.vehicles.Vehicle;

public class MercedesFactory implements VehicleFactory{
    @Override
    public Vehicle crearVehiculo(int iD_vehiculo){
        return new Vehicle(iD_vehiculo, "Mercedes","W15",2.6,355);
    }
}
