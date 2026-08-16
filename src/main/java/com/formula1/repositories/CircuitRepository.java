package com.formula1.repositories;

import java.util.List;

import com.formula1.circuits.Circuit;

public interface CircuitRepository {
    void guardar(Circuit circuito);
    Circuit buscarPorId(int id);
    List<Circuit> listarTodos();
    void eliminar(int id);
}
