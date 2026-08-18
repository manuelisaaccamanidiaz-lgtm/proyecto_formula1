package com.formula1.simulation;

import java.util.List;

public class SesionClasificacion {

    private final int idSesion;
    private final String circuito;
    private final Clima clima;
    private final List<ResultadoClasificacion> resultados;

    public SesionClasificacion(int idSesion, String circuito, Clima clima, List<ResultadoClasificacion> resultados) {
        this.idSesion = idSesion;
        this.circuito = circuito;
        this.clima = clima;
        this.resultados = resultados;
    }

    public int getIdSesion() {
        return idSesion;
    }

    public String getCircuito() {
        return circuito;
    }

    public Clima getClima() {
        return clima;
    }

    public List<ResultadoClasificacion> getResultados() {
        return resultados;
    }
}
