package com.formula1.circuits;

public class Circuit {

    private int ID_circuito;
    private String nombre;
    private String pais;
    private double longitud_km;
    private String descripcion;
    private byte vueltas;
    private String recordVueltaTiempo;
    private String recordVueltaPiloto;
    private int recordVueltaAnio;

    public int getID_circuito() {
        return ID_circuito;
    }

    public void setID_circuito(int iD_circuito) {
        ID_circuito = iD_circuito;
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

    public String getRecordVueltaTiempo() {
        return recordVueltaTiempo;
    }

    public void setRecordVueltaTiempo(String recordVueltaTiempo) {
        this.recordVueltaTiempo = recordVueltaTiempo;
    }

    public String getRecordVueltaPiloto() {
        return recordVueltaPiloto;
    }

    public void setRecordVueltaPiloto(String recordVueltaPiloto) {
        this.recordVueltaPiloto = recordVueltaPiloto;
    }

    public int getRecordVueltaAnio() {
        return recordVueltaAnio;
    }

    public void setRecordVueltaAnio(int recordVueltaAnio) {
        this.recordVueltaAnio = recordVueltaAnio;
    }

    /** Constructor original (se mantiene por compatibilidad, ej. RaceTrackApp.java). */
    public Circuit(int iD_circuito, String nombre, double longitud_km, String descripcion, byte vueltas) {
        this.ID_circuito = iD_circuito;
        this.nombre = nombre;
        this.pais = "-";
        this.longitud_km = longitud_km;
        this.descripcion = descripcion;
        this.vueltas = vueltas;
        this.recordVueltaTiempo = "-";
        this.recordVueltaPiloto = "-";
        this.recordVueltaAnio = 0;
    }

    /** Constructor completo, usado por Main.java (gestion CRUD de circuitos). */
    public Circuit(int iD_circuito, String nombre, String pais, double longitud_km, byte vueltas,
                    String descripcion, String recordVueltaTiempo, String recordVueltaPiloto, int recordVueltaAnio) {
        this.ID_circuito = iD_circuito;
        this.nombre = nombre;
        this.pais = pais;
        this.longitud_km = longitud_km;
        this.vueltas = vueltas;
        this.descripcion = descripcion;
        this.recordVueltaTiempo = recordVueltaTiempo;
        this.recordVueltaPiloto = recordVueltaPiloto;
        this.recordVueltaAnio = recordVueltaAnio;
    }

    public Circuit() {
    }

    @Override
    public String toString() {
        return String.format("#%d %s (%s) - %.2f km, %d vueltas | Record: %s por %s (%d)",
                ID_circuito, nombre, pais, longitud_km, vueltas,
                recordVueltaTiempo, recordVueltaPiloto, recordVueltaAnio);
    }
}
