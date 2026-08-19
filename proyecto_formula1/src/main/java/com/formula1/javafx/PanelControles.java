package com.formula1.javafx;

import java.util.function.DoubleConsumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Controles de la simulacion: iniciar/pausar/reanudar/reiniciar y botones de
 * velocidad de simulacion (0.5x, 1x, 2x, 5x, 10x, tal como pide el
 * enunciado). La velocidad de simulacion solo escala el "dt" que recibe
 * MotorSimulacion.actualizar(dt); no cambia ninguna regla fisica/logica, solo
 * que esos mismos calculos se ejecutan sobre un intervalo de tiempo mayor o
 * menor por cuadro.
 */
public class PanelControles {

    private final Button btnIniciarPausar = new Button("Iniciar");

    public HBox construirPanel(Runnable onIniciarPausar, Runnable onReiniciar, DoubleConsumer onCambiarVelocidad) {
        HBox controles = new HBox(16);
        controles.setPadding(new Insets(12, 16, 16, 16));
        controles.setAlignment(Pos.CENTER_LEFT);
        controles.setStyle("-fx-background-color: #161b22;");

        btnIniciarPausar.setStyle("-fx-background-color: #21262d; -fx-text-fill: white; -fx-cursor: hand;");
        btnIniciarPausar.setOnAction(e -> onIniciarPausar.run());

        Button btnReiniciar = new Button("Reiniciar");
        btnReiniciar.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-cursor: hand;");
        btnReiniciar.setOnAction(e -> onReiniciar.run());

        Label lblVelocidad = new Label("Velocidad de simulación:");
        lblVelocidad.setTextFill(Color.WHITE);

        ToggleGroup grupoVelocidad = new ToggleGroup();
        double[] valores = {0.5, 1.0, 2.0, 5.0, 10.0};
        HBox botonesVelocidad = new HBox(6);
        for (double valor : valores) {
            String etiqueta = (valor == (int) valor ? String.valueOf((int) valor) : String.valueOf(valor)) + "x";
            ToggleButton tb = new ToggleButton(etiqueta);
            tb.setToggleGroup(grupoVelocidad);
            tb.setSelected(valor == 1.0);
            tb.setStyle("-fx-background-color: #21262d; -fx-text-fill: white; -fx-cursor: hand;");
            tb.setOnAction(e -> {
                if (tb.isSelected()) {
                    onCambiarVelocidad.accept(valor);
                }
            });
            botonesVelocidad.getChildren().add(tb);
        }

        controles.getChildren().addAll(btnIniciarPausar, btnReiniciar, lblVelocidad, botonesVelocidad);
        return controles;
    }

    /** Refleja en el boton principal el estado actual (pausado / corriendo / finalizado). */
    public void actualizarEstadoBoton(boolean corriendo, boolean pausado, boolean finalizado) {
        if (finalizado) {
            btnIniciarPausar.setText("Finalizada");
            btnIniciarPausar.setDisable(true);
        } else if (!corriendo) {
            btnIniciarPausar.setText("Iniciar");
            btnIniciarPausar.setDisable(false);
        } else {
            btnIniciarPausar.setText(pausado ? "Reanudar" : "Pausar");
            btnIniciarPausar.setDisable(false);
        }
    }
}
