package com.formula1.javafx;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.formula1.circuits.Circuit;
import com.formula1.config.EstrategiaCombustible;
import com.formula1.simulation.Clima;
import com.formula1.simulation.ConfiguracionCarrera;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Pantalla de configuracion previa a una carrera ("A rodar!"). En vez de una
 * simple grilla de ComboBox, el menu de opciones (Pista, Clima, Combustible,
 * Gasolina) se muestra como una lista de botones a la izquierda; al elegir
 * una opcion, el panel de contenido de la derecha cambia para mostrar esa
 * seccion (igual que un panel de ajustes tipo "wizard"). Al elegir una
 * pista se muestra ademas una imagen con el trazado del circuito.
 */
public class ConfiguracionCarreraDialog {

    private static final String COLOR_FONDO = "#0d1117";
    private static final String COLOR_PANEL = "#161b22";
    private static final String COLOR_BORDE = "#30363d";
    private static final String COLOR_ROJO = "#d32026";
    private static final String COLOR_TEXTO = "#f0f6fc";
    private static final String COLOR_TEXTO_SEC = "#8b949e";

    private Circuit circuitoSeleccionado;
    private String climaSeleccionado = "Aleatorio";
    private EstrategiaCombustible combustibleSeleccionado = EstrategiaCombustible.BALANCEADA;
    private double gasolinaSeleccionada = 100;

    private final Map<Circuit, Button> mapaBotonesPista = new LinkedHashMap<>();

    private ImageView vistaPreviaImagen;
    private Label detallePista;
    private Label resumenSeleccion;

    /** Muestra el dialogo (modal) y devuelve la configuracion elegida, o vacio si el usuario cancelo. */
    public Optional<ConfiguracionCarrera> mostrarYEsperar(List<Circuit> circuitosDisponibles) {
        Dialog<ConfiguracionCarrera> dialogo = new Dialog<>();
        dialogo.setTitle("A rodar! - Configuracion de la carrera");
        dialogo.setHeaderText(null);
        dialogo.setResizable(true);
        dialogo.getDialogPane().setStyle("-fx-background-color: " + COLOR_FONDO + ";");
        dialogo.getDialogPane().setPrefSize(1000, 650);

        if (!circuitosDisponibles.isEmpty()) {
            circuitoSeleccionado = circuitosDisponibles.get(0);
        }

        VBox panelPista = construirPanelPista(circuitosDisponibles);
        VBox panelClima = construirPanelClima();
        VBox panelCombustible = construirPanelCombustible();
        VBox panelGasolina = construirPanelGasolina();
        VBox[] paneles = {panelPista, panelClima, panelCombustible, panelGasolina};

        for (int i = 1; i < paneles.length; i++) {
            paneles[i].setVisible(false);
            paneles[i].setManaged(false);
        }

        StackPane contenido = new StackPane(paneles);
        contenido.setPadding(new Insets(20));
        contenido.setAlignment(Pos.TOP_LEFT);

        VBox menuLateral = construirMenuLateral(paneles);

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: " + COLOR_FONDO + ";");
        layout.setLeft(menuLateral);
        layout.setCenter(contenido);

        resumenSeleccion = new Label();
        resumenSeleccion.setTextFill(Color.web(COLOR_TEXTO_SEC));
        resumenSeleccion.setFont(Font.font("System", 12));
        resumenSeleccion.setPadding(new Insets(4, 20, 12, 20));
        actualizarResumen();
        layout.setBottom(resumenSeleccion);

        dialogo.getDialogPane().setContent(layout);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        estilizarBoton(dialogo, ButtonType.OK, "A rodar!");
        estilizarBoton(dialogo, ButtonType.CANCEL, "Cancelar");

        dialogo.setResultConverter(boton -> {
            if (boton != ButtonType.OK || circuitoSeleccionado == null) {
                return null;
            }
            Clima clima = "Aleatorio".equals(climaSeleccionado)
                    ? Clima.aleatorio()
                    : Clima.valueOf(climaSeleccionado);
            return new ConfiguracionCarrera(circuitoSeleccionado, clima, combustibleSeleccionado, gasolinaSeleccionada);
        });

        return dialogo.showAndWait();
    }

    // -----------------------------------------------------------------
    // Menu lateral izquierdo: cada opcion es un apartado independiente
    // que, al pulsarse, cambia el panel visible a la derecha.
    // -----------------------------------------------------------------
    private VBox construirMenuLateral(VBox[] paneles) {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20, 14, 20, 20));
        menu.setPrefWidth(210);
        menu.setStyle("-fx-background-color: " + COLOR_FONDO + "; -fx-border-color: transparent "
                + COLOR_BORDE + " transparent transparent; -fx-border-width: 0 1 0 0;");

        Label titulo = new Label("OPCIONES DE CARRERA");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 12));
        titulo.setTextFill(Color.web(COLOR_TEXTO_SEC));
        titulo.setPadding(new Insets(0, 0, 6, 6));

        String[] nombres = {"Pista", "Clima", "Combustible", "Gasolina inicial"};
        List<Button> botonesMenu = new ArrayList<>();
        for (int i = 0; i < nombres.length; i++) {
            int indice = i;
            Button boton = new Button(nombres[i]);
            boton.setPrefSize(180, 48);
            boton.setMaxWidth(Double.MAX_VALUE);
            boton.setAlignment(Pos.CENTER_LEFT);
            boton.setFont(Font.font("System", FontWeight.BOLD, 13));
            boton.setOnAction(e -> {
                for (int j = 0; j < paneles.length; j++) {
                    paneles[j].setVisible(j == indice);
                    paneles[j].setManaged(j == indice);
                }
                for (int j = 0; j < botonesMenu.size(); j++) {
                    estiloMenu(botonesMenu.get(j), j == indice);
                }
            });
            botonesMenu.add(boton);
        }
        estiloMenu(botonesMenu.get(0), true);
        for (int j = 1; j < botonesMenu.size(); j++) {
            estiloMenu(botonesMenu.get(j), false);
        }

        menu.getChildren().add(titulo);
        menu.getChildren().addAll(botonesMenu);
        return menu;
    }

    private void estiloMenu(Button boton, boolean activo) {
        if (activo) {
            boton.setStyle("-fx-background-color: #21262d; -fx-text-fill: white; -fx-background-radius: 8; "
                    + "-fx-border-color: " + COLOR_ROJO + " transparent transparent transparent; "
                    + "-fx-border-width: 0 0 0 4; -fx-cursor: hand;");
        } else {
            boton.setStyle("-fx-background-color: " + COLOR_PANEL + "; -fx-text-fill: " + COLOR_TEXTO + "; "
                    + "-fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;");
        }
    }

    // -----------------------------------------------------------------
    // Panel: Pista (lista de circuitos + imagen del trazado elegido)
    // -----------------------------------------------------------------
    private VBox construirPanelPista(List<Circuit> circuitos) {
        Label encabezado = tituloSeccion("Elige la pista");

        VBox listaPistas = new VBox(8);
        listaPistas.setPrefWidth(240);
        for (Circuit circuito : circuitos) {
            Button boton = new Button(circuito.getNombre() + "\n" + circuito.getPais());
            boton.setWrapText(true);
            boton.setPrefSize(220, 52);
            boton.setMaxWidth(Double.MAX_VALUE);
            boton.setAlignment(Pos.CENTER_LEFT);
            boton.setFont(Font.font("System", FontWeight.BOLD, 12));
            boton.setOnAction(e -> seleccionarPista(circuito));
            mapaBotonesPista.put(circuito, boton);
            listaPistas.getChildren().add(boton);
        }

        ScrollPane scroll = new ScrollPane(listaPistas);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportWidth(250);
        scroll.setPrefViewportHeight(420);
        scroll.setStyle("-fx-background: " + COLOR_FONDO + "; -fx-background-color: " + COLOR_FONDO + ";");

        vistaPreviaImagen = new ImageView();
        vistaPreviaImagen.setFitWidth(420);
        vistaPreviaImagen.setPreserveRatio(true);
        vistaPreviaImagen.setSmooth(true);

        StackPane marcoImagen = new StackPane(vistaPreviaImagen);
        marcoImagen.setPadding(new Insets(10));
        marcoImagen.setStyle("-fx-background-color: #0b0e14; -fx-background-radius: 10; "
                + "-fx-border-color: " + COLOR_BORDE + "; -fx-border-radius: 10;");

        detallePista = new Label();
        detallePista.setWrapText(true);
        detallePista.setTextFill(Color.web(COLOR_TEXTO_SEC));
        detallePista.setFont(Font.font("System", 12));
        detallePista.setMaxWidth(420);

        VBox derecha = new VBox(12, marcoImagen, detallePista);

        HBox cuerpo = new HBox(20, scroll, derecha);
        cuerpo.setAlignment(Pos.TOP_LEFT);

        VBox panel = new VBox(14, encabezado, cuerpo);

        if (!circuitos.isEmpty()) {
            seleccionarPista(circuitos.get(0));
        }
        return panel;
    }

    private void seleccionarPista(Circuit circuito) {
        circuitoSeleccionado = circuito;
        mapaBotonesPista.forEach((c, b) -> estiloOpcion(b, c == circuito));

        // Renderiza el trazado del circuito en un canvas fuera de pantalla y
        // lo convierte en una imagen para mostrarla como vista previa.
        Canvas lienzoFuera = new Canvas(960, 640);
        new CircuitoView(lienzoFuera).dibujarVistaPrevia(circuito);
        vistaPreviaImagen.setImage(lienzoFuera.snapshot(null, null));

        detallePista.setText(String.format(
                "%s - %.2f km, %d vueltas%nRecord de vuelta: %s (%s, %d)%n%n%s",
                circuito.getPais(), circuito.getLongitud_km(), circuito.getVueltas(),
                circuito.getRecordVueltaTiempo(), circuito.getRecordVueltaPiloto(), circuito.getRecordVueltaAnio(),
                circuito.getDescripcion()));

        actualizarResumen();
    }

    // -----------------------------------------------------------------
    // Panel: Clima
    // -----------------------------------------------------------------
    private VBox construirPanelClima() {
        Label encabezado = tituloSeccion("Elige el clima");

        VBox lista = new VBox(10);
        lista.setPrefWidth(420);

        String[] opciones = {"Aleatorio", "SECO", "LLUVIOSO", "EXTREMO"};
        String[] descripciones = {
                "El clima se decide al azar justo antes de la salida.",
                "Pista seca: agarre maximo y ritmos de vuelta mas rapidos.",
                "Pista mojada: menos agarre y mayor riesgo de errores.",
                "Condiciones extremas: lluvia intensa y visibilidad reducida."
        };

        List<Button> botonesClima = new ArrayList<>();
        for (int i = 0; i < opciones.length; i++) {
            String opcion = opciones[i];
            Button boton = new Button(opcion + "\n" + descripciones[i]);
            boton.setWrapText(true);
            boton.setPrefSize(400, 56);
            boton.setMaxWidth(Double.MAX_VALUE);
            boton.setAlignment(Pos.CENTER_LEFT);
            boton.setFont(Font.font("System", FontWeight.BOLD, 13));
            boton.setOnAction(e -> {
                climaSeleccionado = opcion;
                for (Button b : botonesClima) {
                    estiloOpcion(b, b == boton);
                }
                actualizarResumen();
            });
            botonesClima.add(boton);
            lista.getChildren().add(boton);
        }
        estiloOpcion(botonesClima.get(0), true);

        return new VBox(14, encabezado, lista);
    }

    // -----------------------------------------------------------------
    // Panel: Combustible
    // -----------------------------------------------------------------
    private VBox construirPanelCombustible() {
        Label encabezado = tituloSeccion("Estrategia de combustible");

        VBox lista = new VBox(10);
        lista.setPrefWidth(320);

        List<Button> botonesCombustible = new ArrayList<>();
        for (EstrategiaCombustible estrategia : EstrategiaCombustible.values()) {
            Button boton = new Button(estrategia.name());
            boton.setPrefSize(300, 48);
            boton.setMaxWidth(Double.MAX_VALUE);
            boton.setAlignment(Pos.CENTER_LEFT);
            boton.setFont(Font.font("System", FontWeight.BOLD, 13));
            boton.setOnAction(e -> {
                combustibleSeleccionado = estrategia;
                for (Button b : botonesCombustible) {
                    estiloOpcion(b, b == boton);
                }
                actualizarResumen();
            });
            botonesCombustible.add(boton);
            lista.getChildren().add(boton);
        }
        estiloOpcion(botonesCombustible.get(EstrategiaCombustible.BALANCEADA.ordinal()), true);

        return new VBox(14, encabezado, lista);
    }

    // -----------------------------------------------------------------
    // Panel: Gasolina inicial
    // -----------------------------------------------------------------
    private VBox construirPanelGasolina() {
        Label encabezado = tituloSeccion("Gasolina inicial");

        Slider slider = new Slider(50, 100, 100);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(10);
        slider.setBlockIncrement(5);
        slider.setPrefWidth(420);

        Label valor = new Label("100 %");
        valor.setTextFill(Color.web(COLOR_TEXTO));
        valor.setFont(Font.font("System", FontWeight.BOLD, 26));

        slider.valueProperty().addListener((obs, viejo, nuevo) -> {
            gasolinaSeleccionada = nuevo.doubleValue();
            valor.setText(String.format("%.0f %%", nuevo.doubleValue()));
            actualizarResumen();
        });

        return new VBox(14, encabezado, valor, slider);
    }

    // -----------------------------------------------------------------
    // Utilidades comunes
    // -----------------------------------------------------------------
    private Label tituloSeccion(String texto) {
        Label label = new Label(texto);
        label.setFont(Font.font("System", FontWeight.BOLD, 20));
        label.setTextFill(Color.web(COLOR_TEXTO));
        return label;
    }

    private void estiloOpcion(Button boton, boolean activo) {
        if (activo) {
            boton.setStyle("-fx-background-color: " + COLOR_ROJO + "; -fx-text-fill: white; "
                    + "-fx-background-radius: 8; -fx-cursor: hand;");
        } else {
            boton.setStyle("-fx-background-color: " + COLOR_PANEL + "; -fx-text-fill: " + COLOR_TEXTO + "; "
                    + "-fx-background-radius: 8; -fx-border-color: " + COLOR_BORDE + "; "
                    + "-fx-border-radius: 8; -fx-cursor: hand;");
        }
    }

    private void actualizarResumen() {
        if (resumenSeleccion == null) {
            return;
        }
        String nombrePista = circuitoSeleccionado != null ? circuitoSeleccionado.getNombre() : "-";
        resumenSeleccion.setText(String.format(
                "Seleccion actual  ->  Pista: %s   |   Clima: %s   |   Combustible: %s   |   Gasolina: %.0f %%",
                nombrePista, climaSeleccionado, combustibleSeleccionado, gasolinaSeleccionada));
    }

    private void estilizarBoton(Dialog<?> dialogo, ButtonType tipo, String texto) {
        var boton = dialogo.getDialogPane().lookupButton(tipo);
        if (boton != null) {
            ((javafx.scene.control.Button) boton).setText(texto);
            boton.setStyle(tipo == ButtonType.OK
                    ? "-fx-background-color: " + COLOR_ROJO + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;"
                    : "-fx-background-color: #21262d; -fx-text-fill: white; -fx-cursor: hand;");
        }
    }
}
