package com.formula1.javafx.gestion;

import java.util.ArrayList;
import java.util.List;

import com.formula1.circuits.Circuit;
import com.formula1.config.ConfiguracionManager;
import com.formula1.config.ConfiguracionVehiculo;
import com.formula1.managers.CircuitManager;
import com.formula1.managers.PilotManager;
import com.formula1.managers.VehicleManager;
import com.formula1.pilots.Pilot;
import com.formula1.simulation.Clima;
import com.formula1.simulation.HistorialClasificacion;
import com.formula1.simulation.Participante;
import com.formula1.simulation.ResultadoClasificacion;
import com.formula1.simulation.SimuladorClasificacion;
import com.formula1.vehicles.Vehicle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/** Panel para simular una sesion de clasificacion en un circuito elegido, sin JOptionPane. */
public class PanelSimulacionClasificacion {

    private final CircuitManager circuitManager;
    private final PilotManager pilotManager;
    private final VehicleManager vehicleManager;
    private final ConfiguracionManager configManager;
    private final HistorialClasificacion historial;

    private final ComboBox<Circuit> comboCircuito = new ComboBox<>();
    private final Label climaElegido = EstiloGestion.estado();
    private final Label estado = EstiloGestion.estado();
    private final TableView<ResultadoClasificacion> tabla = new TableView<>();
    private final ObservableList<ResultadoClasificacion> datos = FXCollections.observableArrayList();

    public PanelSimulacionClasificacion(CircuitManager circuitManager, PilotManager pilotManager,
                                         VehicleManager vehicleManager, ConfiguracionManager configManager,
                                         HistorialClasificacion historial) {
        this.circuitManager = circuitManager;
        this.pilotManager = pilotManager;
        this.vehicleManager = vehicleManager;
        this.configManager = configManager;
        this.historial = historial;
    }

    public BorderPane construirVista() {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(24));
        raiz.setStyle(EstiloGestion.FONDO);
        raiz.setTop(EstiloGestion.titulo("Simular clasificacion"));
        BorderPane.setMargin(raiz.getTop(), new Insets(0, 0, 20, 0));

        comboCircuito.getItems().setAll(circuitManager.listar());
        comboCircuito.setConverter(convertidorCircuito());
        comboCircuito.setPrefWidth(340);
        if (!comboCircuito.getItems().isEmpty()) {
            comboCircuito.getSelectionModel().selectFirst();
        }

        var botonSimular = EstiloGestion.botonPrimario("Simular clasificacion (clima aleatorio)");
        botonSimular.setOnAction(e -> simular());

        HBox controles = new HBox(14, comboCircuito, botonSimular);
        controles.setAlignment(Pos.CENTER_LEFT);

        VBox encabezadoTabla = new VBox(8, controles, climaElegido, estado);
        encabezadoTabla.setPadding(new Insets(0, 0, 14, 0));

        configurarTabla();
        VBox centro = new VBox(14, encabezadoTabla, tabla);
        raiz.setCenter(centro);
        return raiz;
    }

    private StringConverter<Circuit> convertidorCircuito() {
        return new StringConverter<>() {
            @Override
            public String toString(Circuit c) {
                return c == null ? "" : c.getNombre() + " (" + c.getPais() + ")";
            }

            @Override
            public Circuit fromString(String s) {
                return null;
            }
        };
    }

    private void configurarTabla() {
        tabla.setStyle(EstiloGestion.TABLA);
        tabla.setItems(datos);
        tabla.setPrefHeight(400);

        TableColumn<ResultadoClasificacion, Number> colPos = new TableColumn<>("Pos");
        colPos.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPosicion()));
        colPos.setPrefWidth(50);

        TableColumn<ResultadoClasificacion, String> colPiloto = new TableColumn<>("Piloto");
        colPiloto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombrePiloto()));
        colPiloto.setPrefWidth(220);

        TableColumn<ResultadoClasificacion, String> colEquipo = new TableColumn<>("Equipo");
        colEquipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEquipo()));
        colEquipo.setPrefWidth(220);

        TableColumn<ResultadoClasificacion, String> colTiempo = new TableColumn<>("Tiempo de vuelta");
        colTiempo.setCellValueFactory(c -> new SimpleStringProperty(formatoTiempo(c.getValue().getTiempoVueltaSegundos())));
        colTiempo.setPrefWidth(160);

        tabla.getColumns().addAll(List.of(colPos, colPiloto, colEquipo, colTiempo));
    }

    private String formatoTiempo(double segundosTotales) {
        int minutos = (int) (segundosTotales / 60);
        double segundos = segundosTotales % 60;
        return String.format("%d:%06.3f", minutos, segundos);
    }

    private void simular() {
        Circuit circuito = comboCircuito.getValue();
        if (circuito == null) {
            EstiloGestion.mostrarError(estado, "No hay circuitos disponibles.");
            return;
        }

        Clima clima = Clima.aleatorio();
        List<Participante> participantes = new ArrayList<>();
        for (Vehicle v : vehicleManager.listar()) {
            ConfiguracionVehiculo config = configManager.obtener(v.getId_vehiculo());
            for (Integer idPiloto : v.getIdsPilotos()) {
                Pilot piloto = pilotManager.buscarPorId(idPiloto);
                if (piloto != null) {
                    participantes.add(new Participante(piloto, v, config));
                }
            }
        }

        if (participantes.isEmpty()) {
            EstiloGestion.mostrarError(estado, "No hay pilotos con vehiculo asignado. Asignalos primero en Gestionar vehiculos.");
            return;
        }

        SimuladorClasificacion simulador = new SimuladorClasificacion();
        List<ResultadoClasificacion> resultados = simulador.simular(circuito, participantes, clima);
        historial.guardar(circuito.getNombre(), clima, resultados);

        datos.setAll(resultados);
        climaElegido.setText("Clima de la sesion: " + clima);
        EstiloGestion.mostrarInfo(estado, "Clasificacion simulada y guardada en el historial.");
    }
}
