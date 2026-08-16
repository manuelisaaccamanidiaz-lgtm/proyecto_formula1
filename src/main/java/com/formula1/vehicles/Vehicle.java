package com.formula1.vehicles;

public class Vehicle {

    private int id_vehiculo;
    private String motor;
    private String modelo;
    private double aceleracion;
    private int velocidad_maxima;

    public int getId_vehiculo() {
        return id_vehiculo;
    }

    public void setId_vehiculo(int id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }
    
    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getAceleracion() {
        return aceleracion;
    }

    public void setAceleracion(double aceleracion) {
        this.aceleracion = aceleracion;
    }

    public int getVelocidad_maxima() {
        return velocidad_maxima;
    }

    public void setVelocidad_maxima(int velocidad_maxima) {
        this.velocidad_maxima = velocidad_maxima;
    }

    public Vehicle(int id_vehiculo, String motor, String modelo, double aceleracion, int velocidad_maxima) {
        this.id_vehiculo = id_vehiculo;
        this.motor = motor;
        this.modelo = modelo;
        this.aceleracion = aceleracion;
        this.velocidad_maxima = velocidad_maxima;
    }

    public Vehicle() {
    }

}
