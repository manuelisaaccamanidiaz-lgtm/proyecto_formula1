package com.formula1.results;

public class Result {
    private int ID_result;
    private String ganador;
    private double record_vuelta;
    private double tiempo;
    private String circuito;

    public int getID_result() {
        return ID_result;
    }
    public void setID_result(int iD_result) {
        ID_result = iD_result;
    }    

    public String getGanador() {
        return ganador;
    }
    public void setGanador(String ganador) {
        this.ganador = ganador;
    }
    public double getRecord_vuelta() {
        return record_vuelta;
    }
    public void setRecord_vuelta(double record_vuelta) {
        this.record_vuelta = record_vuelta;
    }
    public double getTiempo() {
        return tiempo;
    }
    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }
    public String getCircuito() {
        return circuito;
    }
    public void setCircuito(String circuito) {
        this.circuito = circuito;
    }
    
    public Result(int iD_result, String ganador, double record_vuelta, double tiempo, String circuito) {
        ID_result = iD_result;
        this.ganador = ganador;
        this.record_vuelta = record_vuelta;
        this.tiempo = tiempo;
        this.circuito = circuito;
    }
    public Result() {
    }

}
