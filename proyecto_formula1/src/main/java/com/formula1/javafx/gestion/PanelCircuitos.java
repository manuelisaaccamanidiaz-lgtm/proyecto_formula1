package com.formula1.javafx.gestion;

import java.util.List;

import com.formula1.circuits.Circuit;
import com.formula1.managers.CircuitManager;

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
 * Panel de gestion de circuitos embebido en el area central del menu (sin
 * JOptionPane): tabla, formulario de alta/edicion y buscador, todo en la
 * misma pantalla, a la derecha de la barra lateral.
 */
public class PanelCircuitos {

    private final CircuitManager circuitManager;
    private final ObservableList<Circuit> datos = FXCollections.observableArrayList();

    private final TextField campoNombre = EstiloGestion.campo("Nombre");
    private final TextField campoPais = EstiloGestion.campo("Pais");
    private final TextField campoKm = EstiloGestion.campo("Longitud (km)");
    private final TextField campoVueltas = EstiloGestion.campo("Vueltas");
    private final TextArea campoDescripcion = new TextArea();
    private final TextField campoRecordTiempo = EstiloGestion.campo("Ej. 1:30.983");
    private final TextField campoRecordPiloto = EstiloGestion.campo("Piloto del record");
    private final TextField campoRecordAnio = EstiloGestion.campo("Anio");
    private final TextField campoBusqueda = EstiloGestion.campo("Buscar por nombre o pais...");
    private final Label estado = EstiloGestion.estado();
    private final TableView<Circuit> tabla = new TableView<>();

    private Integer idSeleccionado = null;

    public PanelCircuitos(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
    }

    public BorderPane construirVista() {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(24));
        raiz.setStyle(EstiloGestion.FONDO);

        campoBusqueda.setMaxWidth(320);
        campoBusqueda.textProperty().addListener((obs, viejo, nuevo) -> filtrar(nuevo));
        VBox encabezado = new VBox(8, EstiloGestion.titulo("Gestion de circuitos"), campoBusqueda);
        encabezado.setPadding(new Insets(0, 0, 14, 0));
        raiz.setTop(encabezado);

        configurarTabla();
        BorderPane.setMargin(tabla, new Insets(0, 0, 14, 0));
        raiz.setCenter(tabla);

        raiz.setBottom(construirFormulario());
        cargarDatos();
        return raiz;
    }

    private void configurarTabla() {
        tabla.setStyle(EstiloGestion.TABLA);
        tabla.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tabla.setItems(datos);
        tabla.setPrefHeight(320);

        TableColumn<Circuit, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getID_circuito()));
        colId.setPrefWidth(45);

        TableColumn<Circuit, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colNombre.setPrefWidth(220);

        TableColumn<Circuit, String> colPais = new TableColumn<>("Pais");
        colPais.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPais()));
        colPais.setPrefWidth(150);

        TableColumn<Circuit, Number> colKm = new TableColumn<>("Km");
        colKm.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getLongitud_km()));
        colKm.setPrefWidth(70);

        TableColumn<Circuit, Number> colVueltas = new TableColumn<>("Vueltas");
        colVueltas.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getVueltas()));
        colVueltas.setPrefWidth(70);

        TableColumn<Circuit, String> colRecord = new TableColumn<>("Record de vuelta");
        colRecord.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getRecordVueltaTiempo() + " - " + c.getValue().getRecordVueltaPiloto()
                        + " (" + c.getValue().getRecordVueltaAnio() + ")"));
        colRecord.setPrefWidth(280);

        tabla.getColumns().addAll(List.of(colId, colNombre, colPais, colKm, colVueltas, colRecord));
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> cargarEnFormulario(nuevo));
    }

    private VBox construirFormulario() {
        campoDescripcion.setPromptText("Descripcion");
        campoDescripcion.setStyle(EstiloGestion.CAMPO);
        campoDescripcion.setPrefRowCount(2);
        campoDescripcion.setWrapText(true);

        GridPane grilla = new GridPane();
        grilla.setHgap(14);
        grilla.setVgap(8);
        grilla.add(EstiloGestion.etiquetaCampo("Nombre"), 0, 0);
        grilla.add(campoNombre, 0, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Pais"), 1, 0);
        grilla.add(campoPais, 1, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Longitud (km)"), 2, 0);
        grilla.add(campoKm, 2, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Vueltas"), 3, 0);
        grilla.add(campoVueltas, 3, 1);

        grilla.add(EstiloGestion.etiquetaCampo("Descripcion"), 0, 2);
        grilla.add(campoDescripcion, 0, 3, 2, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Record - tiempo"), 2, 2);
        grilla.add(campoRecordTiempo, 2, 3);
        grilla.add(EstiloGestion.etiquetaCampo("Record - piloto"), 3, 2);
        grilla.add(campoRecordPiloto, 3, 3);
        grilla.add(EstiloGestion.etiquetaCampo("Record - anio"), 2, 4);
        grilla.add(campoRecordAnio, 2, 5);

        for (TextField t : List.of(campoNombre, campoPais, campoKm, campoVueltas,
                campoRecordTiempo, campoRecordPiloto, campoRecordAnio)) {
            t.setPrefWidth(200);
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

    private void cargarDatos() {
        datos.setAll(circuitManager.listar());
    }

    private void filtrar(String texto) {
        datos.setAll(texto == null || texto.isBlank() ? circuitManager.listar() : circuitManager.buscarPorNombreOPais(texto));
    }

    private void cargarEnFormulario(Circuit c) {
        if (c == null) {
            return;
        }
        idSeleccionado = c.getID_circuito();
        campoNombre.setText(c.getNombre());
        campoPais.setText(c.getPais());
        campoKm.setText(String.valueOf(c.getLongitud_km()));
        campoVueltas.setText(String.valueOf(c.getVueltas()));
        campoDescripcion.setText(c.getDescripcion());
        campoRecordTiempo.setText(c.getRecordVueltaTiempo());
        campoRecordPiloto.setText(c.getRecordVueltaPiloto());
        campoRecordAnio.setText(String.valueOf(c.getRecordVueltaAnio()));
        EstiloGestion.mostrarInfo(estado, "Editando circuito #" + idSeleccionado);
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        tabla.getSelectionModel().clearSelection();
        for (TextField t : List.of(campoNombre, campoPais, campoKm, campoVueltas,
                campoRecordTiempo, campoRecordPiloto, campoRecordAnio)) {
            t.clear();
        }
        campoDescripcion.clear();
        EstiloGestion.mostrarInfo(estado, "Formulario listo para un circuito nuevo.");
    }

    private void guardar() {
        if (campoNombre.getText().isBlank()) {
            EstiloGestion.mostrarError(estado, "El nombre es obligatorio.");
            return;
        }
        double km;
        byte vueltas;
        int anio;
        try {
            km = campoKm.getText().isBlank() ? 0 : Double.parseDouble(campoKm.getText().trim().replace(",", "."));
            vueltas = campoVueltas.getText().isBlank() ? 0 : Byte.parseByte(campoVueltas.getText().trim());
            anio = campoRecordAnio.getText().isBlank() ? 0 : Integer.parseInt(campoRecordAnio.getText().trim());
        } catch (NumberFormatException ex) {
            EstiloGestion.mostrarError(estado, "Revisa que km, vueltas y anio sean numeros validos.");
            return;
        }

        Circuit circuito = new Circuit(idSeleccionado == null ? 0 : idSeleccionado, campoNombre.getText().trim(),
                campoPais.getText().trim(), km, vueltas, campoDescripcion.getText().trim(),
                campoRecordTiempo.getText().trim(), campoRecordPiloto.getText().trim(), anio);

        if (idSeleccionado == null) {
            circuitManager.agregar(circuito);
            EstiloGestion.mostrarInfo(estado, "Circuito agregado con id " + circuito.getID_circuito() + ".");
        } else {
            circuitManager.editar(idSeleccionado, circuito);
            EstiloGestion.mostrarInfo(estado, "Circuito #" + idSeleccionado + " actualizado.");
        }
        cargarDatos();
        limpiarFormulario();
    }

    private void eliminar() {
        Circuit seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            EstiloGestion.mostrarError(estado, "Selecciona un circuito de la tabla para eliminar.");
            return;
        }
        circuitManager.eliminar(seleccionado.getID_circuito());
        EstiloGestion.mostrarInfo(estado, "Circuito #" + seleccionado.getID_circuito() + " eliminado.");
        cargarDatos();
        limpiarFormulario();
    }
}
