package com.formula1.javafx.gestion;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Estilos y controles compartidos por todos los paneles de gestion (para
 * que circuitos/pilotos/equipos/vehiculos/etc. se vean consistentes entre
 * si y con el resto de la aplicacion), sin depender de un archivo CSS
 * aparte.
 */
public final class EstiloGestion {

    public static final String FONDO = "-fx-background-color: #0b0e14;";
    public static final String CAMPO = "-fx-background-color: #161b22; -fx-text-fill: #f0f6fc; "
            + "-fx-prompt-text-fill: #6e7681; -fx-background-radius: 6; -fx-border-color: #30363d; -fx-border-radius: 6;";
    public static final String BOTON_PRIMARIO = "-fx-background-color: #d32026; -fx-text-fill: white; "
            + "-fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;";
    public static final String BOTON_SECUNDARIO = "-fx-background-color: #21262d; -fx-text-fill: #f0f6fc; "
            + "-fx-background-radius: 6; -fx-cursor: hand;";
    public static final String BOTON_PELIGRO = "-fx-background-color: #3a1418; -fx-text-fill: #ff7b72; "
            + "-fx-border-color: #6e2129; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;";
    public static final String TABLA = "-fx-background-color: #11161d; -fx-control-inner-background: #11161d; "
            + "-fx-table-cell-border-color: #21262d;";
    public static final String TARJETA = "-fx-background-color: #11161d; -fx-background-radius: 10; "
            + "-fx-border-color: #21262d; -fx-border-radius: 10;";

    private EstiloGestion() {
    }

    public static Label titulo(String texto) {
        Label l = new Label(texto);
        l.setFont(Font.font("System", FontWeight.BOLD, 22));
        l.setTextFill(Color.WHITE);
        return l;
    }

    public static Label etiquetaCampo(String texto) {
        Label l = new Label(texto);
        l.setFont(Font.font("System", FontWeight.BOLD, 11));
        l.setTextFill(Color.web("#8b949e"));
        return l;
    }

    public static Label estado() {
        Label l = new Label(" ");
        l.setFont(Font.font("System", 12));
        l.setTextFill(Color.web("#8b949e"));
        return l;
    }

    public static void mostrarInfo(Label estado, String mensaje) {
        estado.setTextFill(Color.web("#3fb950"));
        estado.setText(mensaje);
    }

    public static void mostrarError(Label estado, String mensaje) {
        estado.setTextFill(Color.web("#ff7b72"));
        estado.setText(mensaje);
    }

    public static TextField campo(String prompt) {
        TextField t = new TextField();
        t.setPromptText(prompt);
        t.setStyle(CAMPO);
        return t;
    }

    public static Button botonPrimario(String texto) {
        Button b = new Button(texto);
        b.setStyle(BOTON_PRIMARIO);
        return b;
    }

    public static Button botonSecundario(String texto) {
        Button b = new Button(texto);
        b.setStyle(BOTON_SECUNDARIO);
        return b;
    }

    public static Button botonPeligro(String texto) {
        Button b = new Button(texto);
        b.setStyle(BOTON_PELIGRO);
        return b;
    }
}
