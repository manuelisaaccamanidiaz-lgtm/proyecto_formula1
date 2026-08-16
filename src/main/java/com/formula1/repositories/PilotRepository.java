package com.formula1.repositories;

import java.util.List;

import com.formula1.pilots.Pilot;

public interface PilotRepository {
    void guardar(Pilot piloto);
    Pilot buscarPorId(int id);
    List<Pilot> listarTodos();
    void eliminar(int id);
}
