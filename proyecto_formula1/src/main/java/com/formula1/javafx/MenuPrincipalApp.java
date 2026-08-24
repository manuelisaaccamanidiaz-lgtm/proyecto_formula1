package com.formula1.javafx;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.formula1.circuitfactories.F1CircuitFactory;
import com.formula1.config.CargaAerodinamica;
import com.formula1.config.ConfiguracionManager;
import com.formula1.config.ConfiguracionVehiculo;
import com.formula1.config.EstrategiaCombustible;
import com.formula1.config.PresionNeumaticos;
import com.formula1.factories.VehicleFactory;
import com.formula1.javafx.gestion.PanelCircuitos;
import com.formula1.javafx.gestion.PanelConfiguracionVehiculo;
import com.formula1.javafx.gestion.PanelEquipos;
import com.formula1.javafx.gestion.PanelHistorial;
import com.formula1.javafx.gestion.PanelPilotos;
import com.formula1.javafx.gestion.PanelRecords;
import com.formula1.javafx.gestion.PanelSimulacionClasificacion;
import com.formula1.javafx.gestion.PanelVehiculos;
import com.formula1.managers.CircuitManager;
import com.formula1.managers.PilotManager;
import com.formula1.managers.TeamManager;
import com.formula1.managers.VehicleManager;
import com.formula1.pilotfactories.F1PilotFactory;
import com.formula1.resultfactories.F1ResultFactory;
import com.formula1.results.Result;
import com.formula1.simulation.ConfiguracionCarrera;
import com.formula1.simulation.HistorialClasificacion;
import com.formula1.teamfactories.FerrariTeamFactory;
import com.formula1.teamfactories.MercedesTeamFactory;
import com.formula1.teamfactories.RedBullTeamFactory;
import com.formula1.teams.Team;
import com.formula1.vehiclefactories.AlfaRomeoFactory;
import com.formula1.vehiclefactories.AlphaTauriFactory;
import com.formula1.vehiclefactories.AlpineFactory;
import com.formula1.vehiclefactories.AstonMartinFactory;
import com.formula1.vehiclefactories.FerrariFactory;
import com.formula1.vehiclefactories.HaasFactory;
import com.formula1.vehiclefactories.McLarenFactory;
import com.formula1.vehiclefactories.MercedesFactory;
import com.formula1.vehiclefactories.RedBullFactory;
import com.formula1.vehiclefactories.WilliamsFactory;
import com.formula1.vehicles.ModoConduccion;
import com.formula1.vehicles.Vehicle;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Punto de entrada JavaFX principal del proyecto: pantalla de carga con el
 * logo, y luego un menu con barra lateral izquierda (tematica F1) cuyas
 * opciones muestran su contenido AL LADO DERECHO de la misma ventana -
 * nada de JOptionPane. Cada seccion (circuitos, pilotos, equipos,
 * vehiculos, configuracion, simulacion, historial, records) es un panel
 * JavaFX propio dentro de {@code com.formula1.javafx.gestion}, con su
 * propia tabla y formulario embebidos.
 *
 * El unico lugar donde sigue existiendo JOptionPane en el proyecto es
 * {@code Main.java} (la version de consola), que usa
 * {@code com.formula1.menu.OperacionesMenu} porque, al ser una app 100%
 * de consola, no tiene otra superficie donde mostrar dialogos.
 */
public class MenuPrincipalApp extends Application {

    private final CircuitManager circuitManager = new CircuitManager();
    private final PilotManager pilotManager = new PilotManager();
    private final TeamManager teamManager = new TeamManager();
    private final VehicleManager vehicleManager = new VehicleManager();
    private final ConfiguracionManager configManager = new ConfiguracionManager();
    private final HistorialClasificacion historial = new HistorialClasificacion();
    private final List<Result> recordsHistoricos = new ArrayList<>();

    private Stage stage;
    private VistaCarreraEnVivo vistaCarreraActual;
    private StackPane areaContenido;
    private final Map<String, Button> botonesBarraLateral = new LinkedHashMap<>();

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        cargarDatosDelProyecto();

        stage.setTitle("Simulador de Formula 1");
        Scene scene = new Scene(construirPantallaCarga(), 1400, 760);
        stage.setScene(scene);
        stage.show();
    }

    // -----------------------------------------------------------------
    // Pantalla de carga: el logo aparece pequeno y crece de forma fluida
    // y lenta, como si estuviera cargando, con el texto de bienvenida.
    // -----------------------------------------------------------------
    private StackPane construirPantallaCarga() {
        StackPane fondo = new StackPane();
        fondo.setStyle("-fx-background-color: linear-gradient(to bottom, #0b0e14, #14181f, #0b0e14);");

        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/logo-f1.png")));
        logo.setPreserveRatio(true);
        logo.setFitWidth(420);
        logo.setScaleX(0.05);
        logo.setScaleY(0.05);
        logo.setOpacity(0);

        Label bienvenida = new Label("WELCOME TO FORMULA 1");
        bienvenida.setFont(Font.font("System", FontWeight.BOLD, 26));
        bienvenida.setTextFill(Color.WHITE);
        bienvenida.setOpacity(0);

        Label listos = new Label("Ready to race");
        listos.setFont(Font.font("System", FontWeight.NORMAL, 16));
        listos.setTextFill(Color.web("#d32026"));
        listos.setOpacity(0);

        VBox texto = new VBox(6, bienvenida, listos);
        texto.setAlignment(Pos.CENTER);

        VBox contenido = new VBox(30, logo, texto);
        contenido.setAlignment(Pos.CENTER);
        fondo.getChildren().add(contenido);

        ScaleTransition crecer = new ScaleTransition(Duration.seconds(2.6), logo);
        crecer.setFromX(0.05);
        crecer.setFromY(0.05);
        crecer.setToX(1.0);
        crecer.setToY(1.0);
        crecer.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition apareceLogo = new FadeTransition(Duration.seconds(1.2), logo);
        apareceLogo.setFromValue(0);
        apareceLogo.setToValue(1);

        ParallelTransition entradaLogo = new ParallelTransition(crecer, apareceLogo);

        FadeTransition apareceTexto = new FadeTransition(Duration.seconds(1.0), texto);
        apareceTexto.setFromValue(0);
        apareceTexto.setToValue(1);

        PauseTransition pausaFinal = new PauseTransition(Duration.seconds(0.6));

        FadeTransition desvaneceTodo = new FadeTransition(Duration.seconds(0.6), fondo);
        desvaneceTodo.setFromValue(1);
        desvaneceTodo.setToValue(0);

        SequentialTransition secuencia = new SequentialTransition(
                entradaLogo, apareceTexto, pausaFinal, desvaneceTodo);
        secuencia.setOnFinished(e -> irAlMenuPrincipal());
        secuencia.play();

        return fondo;
    }

    private void irAlMenuPrincipal() {
        stage.getScene().setRoot(construirMenuPrincipal());
    }

    // -----------------------------------------------------------------
    // Menu principal: barra lateral izquierda + contenido a la derecha
    // -----------------------------------------------------------------
    private BorderPane construirMenuPrincipal() {
        BorderPane raiz = new BorderPane();
        raiz.setStyle("-fx-background-color: #0b0e14;");

        raiz.setLeft(construirBarraLateral());

        areaContenido = new StackPane();
        areaContenido.setStyle("-fx-background-color: #0b0e14;");
        ScrollPane scroll = new ScrollPane(areaContenido);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background: #0b0e14; -fx-background-color: #0b0e14;");
        raiz.setCenter(scroll);

        mostrarPantallaBienvenidaMenu();
        return raiz;
    }

    private VBox construirBarraLateral() {
        VBox barra = new VBox();
        barra.setPrefWidth(260);
        barra.setMinWidth(260);
        barra.setStyle("-fx-background-color: #11161d; -fx-border-color: #21262d; -fx-border-width: 0 1 0 0;");
        barra.setPadding(new Insets(20, 14, 20, 14));
        barra.setSpacing(6);

        Label titulo = new Label("FORMULA 1");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.setTextFill(Color.WHITE);
        Label subtitulo = new Label("Panel de gestion");
        subtitulo.setFont(Font.font("System", 11));
        subtitulo.setTextFill(Color.web("#8b949e"));
        VBox encabezado = new VBox(2, titulo, subtitulo);
        encabezado.setPadding(new Insets(4, 0, 20, 6));

        barra.getChildren().add(encabezado);
        barra.getChildren().add(botonBarraLateral("Gestionar circuitos",
                () -> mostrarPanel(new PanelCircuitos(circuitManager).construirVista())));
        barra.getChildren().add(botonBarraLateral("Gestionar pilotos",
                () -> mostrarPanel(new PanelPilotos(pilotManager).construirVista())));
        barra.getChildren().add(botonBarraLateral("Gestionar equipos",
                () -> mostrarPanel(new PanelEquipos(teamManager).construirVista())));
        barra.getChildren().add(botonBarraLateral("Gestionar vehiculos",
                () -> mostrarPanel(new PanelVehiculos(vehicleManager).construirVista())));
        barra.getChildren().add(botonBarraLateral("Configurar un vehiculo",
                () -> mostrarPanel(new PanelConfiguracionVehiculo(vehicleManager, configManager).construirVista())));
        barra.getChildren().add(botonBarraLateral("Simular clasificacion",
                () -> mostrarPanel(new PanelSimulacionClasificacion(circuitManager, pilotManager, vehicleManager,
                        configManager, historial).construirVista())));
        barra.getChildren().add(botonBarraLateral("Historial de clasificaciones",
                () -> mostrarPanel(new PanelHistorial(historial).construirVista())));
        barra.getChildren().add(botonBarraLateral("Records de vuelta",
                () -> mostrarPanel(new PanelRecords(recordsHistoricos).construirVista())));

        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);
        barra.getChildren().add(espacio);

        Button botonARodar = new Button("¡A RODAR!");
        botonARodar.setMaxWidth(Double.MAX_VALUE);
        botonARodar.setPrefHeight(56);
        botonARodar.setFont(Font.font("System", FontWeight.BOLD, 16));
        botonARodar.setStyle("-fx-background-color: #d32026; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        botonARodar.setOnMouseEntered(e -> botonARodar.setStyle(
                "-fx-background-color: #ff2d33; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        botonARodar.setOnMouseExited(e -> botonARodar.setStyle(
                "-fx-background-color: #d32026; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;"));
        botonARodar.setOnAction(e -> abrirConfiguracionYArrancarCarrera());

        Button botonSalir = new Button("Salir");
        botonSalir.setMaxWidth(Double.MAX_VALUE);
        botonSalir.setPrefHeight(40);
        botonSalir.setFont(Font.font("System", FontWeight.BOLD, 13));
        botonSalir.setStyle("-fx-background-color: #21262d; -fx-text-fill: #f0f6fc; -fx-background-radius: 8; -fx-cursor: hand;");
        botonSalir.setOnAction(e -> stage.close());

        VBox acciones = new VBox(10, botonARodar, botonSalir);
        acciones.setPadding(new Insets(16, 0, 0, 0));
        barra.getChildren().add(acciones);

        return barra;
    }

    /** Boton de la barra lateral: al hacer click, cambia el contenido de la derecha y se marca como activo. */
    private Button botonBarraLateral(String texto, Runnable accion) {
        Button boton = new Button(texto);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setAlignment(Pos.CENTER_LEFT);
        boton.setPrefHeight(42);
        boton.setFont(Font.font("System", FontWeight.BOLD, 13));
        aplicarEstiloInactivo(boton);
        boton.setOnAction(e -> {
            accion.run();
            marcarActivo(texto);
        });
        botonesBarraLateral.put(texto, boton);
        return boton;
    }

    private void aplicarEstiloInactivo(Button boton) {
        boton.setStyle("-fx-background-color: transparent; -fx-text-fill: #c9d1d9; -fx-background-radius: 6; -fx-cursor: hand;");
    }

    private void aplicarEstiloActivo(Button boton) {
        boton.setStyle("-fx-background-color: #21262d; -fx-text-fill: white; -fx-border-color: #d32026; "
                + "-fx-border-width: 0 0 0 3; -fx-background-radius: 6; -fx-cursor: hand;");
    }

    private void marcarActivo(String texto) {
        for (Map.Entry<String, Button> entrada : botonesBarraLateral.entrySet()) {
            if (entrada.getKey().equals(texto)) {
                aplicarEstiloActivo(entrada.getValue());
            } else {
                aplicarEstiloInactivo(entrada.getValue());
            }
        }
    }

    private void mostrarPanel(Node panel) {
        areaContenido.getChildren().setAll(panel);
    }

    private void mostrarPantallaBienvenidaMenu() {
        VBox bienvenida = new VBox(10);
        bienvenida.setAlignment(Pos.CENTER);
        Label titulo = new Label("Elige una opcion en el menu de la izquierda");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        titulo.setTextFill(Color.WHITE);
        Label subtitulo = new Label("o presiona ¡A RODAR! para largar una carrera.");
        subtitulo.setFont(Font.font("System", 14));
        subtitulo.setTextFill(Color.web("#8b949e"));
        bienvenida.getChildren().addAll(titulo, subtitulo);
        bienvenida.setPadding(new Insets(260, 0, 0, 0));
        mostrarPanel(bienvenida);
    }

    // -----------------------------------------------------------------
    // "A rodar!" -> elegir pista/clima/gasolina -> vista de carrera
    // -----------------------------------------------------------------
    private void abrirConfiguracionYArrancarCarrera() {
        Optional<ConfiguracionCarrera> configuracion = new ConfiguracionCarreraDialog()
                .mostrarYEsperar(circuitManager.listar());
        configuracion.ifPresent(this::arrancarCarrera);
    }

    private void arrancarCarrera(ConfiguracionCarrera configCarrera) {
        vistaCarreraActual = new VistaCarreraEnVivo(pilotManager, vehicleManager, configManager, configCarrera);
        BorderPane vista = vistaCarreraActual.construirVista(this::volverAlMenu);
        stage.getScene().setRoot(vista);
        vistaCarreraActual.iniciarBucle();
    }

    private void volverAlMenu() {
        stage.getScene().setRoot(construirMenuPrincipal());
    }

    // -----------------------------------------------------------------
    // Datos iniciales (mismo catalogo real del proyecto que usa RaceTrackApp)
    // -----------------------------------------------------------------
    private void cargarDatosDelProyecto() {
        F1CircuitFactory circuitFactory = new F1CircuitFactory();
        for (int i = 1; i <= 10; i++) {
            circuitManager.agregar(circuitFactory.createCircuit(i));
        }

        F1PilotFactory pilotFactory = new F1PilotFactory();
        for (int i = 1; i <= 20; i++) {
            pilotManager.agregar(pilotFactory.createPilot(i));
        }

        teamManager.agregar(new RedBullTeamFactory().createtTeam(1));
        teamManager.agregar(new MercedesTeamFactory().createtTeam(2));
        teamManager.agregar(new FerrariTeamFactory().createtTeam(3));

        VehicleFactory[] fabricas = {
                new RedBullFactory(), new MercedesFactory(), new FerrariFactory(), new McLarenFactory(),
                new AstonMartinFactory(), new AlpineFactory(), new AlfaRomeoFactory(), new HaasFactory(),
                new AlphaTauriFactory(), new WilliamsFactory()
        };
        for (int i = 0; i < fabricas.length; i++) {
            Vehicle vehiculo = fabricas[i].crearVehiculo(i + 1);
            vehicleManager.agregar(vehiculo);
        }

        ModoConduccion[] modos = ModoConduccion.values();
        CargaAerodinamica[] aeros = CargaAerodinamica.values();
        PresionNeumaticos[] presiones = PresionNeumaticos.values();
        EstrategiaCombustible[] combustibles = EstrategiaCombustible.values();
        for (int i = 1; i <= 10; i++) {
            configManager.guardar(new ConfiguracionVehiculo(i,
                    modos[i % modos.length], aeros[i % aeros.length],
                    presiones[i % presiones.length], combustibles[i % combustibles.length]));
        }

        F1ResultFactory resultFactory = new F1ResultFactory();
        for (int i = 1; i <= 7; i++) {
            recordsHistoricos.add(resultFactory.createResult(i));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
