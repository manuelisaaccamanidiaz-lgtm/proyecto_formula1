package com.formula1.javafx.gestion;

import java.util.ArrayList;
import java.util.List;

import com.formula1.managers.VehicleManager;
import com.formula1.vehicles.Vehicle;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Panel de gestion de vehiculos, embebido (sin JOptionPane): tabla,
 * formulario de alta/edicion, buscador, asignar piloto y comparar
 * vehiculos seleccionados.
 */
public class PanelVehiculos {

    private final VehicleManager vehicleManager;
    private final ObservableList<Vehicle> datos = FXCollections.observableArrayList();

    private final TextField campoMotor = EstiloGestion.campo("Motor");
    private final TextField campoModelo = EstiloGestion.campo("Modelo");
    private final TextField campoAceleracion = EstiloGestion.campo("Aceleracion 0-100 (s)");
    private final TextField campoVelocidad = EstiloGestion.campo("Velocidad maxima (km/h)");
    private final TextField campoEquipo = EstiloGestion.campo("Equipo");
    private final TextField campoBusqueda = EstiloGestion.campo("Buscar por modelo o equipo...");

    private final TextField campoIdPilotoAsignar = EstiloGestion.campo("Id del piloto a asignar");
    private final TextArea areaComparacion = new TextArea();

    private final Label estado = EstiloGestion.estado();
    private final TableView<Vehicle> tabla = new TableView<>();

    private Integer idSeleccionado = null;

    public PanelVehiculos(VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    public BorderPane construirVista() {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(24));
        raiz.setStyle(EstiloGestion.FONDO);

        campoBusqueda.setMaxWidth(320);
        campoBusqueda.textProperty().addListener((obs, viejo, nuevo) -> filtrar(nuevo));
        VBox encabezado = new VBox(8, EstiloGestion.titulo("Gestion de vehiculos"), campoBusqueda);
        encabezado.setPadding(new Insets(0, 0, 14, 0));
        raiz.setTop(encabezado);

        configurarTabla();
        BorderPane.setMargin(tabla, new Insets(0, 0, 14, 0));
        raiz.setCenter(tabla);

        raiz.setBottom(new VBox(14, construirFormulario(), construirHerramientas()));
        cargarDatos();
        return raiz;
    }

    private void configurarTabla() {
        tabla.setStyle(EstiloGestion.TABLA);
        tabla.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tabla.setItems(datos);
        tabla.setPrefHeight(280);

        TableColumn<Vehicle, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId_vehiculo()));
        colId.setPrefWidth(45);

        TableColumn<Vehicle, String> colMotor = new TableColumn<>("Motor");
        colMotor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMotor()));
        colMotor.setPrefWidth(140);

        TableColumn<Vehicle, String> colModelo = new TableColumn<>("Modelo");
        colModelo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getModelo()));
        colModelo.setPrefWidth(140);

        TableColumn<Vehicle, String> colEquipo = new TableColumn<>("Equipo");
        colEquipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEquipo()));
        colEquipo.setPrefWidth(180);

        TableColumn<Vehicle, Number> colVelocidad = new TableColumn<>("Vel. max");
        colVelocidad.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getVelocidad_maxima()));
        colVelocidad.setPrefWidth(80);

        TableColumn<Vehicle, Number> colAcel = new TableColumn<>("Acel 0-100");
        colAcel.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getAceleracion()));
        colAcel.setPrefWidth(90);

        TableColumn<Vehicle, String> colPilotos = new TableColumn<>("Pilotos asignados");
        colPilotos.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIdsPilotos())));
        colPilotos.setPrefWidth(160);

        tabla.getColumns().addAll(List.of(colId, colMotor, colModelo, colEquipo, colVelocidad, colAcel, colPilotos));
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> cargarEnFormulario(nuevo));
    }

    private VBox construirFormulario() {
        GridPane grilla = new GridPane();
        grilla.setHgap(14);
        grilla.setVgap(8);
        grilla.add(EstiloGestion.etiquetaCampo("Motor"), 0, 0);
        grilla.add(campoMotor, 0, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Modelo"), 1, 0);
        grilla.add(campoModelo, 1, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Equipo"), 2, 0);
        grilla.add(campoEquipo, 2, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Aceleracion 0-100 (s)"), 3, 0);
        grilla.add(campoAceleracion, 3, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Velocidad maxima (km/h)"), 4, 0);
        grilla.add(campoVelocidad, 4, 1);

        for (TextField t : List.of(campoMotor, campoModelo, campoEquipo, campoAceleracion, campoVelocidad)) {
            t.setPrefWidth(170);
        }

        var botonNuevo = EstiloGestion.botonSecundario("Nuevo");
        botonNuevo.setOnAction(e -> limpiarFormulario());
        var botonGuardar = EstiloGestion.botonPrimario("Guardar");
        botonGuardar.setOnAction(e -> guardar());
        var botonEliminar = EstiloGestion.botonPeligro("Eliminar seleccionado");
        botonEliminar.setOnAction(e -> eliminar());

        HBox botones = new HBox(10, botonNuevo, botonGuardar, botonEliminar);
        botones.setAlignment(Pos.CENTER_LEFT);

        VBox contenedor = new VBox(12, grilla, botones, estado);
        contenedor.setPadding(new Insets(16));
        contenedor.setStyle(EstiloGestion.TARJETA);
        return contenedor;
    }

    private VBox construirHerramientas() {
        campoIdPilotoAsignar.setPrefWidth(200);
        var botonAsignar = EstiloGestion.botonSecundario("Asignar piloto al vehiculo seleccionado");
        botonAsignar.setOnAction(e -> asignarPiloto());
        HBox filaAsignar = new HBox(10, EstiloGestion.etiquetaCampo("Id piloto:"), campoIdPilotoAsignar, botonAsignar);
        filaAsignar.setAlignment(Pos.CENTER_LEFT);

        areaComparacion.setEditable(false);
        areaComparacion.setPrefRowCount(4);
        areaComparacion.setStyle(EstiloGestion.CAMPO + "-fx-font-family: monospace;");
        var botonComparar = EstiloGestion.botonSecundario("Comparar vehiculos seleccionados (ctrl+click)");
        botonComparar.setOnAction(e -> comparar());

        VBox contenedor = new VBox(10, filaAsignar, botonComparar, areaComparacion);
        contenedor.setPadding(new Insets(16));
        contenedor.setStyle(EstiloGestion.TARJETA);
        return contenedor;
    }

    private void cargarDatos() {
        datos.setAll(vehicleManager.listar());
    }

    private void filtrar(String texto) {
        datos.setAll(texto == null || texto.isBlank() ? vehicleManager.listar() : vehicleManager.buscarPorEquipoOModelo(texto));
    }

    private void cargarEnFormulario(Vehicle v) {
        if (v == null) {
            return;
        }
        idSeleccionado = v.getId_vehiculo();
        campoMotor.setText(v.getMotor());
        campoModelo.setText(v.getModelo());
        campoEquipo.setText(v.getEquipo());
        campoAceleracion.setText(String.valueOf(v.getAceleracion()));
        campoVelocidad.setText(String.valueOf(v.getVelocidad_maxima()));
        EstiloGestion.mostrarInfo(estado, "Editando vehiculo #" + idSeleccionado);
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        tabla.getSelectionModel().clearSelection();
        for (TextField t : List.of(campoMotor, campoModelo, campoEquipo, campoAceleracion, campoVelocidad)) {
            t.clear();
        }
        EstiloGestion.mostrarInfo(estado, "Formulario listo para un vehiculo nuevo.");
    }

    private void guardar() {
        if (campoModelo.getText().isBlank()) {
            EstiloGestion.mostrarError(estado, "El modelo es obligatorio.");
            return;
        }
        double aceleracion;
        int velocidad;
        try {
            aceleracion = campoAceleracion.getText().isBlank() ? 0 : Double.parseDouble(campoAceleracion.getText().trim().replace(",", "."));
            velocidad = campoVelocidad.getText().isBlank() ? 0 : Integer.parseInt(campoVelocidad.getText().trim());
        } catch (NumberFormatException ex) {
            EstiloGestion.mostrarError(estado, "Aceleracion y velocidad deben ser numeros validos.");
            return;
        }

        if (idSeleccionado == null) {
            Vehicle nuevo = new Vehicle(0, campoMotor.getText().trim(), campoModelo.getText().trim(), aceleracion, velocidad);
            nuevo.setEquipo(campoEquipo.getText().trim());
            vehicleManager.agregar(nuevo);
            EstiloGestion.mostrarInfo(estado, "Vehiculo agregado con id " + nuevo.getId_vehiculo() + ".");
        } else {
            Vehicle existente = vehicleManager.buscarPorId(idSeleccionado);
            if (existente != null) {
                existente.setMotor(campoMotor.getText().trim());
                existente.setModelo(campoModelo.getText().trim());
                existente.setEquipo(campoEquipo.getText().trim());
                existente.setAceleracion(aceleracion);
                existente.setVelocidad_maxima(velocidad);
                vehicleManager.editar(idSeleccionado, existente);
                EstiloGestion.mostrarInfo(estado, "Vehiculo #" + idSeleccionado + " actualizado.");
            }
        }
        cargarDatos();
        limpiarFormulario();
    }

    private void eliminar() {
        Vehicle seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            EstiloGestion.mostrarError(estado, "Selecciona un vehiculo de la tabla para eliminar.");
            return;
        }
        vehicleManager.eliminar(seleccionado.getId_vehiculo());
        EstiloGestion.mostrarInfo(estado, "Vehiculo #" + seleccionado.getId_vehiculo() + " eliminado.");
        cargarDatos();
        limpiarFormulario();
    }

    private void asignarPiloto() {
        Vehicle seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            EstiloGestion.mostrarError(estado, "Selecciona un vehiculo de la tabla primero.");
            return;
        }
        try {
            int idPiloto = Integer.parseInt(campoIdPilotoAsignar.getText().trim());
            vehicleManager.asignarPiloto(seleccionado.getId_vehiculo(), idPiloto);
            EstiloGestion.mostrarInfo(estado, "Piloto #" + idPiloto + " asignado al vehiculo #" + seleccionado.getId_vehiculo() + ".");
            cargarDatos();
        } catch (NumberFormatException ex) {
            EstiloGestion.mostrarError(estado, "Ingresa un id de piloto valido.");
        }
    }

    private void comparar() {
        List<Integer> ids = new ArrayList<>();
        for (Vehicle v : tabla.getSelectionModel().getSelectedItems()) {
            ids.add(v.getId_vehiculo());
        }
        if (ids.isEmpty()) {
            EstiloGestion.mostrarError(estado, "Selecciona al menos un vehiculo (ctrl+click) para comparar.");
            return;
        }
        areaComparacion.setText(vehicleManager.comparar(ids));
    }
}
