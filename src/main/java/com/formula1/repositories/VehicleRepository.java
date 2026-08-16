package com.formula1.repositories;

import java.util.List;

import com.formula1.vehicles.Vehicle;

public interface VehicleRepository {
    void guardar(Vehicle vehiculo);
    Vehicle buscarPorId(int id);
    List<Vehicle> listarTodos();
    void eliminar(int id);
}