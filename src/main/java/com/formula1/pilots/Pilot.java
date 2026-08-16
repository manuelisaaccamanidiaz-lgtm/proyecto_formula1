package com.formula1.pilots;

public class Pilot {

    private int ID_pilot;
    private String nombre;
    private String rol;

    public int getID_pilot() {
        return ID_pilot;
    }

    public void setID_pilot(int iD_pilot) {
        ID_pilot = iD_pilot;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Pilot(int iD_pilot, String nombre, String rol) {
        ID_pilot = iD_pilot;
        this.nombre = nombre;
        this.rol = rol;
    }

    public Pilot() {
    }

}
