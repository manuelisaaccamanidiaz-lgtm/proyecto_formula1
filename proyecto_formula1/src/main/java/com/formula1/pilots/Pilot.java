package com.formula1.pilots;

public class Pilot {

    private int ID_pilot;
    private String nombre;
    private String rol;
    private String equipo;
    private int idEquipo;
    private int habilidad; // 1-100

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

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public int getHabilidad() {
        return habilidad;
    }

    public void setHabilidad(int habilidad) {
        this.habilidad = habilidad;
    }

    /** Constructor original (se mantiene por compatibilidad, ej. RaceTrackApp.java). */
    public Pilot(int iD_pilot, String nombre, String rol) {
        this.ID_pilot = iD_pilot;
        this.nombre = nombre;
        this.rol = rol;
        this.equipo = "-";
        this.idEquipo = 0;
        this.habilidad = 80;
    }

    /** Constructor completo, usado por Main.java (gestion CRUD de pilotos). */
    public Pilot(int iD_pilot, String nombre, String rol, String equipo, int idEquipo, int habilidad) {
        this.ID_pilot = iD_pilot;
        this.nombre = nombre;
        this.rol = rol;
        this.equipo = equipo;
        this.idEquipo = idEquipo;
        this.habilidad = habilidad;
    }

    public Pilot() {
    }

    @Override
    public String toString() {
        return String.format("#%d %s - %s (%s) | Habilidad: %d", ID_pilot, nombre, equipo, rol, habilidad);
    }
}
