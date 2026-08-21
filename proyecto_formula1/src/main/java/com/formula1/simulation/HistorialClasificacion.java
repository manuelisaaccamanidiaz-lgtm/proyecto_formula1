package com.formula1.simulation;

import java.util.ArrayList;
import java.util.List;

public class HistorialClasificacion {

    private final List<SesionClasificacion> sesiones = new ArrayList<>();
    private int siguienteId = 1;

    public void guardar(String circuito, Clima clima, List<ResultadoClasificacion> resultados) {
        sesiones.add(new SesionClasificacion(siguienteId++, circuito, clima, resultados));
    }

    public List<SesionClasificacion> listar() {
        return sesiones;
    }
}
