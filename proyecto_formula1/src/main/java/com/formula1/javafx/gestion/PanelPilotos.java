package com.formula1.javafx.gestion;

import java.util.List;

import com.formula1.managers.PilotManager;
import com.formula1.pilots.Pilot;

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
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Panel de gestion de pilotos, embebido (sin JOptionPane): tabla + formulario + buscador. */
public class PanelPilotos {

    private final PilotManager pilotManager;
    private final ObservableList<Pilot> datos = FXCollections.observableArrayList();

    private final TextField campoNombre = EstiloGestion.campo("Nombre");
    private final TextField campoRol = EstiloGestion.campo("Rol (Lider/Escudero)");
    private final TextField campoEquipo = EstiloGestion.campo("Equipo");
    private final TextField campoIdEquipo = EstiloGestion.campo("Id de equipo");
    private final TextField campoHabilidad = EstiloGestion.campo("Habilidad (1-100)");
    private final TextField campoBusqueda = EstiloGestion.campo("Buscar por nombre o equipo...");
    private final Label estado = EstiloGestion.estado();
    private final TableView<Pilot> tabla = new TableView<>();

    private Integer idSeleccionado = null;

    public PanelPilotos(PilotManager pilotManager) {
        this.pilotManager = pilotManager;
    }

    public BorderPane construirVista() {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(24));
        raiz.setStyle(EstiloGestion.FONDO);

        campoBusqueda.setMaxWidth(320);
        campoBusqueda.textProperty().addListener((obs, viejo, nuevo) -> filtrar(nuevo));
        VBox encabezado = new VBox(8, EstiloGestion.titulo("Gestion de pilotos"), campoBusqueda);
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

        TableColumn<Pilot, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getID_pilot()));
        colId.setPrefWidth(45);

        TableColumn<Pilot, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colNombre.setPrefWidth(220);

        TableColumn<Pilot, String> colRol = new TableColumn<>("Rol");
        colRol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRol()));
        colRol.setPrefWidth(120);

        TableColumn<Pilot, String> colEquipo = new TableColumn<>("Equipo");
        colEquipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEquipo()));
        colEquipo.setPrefWidth(200);

        TableColumn<Pilot, Number> colHabilidad = new TableColumn<>("Habilidad");
        colHabilidad.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getHabilidad()));
        colHabilidad.setPrefWidth(90);

        tabla.getColumns().addAll(List.of(colId, colNombre, colRol, colEquipo, colHabilidad));
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> cargarEnFormulario(nuevo));
    }

    private VBox construirFormulario() {
        GridPane grilla = new GridPane();
        grilla.setHgap(14);
        grilla.setVgap(8);
        grilla.add(EstiloGestion.etiquetaCampo("Nombre"), 0, 0);
        grilla.add(campoNombre, 0, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Rol"), 1, 0);
        grilla.add(campoRol, 1, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Equipo"), 2, 0);
        grilla.add(campoEquipo, 2, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Id de equipo"), 3, 0);
        grilla.add(campoIdEquipo, 3, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Habilidad (1-100)"), 4, 0);
        grilla.add(campoHabilidad, 4, 1);

        for (TextField t : List.of(campoNombre, campoRol, campoEquipo, campoIdEquipo, campoHabilidad)) {
            t.setPrefWidth(190);
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
        datos.setAll(pilotManager.listar());
    }

    private void filtrar(String texto) {
        datos.setAll(texto == null || texto.isBlank() ? pilotManager.listar() : pilotManager.buscarPorNombreOEquipo(texto));
    }

    private void cargarEnFormulario(Pilot p) {
        if (p == null) {
            return;
        }
        idSeleccionado = p.getID_pilot();
        campoNombre.setText(p.getNombre());
        campoRol.setText(p.getRol());
        campoEquipo.setText(p.getEquipo());
        campoIdEquipo.setText(String.valueOf(p.getIdEquipo()));
        campoHabilidad.setText(String.valueOf(p.getHabilidad()));
        EstiloGestion.mostrarInfo(estado, "Editando piloto #" + idSeleccionado);
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        tabla.getSelectionModel().clearSelection();
        for (TextField t : List.of(campoNombre, campoRol, campoEquipo, campoIdEquipo, campoHabilidad)) {
            t.clear();
        }
        EstiloGestion.mostrarInfo(estado, "Formulario listo para un piloto nuevo.");
    }

    private void guardar() {
        if (campoNombre.getText().isBlank()) {
            EstiloGestion.mostrarError(estado, "El nombre es obligatorio.");
            return;
        }
        int idEquipo;
        int habilidad;
        try {
            idEquipo = campoIdEquipo.getText().isBlank() ? 0 : Integer.parseInt(campoIdEquipo.getText().trim());
            habilidad = campoHabilidad.getText().isBlank() ? 80 : Integer.parseInt(campoHabilidad.getText().trim());
        } catch (NumberFormatException ex) {
            EstiloGestion.mostrarError(estado, "Id de equipo y habilidad deben ser numeros.");
            return;
        }

        Pilot piloto = new Pilot(idSeleccionado == null ? 0 : idSeleccionado, campoNombre.getText().trim(),
                campoRol.getText().trim(), campoEquipo.getText().trim(), idEquipo, habilidad);

        if (idSeleccionado == null) {
            pilotManager.agregar(piloto);
            EstiloGestion.mostrarInfo(estado, "Piloto agregado con id " + piloto.getID_pilot() + ".");
        } else {
            pilotManager.editar(idSeleccionado, piloto);
            EstiloGestion.mostrarInfo(estado, "Piloto #" + idSeleccionado + " actualizado.");
        }
        cargarDatos();
        limpiarFormulario();
    }

    private void eliminar() {
        Pilot seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            EstiloGestion.mostrarError(estado, "Selecciona un piloto de la tabla para eliminar.");
            return;
        }
        pilotManager.eliminar(seleccionado.getID_pilot());
        EstiloGestion.mostrarInfo(estado, "Piloto #" + seleccionado.getID_pilot() + " eliminado.");
        cargarDatos();
        limpiarFormulario();
    }
}
