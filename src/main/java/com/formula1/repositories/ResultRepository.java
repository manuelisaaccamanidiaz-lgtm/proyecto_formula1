package com.formula1.repositories;

import java.util.List;

import com.formula1.results.Result;

public interface ResultRepository {
    void guardar(Result resultado);
    Result buscarPorId(int id);
    List<Result> listarTodos();
    void eliminar(int id);
}
