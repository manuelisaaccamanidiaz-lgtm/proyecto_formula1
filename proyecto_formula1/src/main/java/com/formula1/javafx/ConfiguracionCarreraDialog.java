package com.formula1.javafx;

import java.util.List;
import java.util.Optional;

import com.formula1.circuits.Circuit;
import com.formula1.config.EstrategiaCombustible;
import com.formula1.simulation.Clima;
import com.formula1.simulation.ConfiguracionCarrera;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

/**
 * Pantalla de configuracion previa a una carrera ("A rodar!"): permite
 * elegir cada aspecto que pide el enunciado: la pista, el clima, la
 * estrategia de combustible y el nivel de gasolina inicial.
 */
public class ConfiguracionCarreraDialog {

    /** Muestra el dialogo (modal) y devuelve la configuracion elegida, o vacio si el usuario cancelo. */
    public Optional<ConfiguracionCarrera> mostrarYEsperar(List<Circuit> circuitosDisponibles) {
        Dialog<ConfiguracionCarrera> dialogo = new Dialog<>();
        dialogo.setTitle("A rodar! - Configuracion de la carrera");
        dialogo.setHeaderText(null);
        dialogo.getDialogPane().setStyle("-fx-background-color: #0d1117;");

        Label encabezado = new Label("Elige pista, clima y combustible");
        encabezado.setFont(Font.font("System", FontWeight.BOLD, 16));
        encabezado.setTextFill(Color.web("#f0f6fc"));

        ComboBox<Circuit> comboPista = new ComboBox<>();
        comboPista.getItems().addAll(circuitosDisponibles);
        comboPista.getSelectionModel().selectFirst();
        comboPista.setConverter(new StringConverter<>() {
            @Override
            public String toString(Circuit c) {
                return c == null ? "" : c.getNombre() + " (" + c.getPais() + ") - " + c.getLongitud_km() + " km";
            }

            @Override
            public Circuit fromString(String string) {
                return null;
            }
        });

        ComboBox<String> comboClima = new ComboBox<>();
        comboClima.getItems().addAll("Aleatorio", "SECO", "LLUVIOSO", "EXTREMO");
        comboClima.getSelectionModel().selectFirst();

        ComboBox<EstrategiaCombustible> comboCombustible = new ComboBox<>();
        comboCombustible.getItems().addAll(EstrategiaCombustible.values());
        comboCombustible.getSelectionModel().select(EstrategiaCombustible.BALANCEADA);

        Slider sliderGasolina = new Slider(50, 100, 100);
        sliderGasolina.setShowTickLabels(true);
        sliderGasolina.setShowTickMarks(true);
        sliderGasolina.setMajorTickUnit(10);
        sliderGasolina.setBlockIncrement(5);
        Label lblGasolinaValor = new Label("100 %");
        lblGasolinaValor.setTextFill(Color.web("#f0f6fc"));
        sliderGasolina.valueProperty().addListener((obs, viejo, nuevo)
                -> lblGasolinaValor.setText(String.format("%.0f %%", nuevo.doubleValue())));

        GridPane grilla = new GridPane();
        grilla.setHgap(12);
        grilla.setVgap(14);
        grilla.setPadding(new Insets(16, 8, 8, 8));

        grilla.add(encabezado, 0, 0, 2, 1);
        grilla.add(etiqueta("Pista:"), 0, 1);
        grilla.add(comboPista, 1, 1);
        grilla.add(etiqueta("Clima:"), 0, 2);
        grilla.add(comboClima, 1, 2);
        grilla.add(etiqueta("Estrategia de combustible:"), 0, 3);
        grilla.add(comboCombustible, 1, 3);
        grilla.add(etiqueta("Gasolina inicial:"), 0, 4);
        grilla.add(sliderGasolina, 1, 4);
        grilla.add(lblGasolinaValor, 2, 4);

        dialogo.getDialogPane().setContent(grilla);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        estilizarBoton(dialogo, ButtonType.OK, "A rodar!");
        estilizarBoton(dialogo, ButtonType.CANCEL, "Cancelar");

        dialogo.setResultConverter(boton -> {
            if (boton != ButtonType.OK) {
                return null;
            }
            Circuit circuito = comboPista.getValue();
            Clima clima = "Aleatorio".equals(comboClima.getValue())
                    ? Clima.aleatorio()
                    : Clima.valueOf(comboClima.getValue());
            return new ConfiguracionCarrera(circuito, clima, comboCombustible.getValue(), sliderGasolina.getValue());
        });

        return dialogo.showAndWait();
    }

    private Label etiqueta(String texto) {
        Label l = new Label(texto);
        l.setTextFill(Color.web("#8b949e"));
        l.setFont(Font.font("System", FontWeight.BOLD, 12));
        return l;
    }

    private void estilizarBoton(Dialog<?> dialogo, ButtonType tipo, String texto) {
        var boton = dialogo.getDialogPane().lookupButton(tipo);
        if (boton != null) {
            ((javafx.scene.control.Button) boton).setText(texto);
            boton.setStyle(tipo == ButtonType.OK
                    ? "-fx-background-color: #d32026; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;"
                    : "-fx-background-color: #21262d; -fx-text-fill: white; -fx-cursor: hand;");
        }
    }
}
