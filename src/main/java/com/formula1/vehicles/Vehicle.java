package com.formula1.vehicles;

public class Vehicle {

    private int id_vehiculo;
    private String motor;
    private String modelo;
    private double aceleracion;
    private int velocidadMaxima;
    private int idEquipo;

    public Vehicle(int id_vehiculo,String motor,String modelo, double aceleracion, int velocidadMaxima, int idEquipo) {
        this.aceleracion = aceleracion;
        
        this.id_vehiculo = id_vehiculo;
        this.modelo = modelo;
        this.motor = motor;
        this.velocidadMaxima = velocidadMaxima;
        this.idEquipo = idEquipo;
    }



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

    public int getVelocidadMaxima() {
        return velocidadMaxima;
    }

    public void setVelocidadMaxima(int velocidadMaxima) {
        this.velocidadMaxima = velocidadMaxima;
    }
    
    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    

    public Vehicle() {
    }
}
