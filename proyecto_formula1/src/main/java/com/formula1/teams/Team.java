package com.formula1.teams;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private int ID_equipo;
    private String nombre;
    private String pais;
    private String motor;
    private List<Integer> idsPilotos;

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

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public List<Integer> getIdsPilotos() {
        return idsPilotos;
    }

    public void setIdsPilotos(List<Integer> idsPilotos) {
        this.idsPilotos = idsPilotos;
    }

    /** Constructor original (se mantiene por compatibilidad). */
    public Team(int iD_equipo, String nombre, String pais) {
        this.ID_equipo = iD_equipo;
        this.nombre = nombre;
        this.pais = pais;
        this.motor = "-";
        this.idsPilotos = new ArrayList<>();
    }

    /** Constructor completo, usado por Main.java (gestion CRUD de equipos). */
    public Team(int iD_equipo, String nombre, String pais, String motor, List<Integer> idsPilotos) {
        this.ID_equipo = iD_equipo;
        this.nombre = nombre;
        this.pais = pais;
        this.motor = motor;
        this.idsPilotos = idsPilotos != null ? idsPilotos : new ArrayList<>();
    }

    public Team() {
        this.idsPilotos = new ArrayList<>();
    }

    @Override
    public String toString() {
        return String.format("#%d %s (%s) - Motor: %s | Pilotos: %s",
                ID_equipo, nombre, pais, motor, idsPilotos);
    }
}
