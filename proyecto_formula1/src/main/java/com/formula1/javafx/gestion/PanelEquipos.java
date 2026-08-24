package com.formula1.javafx.gestion;

import java.util.ArrayList;
import java.util.List;

import com.formula1.managers.TeamManager;
import com.formula1.teams.Team;

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

/** Panel de gestion de equipos, embebido (sin JOptionPane): tabla + formulario + buscador. */
public class PanelEquipos {

    private final TeamManager teamManager;
    private final ObservableList<Team> datos = FXCollections.observableArrayList();

    private final TextField campoNombre = EstiloGestion.campo("Nombre");
    private final TextField campoPais = EstiloGestion.campo("Pais");
    private final TextField campoMotor = EstiloGestion.campo("Motor");
    private final TextField campoBusqueda = EstiloGestion.campo("Buscar por nombre o pais...");
    private final Label estado = EstiloGestion.estado();
    private final TableView<Team> tabla = new TableView<>();

    private Integer idSeleccionado = null;

    public PanelEquipos(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public BorderPane construirVista() {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(24));
        raiz.setStyle(EstiloGestion.FONDO);

        campoBusqueda.setMaxWidth(320);
        campoBusqueda.textProperty().addListener((obs, viejo, nuevo) -> filtrar(nuevo));
        VBox encabezado = new VBox(8, EstiloGestion.titulo("Gestion de equipos"), campoBusqueda);
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

        TableColumn<Team, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getID_equipo()));
        colId.setPrefWidth(45);

        TableColumn<Team, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colNombre.setPrefWidth(220);

        TableColumn<Team, String> colPais = new TableColumn<>("Pais");
        colPais.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPais()));
        colPais.setPrefWidth(150);

        TableColumn<Team, String> colMotor = new TableColumn<>("Motor");
        colMotor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMotor()));
        colMotor.setPrefWidth(150);

        TableColumn<Team, String> colPilotos = new TableColumn<>("Ids pilotos");
        colPilotos.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIdsPilotos())));
        colPilotos.setPrefWidth(180);

        tabla.getColumns().addAll(List.of(colId, colNombre, colPais, colMotor, colPilotos));
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> cargarEnFormulario(nuevo));
    }

    private VBox construirFormulario() {
        GridPane grilla = new GridPane();
        grilla.setHgap(14);
        grilla.setVgap(8);
        grilla.add(EstiloGestion.etiquetaCampo("Nombre"), 0, 0);
        grilla.add(campoNombre, 0, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Pais"), 1, 0);
        grilla.add(campoPais, 1, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Motor"), 2, 0);
        grilla.add(campoMotor, 2, 1);

        for (TextField t : List.of(campoNombre, campoPais, campoMotor)) {
            t.setPrefWidth(220);
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
        datos.setAll(teamManager.listar());
    }

    private void filtrar(String texto) {
        datos.setAll(texto == null || texto.isBlank() ? teamManager.listar() : teamManager.buscarPorNombreOPais(texto));
    }

    private void cargarEnFormulario(Team t) {
        if (t == null) {
            return;
        }
        idSeleccionado = t.getID_equipo();
        campoNombre.setText(t.getNombre());
        campoPais.setText(t.getPais());
        campoMotor.setText(t.getMotor());
        EstiloGestion.mostrarInfo(estado, "Editando equipo #" + idSeleccionado);
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        tabla.getSelectionModel().clearSelection();
        for (TextField t : List.of(campoNombre, campoPais, campoMotor)) {
            t.clear();
        }
        EstiloGestion.mostrarInfo(estado, "Formulario listo para un equipo nuevo.");
    }

    private void guardar() {
        if (campoNombre.getText().isBlank()) {
            EstiloGestion.mostrarError(estado, "El nombre es obligatorio.");
            return;
        }
        List<Integer> idsPilotosPrevios = new ArrayList<>();
        if (idSeleccionado != null) {
            Team existente = teamManager.buscarPorId(idSeleccionado);
            if (existente != null) {
                idsPilotosPrevios = existente.getIdsPilotos();
            }
        }
        Team equipo = new Team(idSeleccionado == null ? 0 : idSeleccionado, campoNombre.getText().trim(),
                campoPais.getText().trim(), campoMotor.getText().trim(), idsPilotosPrevios);

        if (idSeleccionado == null) {
            teamManager.agregar(equipo);
            EstiloGestion.mostrarInfo(estado, "Equipo agregado con id " + equipo.getID_equipo() + ".");
        } else {
            teamManager.editar(idSeleccionado, equipo);
            EstiloGestion.mostrarInfo(estado, "Equipo #" + idSeleccionado + " actualizado.");
        }
        cargarDatos();
        limpiarFormulario();
    }

    private void eliminar() {
        Team seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            EstiloGestion.mostrarError(estado, "Selecciona un equipo de la tabla para eliminar.");
            return;
        }
        teamManager.eliminar(seleccionado.getID_equipo());
        EstiloGestion.mostrarInfo(estado, "Equipo #" + seleccionado.getID_equipo() + " eliminado.");
        cargarDatos();
        limpiarFormulario();
    }
}
