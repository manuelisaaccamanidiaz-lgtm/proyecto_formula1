package com.formula1.circuits;

public class Circuit {

    private int idCircuito;
    private String nombre;
    private double longitud_km;
    private String descripcion;
    private byte vueltas;

    public int getIdCircuito() {
        return idCircuito;
    }

    public void setIdCircuito(int idCircuito) {
        this.idCircuito = idCircuito;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLongitud_km() {
        return longitud_km;
    }

    public void setLongitud_km(double longitud_km) {
        this.longitud_km = longitud_km;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte getVueltas() {
        return vueltas;
    }

    public void setVueltas(byte vueltas) {
        this.vueltas = vueltas;
    }

    public Circuit(int idCircuito, String nombre, double longitud_km, String descripcion, byte vueltas) {
        this.idCircuito = idCircuito;
        this.nombre = nombre;
        this.longitud_km = longitud_km;
        this.descripcion = descripcion;
        this.vueltas = vueltas;
    }

    public Circuit() {
    }

    

}
