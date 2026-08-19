package com.formula1.javafx;

import java.util.List;

import com.formula1.model.EstadoVehiculo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Tabla de clasificacion (POS | PILOTO | EQUIPO | VUELTA | VELOCIDAD |
 * TIEMPO | DIFERENCIA) que se refresca en cada cuadro de la simulacion.
 *
 * Se usa TableView con una ObservableList "fila-vista" (FilaClasificacion),
 * en vez de manipular Labels sueltos: es el mecanismo estandar de JavaFX
 * para listas que cambian, y evita reconstruir toda la tabla en cada cuadro
 * (solo se actualizan los VALORES de las filas ya existentes).
 */
public class ClasificacionView {

    /** Fila "de solo lectura" que expone getters para el PropertyValueFactory de la tabla. */
    public static class FilaClasificacion {
        private final int posicion;
        private final String piloto;
        private final String equipo;
        private final String vuelta;
        private final String velocidad;
        private final String tiempo;
        private final String diferencia;

        FilaClasificacion(int posicion, String piloto, String equipo, String vuelta,
                           String velocidad, String tiempo, String diferencia) {
            this.posicion = posicion;
            this.piloto = piloto;
            this.equipo = equipo;
            this.vuelta = vuelta;
            this.velocidad = velocidad;
            this.tiempo = tiempo;
            this.diferencia = diferencia;
        }

        public int getPosicion() { return posicion; }
        public String getPiloto() { return piloto; }
        public String getEquipo() { return equipo; }
        public String getVuelta() { return vuelta; }
        public String getVelocidad() { return velocidad; }
        public String getTiempo() { return tiempo; }
        public String getDiferencia() { return diferencia; }
    }

    private final TableView<FilaClasificacion> tabla = new TableView<>();
    private final ObservableList<FilaClasificacion> filas = FXCollections.observableArrayList();

    public ClasificacionView() {
        TableColumn<FilaClasificacion, Number> colPos = new TableColumn<>("POS");
        colPos.setCellValueFactory(new PropertyValueFactory<>("posicion"));
        colPos.setPrefWidth(45);

        TableColumn<FilaClasificacion, String> colPiloto = new TableColumn<>("PILOTO");
        colPiloto.setCellValueFactory(new PropertyValueFactory<>("piloto"));
        colPiloto.setPrefWidth(150);

        TableColumn<FilaClasificacion, String> colEquipo = new TableColumn<>("EQUIPO");
        colEquipo.setCellValueFactory(new PropertyValueFactory<>("equipo"));
        colEquipo.setPrefWidth(150);

        TableColumn<FilaClasificacion, String> colVuelta = new TableColumn<>("VUELTA");
        colVuelta.setCellValueFactory(new PropertyValueFactory<>("vuelta"));
        colVuelta.setPrefWidth(70);

        TableColumn<FilaClasificacion, String> colVelocidad = new TableColumn<>("VELOCIDAD");
        colVelocidad.setCellValueFactory(new PropertyValueFactory<>("velocidad"));
        colVelocidad.setPrefWidth(90);

        TableColumn<FilaClasificacion, String> colTiempo = new TableColumn<>("TIEMPO");
        colTiempo.setCellValueFactory(new PropertyValueFactory<>("tiempo"));
        colTiempo.setPrefWidth(90);

        TableColumn<FilaClasificacion, String> colDiferencia = new TableColumn<>("DIFERENCIA");
        colDiferencia.setCellValueFactory(new PropertyValueFactory<>("diferencia"));
        colDiferencia.setPrefWidth(90);

        tabla.getColumns().addAll(List.of(colPos, colPiloto, colEquipo, colVuelta, colVelocidad, colTiempo, colDiferencia));
        tabla.setItems(filas);
        tabla.setPrefHeight(360);
    }

    public VBox construirPanel() {
        VBox panel = new VBox(8);
        panel.setStyle("-fx-background-color: #161b22;");
        panel.setPrefWidth(680);

        Label titulo = new Label("CLASIFICACIÓN EN TIEMPO REAL");
        titulo.setTextFill(Color.web("#f0f6fc"));
        titulo.setFont(Font.font("System", FontWeight.BOLD, 15));
        titulo.setPadding(new javafx.geometry.Insets(10, 0, 0, 10));

        panel.getChildren().addAll(titulo, tabla);
        return panel;
    }

    /** Reconstruye los valores de la tabla a partir del orden actual de la carrera. */
    public void actualizar(List<EstadoVehiculo> ordenados, int vueltaObjetivo) {
        Double tiempoLider = ordenados.isEmpty() ? null : ordenados.get(0).getTiempoTotalSeg();

        filas.setAll(ordenados.stream().map(estado -> {
            int vueltaActual = Math.min(estado.getVueltasCompletas() + 1, vueltaObjetivo);
            String diferencia = tiempoLider == null || estado == ordenados.get(0)
                    ? "-"
                    : "+" + String.format("%.3f", estado.getTiempoTotalSeg() - tiempoLider) + "s";

            return new FilaClasificacion(
                    ordenados.indexOf(estado) + 1,
                    estado.getParticipante().getPiloto().getNombre(),
                    estado.getEquipo(),
                    vueltaActual + "/" + vueltaObjetivo,
                    String.format("%.0f km/h", estado.getVelocidadActualKmh()),
                    EstadoVehiculo.formatearTiempo(estado.getTiempoVueltaActualSeg()),
                    diferencia
            );
        }).toList());
    }
}
