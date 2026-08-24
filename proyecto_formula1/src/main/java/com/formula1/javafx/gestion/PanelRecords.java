package com.formula1.javafx.gestion;

import java.util.List;

import com.formula1.results.Result;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

/** Panel de solo lectura con los records de vuelta historicos por circuito, sin JOptionPane. */
public class PanelRecords {

    private final List<Result> records;
    private final TableView<Result> tabla = new TableView<>();

    public PanelRecords(List<Result> records) {
        this.records = records;
    }

    public BorderPane construirVista() {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(24));
        raiz.setStyle(EstiloGestion.FONDO);
        raiz.setTop(EstiloGestion.titulo("Records de vuelta por circuito"));
        BorderPane.setMargin(raiz.getTop(), new Insets(0, 0, 20, 0));

        configurarTabla();
        raiz.setCenter(tabla);
        return raiz;
    }

    private void configurarTabla() {
        tabla.setStyle(EstiloGestion.TABLA);
        tabla.setPrefHeight(500);

        ObservableList<Result> datos = FXCollections.observableArrayList(records);
        tabla.setItems(datos);

        TableColumn<Result, String> colCircuito = new TableColumn<>("Circuito");
        colCircuito.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCircuito()));
        colCircuito.setPrefWidth(240);

        TableColumn<Result, String> colGanador = new TableColumn<>("Ganador historico");
        colGanador.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGanador()));
        colGanador.setPrefWidth(220);

        TableColumn<Result, String> colRecord = new TableColumn<>("Record de vuelta");
        colRecord.setCellValueFactory(c -> {
            double segundosTotales = c.getValue().getRecord_vuelta();
            int minutos = (int) (segundosTotales / 60);
            double segundos = segundosTotales % 60;
            return new SimpleStringProperty(String.format("%d:%06.3f", minutos, segundos));
        });
        colRecord.setPrefWidth(160);

        TableColumn<Result, String> colTiempo = new TableColumn<>("Tiempo total historico");
        colTiempo.setCellValueFactory(c -> {
            double segundosTotales = c.getValue().getTiempo();
            int minutos = (int) (segundosTotales / 60);
            double segundos = segundosTotales % 60;
            return new SimpleStringProperty(String.format("%d:%06.3f", minutos, segundos));
        });
        colTiempo.setPrefWidth(180);

        tabla.getColumns().addAll(List.of(colCircuito, colGanador, colRecord, colTiempo));
    }
}
