package com.formula1.pilots;

public class Pilot {

    private int idPiloto;
    private String nombre;
    private String rol;
    private int idEquipo;
    private int idVehiculo;

    public Pilot(int idPiloto,String nombre,String rol, int idEquipo,  int idVehiculo) {
        this.idEquipo = idEquipo;
        this.idPiloto = idPiloto;
        this.idVehiculo = idVehiculo;
        this.nombre = nombre;
        this.rol = rol;
    }


    public int getIdPiloto() {
        return idPiloto;
    }

    public void setIdPiloto(int idPiloto) {
        this.idPiloto = idPiloto;
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

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }
    

    public Pilot() {
    }

    

}
