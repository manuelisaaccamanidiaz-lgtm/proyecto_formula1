package com.formula1.javafx;

import com.formula1.model.EstadoVehiculo;
import com.formula1.simulation.Clima;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Panel de datos en vivo:
 *  1) Clima de la sesion — dato REAL del proyecto (enum Clima).
 *  2) Telemetria del vehiculo que va en cabeza — velocidad, vuelta, tiempo,
 *     neumaticos (datos reales) + combustible/desgaste (PROPUESTA, ver
 *     EstadoVehiculo y MotorSimulacion).
 *
 * PROPUESTA marcada explicitamente: el enunciado pide mostrar tambien
 * "TEMPERATURA" y "VIENTO". El proyecto no modela ninguno de los dos como
 * dato numerico (solo existe el enum Clima). Aqui se muestran valores
 * ilustrativos derivados del Clima (por ejemplo, LLUVIOSO -> mas viento),
 * claramente etiquetados como estimados, para no inventar una fuente de
 * datos que no existe sin decirlo.
 */
public class PanelTelemetria {

    private final Label lblClima = new Label();
    private final Label lblTemperaturaViento = new Label();
    private final Label lblPista = new Label();

    private final Label lblPilotoLider = new Label();
    private final Label lblVelocidad = new Label();
    private final Label lblVuelta = new Label();
    private final Label lblTiempo = new Label();
    private final Label lblCombustible = new Label();
    private final Label lblNeumaticos = new Label();
    private final Label lblDesgaste = new Label();

    public VBox construirPanel(Clima clima) {
        VBox panel = new VBox(6);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: #0d1117; -fx-background-radius: 6;");
        panel.setPrefWidth(300);

        Label tituloClima = titulo("CONDICIONES DE LA SESIÓN");
        estilizarValor(lblClima);
        estilizarValor(lblTemperaturaViento);
        estilizarValor(lblPista);

        Label tituloAuto = titulo("VEHÍCULO EN CABEZA");
        estilizarValor(lblPilotoLider);
        estilizarValor(lblVelocidad);
        estilizarValor(lblVuelta);
        estilizarValor(lblTiempo);
        estilizarValor(lblCombustible);
        estilizarValor(lblNeumaticos);
        estilizarValor(lblDesgaste);

        panel.getChildren().addAll(tituloClima, lblClima, lblTemperaturaViento, lblPista,
                new javafx.scene.control.Separator(), tituloAuto, lblPilotoLider, lblVelocidad,
                lblVuelta, lblTiempo, lblCombustible, lblNeumaticos, lblDesgaste);

        actualizarClima(clima);
        return panel;
    }

    private Label titulo(String texto) {
        Label l = new Label(texto);
        l.setTextFill(Color.web("#8b949e"));
        l.setFont(Font.font("System", FontWeight.BOLD, 11));
        return l;
    }

    private void estilizarValor(Label l) {
        l.setTextFill(Color.WHITE);
        l.setFont(Font.font("Monospaced", 13));
    }

    private void actualizarClima(Clima clima) {
        lblClima.setText("Clima: " + clima);
        // PROPUESTA: temperatura/viento estimados a partir del clima (no son datos del proyecto).
        String estimado = switch (clima) {
            case SECO -> "~24 °C (estimado) | Viento ~10 km/h (estimado)";
            case LLUVIOSO -> "~17 °C (estimado) | Viento ~22 km/h (estimado)";
            case EXTREMO -> "~13 °C (estimado) | Viento ~38 km/h (estimado)";
        };
        lblTemperaturaViento.setText(estimado);
        String pista = switch (clima) {
            case SECO -> "Pista seca";
            case LLUVIOSO -> "Pista húmeda";
            case EXTREMO -> "Pista muy mojada";
        };
        lblPista.setText("Estado de pista: " + pista + " (derivado del clima)");
    }

    /** Actualiza la caja de telemetria con el vehiculo que va primero. */
    public void actualizarLider(EstadoVehiculo lider, int vueltaObjetivo) {
        if (lider == null) {
            return;
        }
        lblPilotoLider.setText("#" + (lider.getVueltasCompletas() + 1) + " " + lider.getParticipante().getPiloto().getNombre());
        lblVelocidad.setText(String.format("Velocidad: %.0f km/h", lider.getVelocidadActualKmh()));
        lblVuelta.setText("Vuelta: " + Math.min(lider.getVueltasCompletas() + 1, vueltaObjetivo) + "/" + vueltaObjetivo);
        lblTiempo.setText("Tiempo vuelta: " + EstadoVehiculo.formatearTiempo(lider.getTiempoVueltaActualSeg()));
        lblCombustible.setText(String.format("Combustible: %.0f%%", lider.getCombustiblePorcentaje()));
        lblNeumaticos.setText("Neumáticos: " + lider.getParticipante().getConfiguracion().getPresionNeumaticos());
        lblDesgaste.setText(String.format("Desgaste: %.0f%%", lider.getDesgasteNeumaticosPorcentaje()));
    }
}
