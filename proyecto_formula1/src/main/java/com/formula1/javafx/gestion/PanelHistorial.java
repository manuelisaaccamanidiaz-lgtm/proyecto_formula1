package com.formula1.javafx.gestion;

import java.util.ArrayList;
import java.util.List;

import com.formula1.simulation.HistorialClasificacion;
import com.formula1.simulation.ResultadoClasificacion;
import com.formula1.simulation.SesionClasificacion;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/** Panel que lista todas las sesiones de clasificacion simuladas, sin JOptionPane. */
public class PanelHistorial {

    /** Fila "aplanada" del historial: una sesion + un resultado de esa sesion. */
    public static class FilaHistorial {
        final int idSesion;
        final String circuito;
        final String clima;
        final int posicion;
        final String piloto;
        final String equipo;
        final String tiempo;

        FilaHistorial(int idSesion, String circuito, String clima, ResultadoClasificacion r) {
            this.idSesion = idSesion;
            this.circuito = circuito;
            this.clima = clima;
            this.posicion = r.getPosicion();
            this.piloto = r.getNombrePiloto();
            this.equipo = r.getEquipo();
            int minutos = (int) (r.getTiempoVueltaSegundos() / 60);
            double segundos = r.getTiempoVueltaSegundos() % 60;
            this.tiempo = String.format("%d:%06.3f", minutos, segundos);
        }
    }

    private final HistorialClasificacion historial;
    private final TableView<FilaHistorial> tabla = new TableView<>();
    private final ObservableList<FilaHistorial> datos = FXCollections.observableArrayList();

    public PanelHistorial(HistorialClasificacion historial) {
        this.historial = historial;
    }

    public BorderPane construirVista() {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(24));
        raiz.setStyle(EstiloGestion.FONDO);
        raiz.setTop(EstiloGestion.titulo("Historial de clasificaciones"));
        BorderPane.setMargin(raiz.getTop(), new Insets(0, 0, 20, 0));

        configurarTabla();
        cargarDatos();

        Label vacio = EstiloGestion.estado();
        if (datos.isEmpty()) {
            EstiloGestion.mostrarInfo(vacio, "Todavia no hay sesiones simuladas. Prueba \"Simular clasificacion\".");
        }

        VBox centro = new VBox(10, vacio, tabla);
        raiz.setCenter(centro);
        return raiz;
    }

    private void configurarTabla() {
        tabla.setStyle(EstiloGestion.TABLA);
        tabla.setItems(datos);
        tabla.setPrefHeight(500);

        TableColumn<FilaHistorial, Number> colSesion = new TableColumn<>("Sesion");
        colSesion.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().idSesion));
        colSesion.setPrefWidth(70);

        TableColumn<FilaHistorial, String> colCircuito = new TableColumn<>("Circuito");
        colCircuito.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().circuito));
        colCircuito.setPrefWidth(220);

        TableColumn<FilaHistorial, String> colClima = new TableColumn<>("Clima");
        colClima.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().clima));
        colClima.setPrefWidth(90);

        TableColumn<FilaHistorial, Number> colPos = new TableColumn<>("Pos");
        colPos.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().posicion));
        colPos.setPrefWidth(50);

        TableColumn<FilaHistorial, String> colPiloto = new TableColumn<>("Piloto");
        colPiloto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().piloto));
        colPiloto.setPrefWidth(200);

        TableColumn<FilaHistorial, String> colEquipo = new TableColumn<>("Equipo");
        colEquipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().equipo));
        colEquipo.setPrefWidth(200);

        TableColumn<FilaHistorial, String> colTiempo = new TableColumn<>("Tiempo");
        colTiempo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tiempo));
        colTiempo.setPrefWidth(120);

        tabla.getColumns().addAll(List.of(colSesion, colCircuito, colClima, colPos, colPiloto, colEquipo, colTiempo));
    }

    private void cargarDatos() {
        List<FilaHistorial> filas = new ArrayList<>();
        for (SesionClasificacion sesion : historial.listar()) {
            for (ResultadoClasificacion r : sesion.getResultados()) {
                filas.add(new FilaHistorial(sesion.getIdSesion(), sesion.getCircuito(), sesion.getClima().name(), r));
            }
        }
        datos.setAll(filas);
    }
}
