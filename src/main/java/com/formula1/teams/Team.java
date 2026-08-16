package com.formula1.teams;

public class Team {

    private int ID_equipo;
    private String nombre;
    private String pais;

    public int getID_equipo() {
        return ID_equipo;
    }

    public void setID_equipo(int iD_equipo) {
        ID_equipo = iD_equipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Team(int iD_equipo, String nombre, String pais) {
        ID_equipo = iD_equipo;
        this.nombre = nombre;
        this.pais = pais;
    }

    public Team() {
    }

}
