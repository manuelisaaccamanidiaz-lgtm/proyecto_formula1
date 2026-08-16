package com.formula1.results;

public class Result {
    private int idResult;
    private double tiempo;
    private int idCircuito;
    private int idVehiculo;

    public Result(int idResult,double tiempo,int idCircuito,  int idVehiculo) {
        this.idCircuito = idCircuito;
        this.idResult = idResult;
        this.idVehiculo = idVehiculo;
        this.tiempo = tiempo;
    }

    public Result() {
    }

    public int getIdResult() {
        return idResult;
    }

    public void setIdResult(int idResult) {
        this.idResult = idResult;
    }

    public double getTiempo() {
        return tiempo;
    }

    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }

    public int getIdCircuito() {
        return idCircuito;
    }

    public void setIdCircuito(int idCircuito) {
        this.idCircuito = idCircuito;
    }

    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    

}
