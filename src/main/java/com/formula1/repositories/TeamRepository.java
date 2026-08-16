package com.formula1.repositories;

import java.util.List;

import com.formula1.teams.Team;

public interface TeamRepository {
    void guardar(Team equipo);
    Team buscarPorId(int id);
    List<Team> listarTodos();
    void eliminar(int id);
}
