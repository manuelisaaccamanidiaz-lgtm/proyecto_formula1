package com.formula1.javafx;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.formula1.circuitfactories.F1CircuitFactory;
import com.formula1.circuits.Circuit;
import com.formula1.config.CargaAerodinamica;
import com.formula1.config.ConfiguracionManager;
import com.formula1.config.ConfiguracionVehiculo;
import com.formula1.config.EstrategiaCombustible;
import com.formula1.config.PresionNeumaticos;
import com.formula1.factories.VehicleFactory;
import com.formula1.managers.CircuitManager;
import com.formula1.managers.PilotManager;
import com.formula1.managers.TeamManager;
import com.formula1.managers.VehicleManager;
import com.formula1.menu.OperacionesMenu;
import com.formula1.pilotfactories.F1PilotFactory;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Punto de entrada JavaFX principal del proyecto: un menu con tematica de
 * Formula 1 (fondo oscuro, acentos rojo/checkered) cuyos botones equivalen
 * uno a uno a las opciones del menu de texto de {@code Main.java}, mas el
 * boton destacado "A RODAR!" que abre la seleccion de pista/clima/gasolina
 * y lanza la carrera en tiempo real ({@link VistaCarreraEnVivo}).
 *
 * Los botones de gestion (circuitos, pilotos, equipos, vehiculos,
 * configuracion, simulacion de clasificacion, historial, records) delegan
 * en {@link OperacionesMenu}, la MISMA logica que usa Main.java por
 * consola (evita duplicar el CRUD dos veces). Esos dialogos siguen siendo
 * JOptionPane, igual que en el proyecto original; solo cambia que ahora se
 * disparan desde un boton en vez de un menu de texto numerado. Main.java
 * ya invocaba JOptionPane desde el hilo main() (que tampoco es el AWT
 * Event Dispatch Thread); aqui se hace exactamente lo mismo pero desde el
 * hilo de aplicacion de JavaFX, con el mismo resultado practico.
 */
public class MenuPrincipalApp extends Application {

    private final CircuitManager circuitManager = new CircuitManager();
    private final PilotManager pilotManager = new PilotManager();
    private final TeamManager teamManager = new TeamManager();
    private final VehicleManager vehicleManager = new VehicleManager();
    private final ConfiguracionManager configManager = new ConfiguracionManager();
    private final HistorialClasificacion historial = new HistorialClasificacion();
    private final List<Result> recordsHistoricos = new ArrayList<>();

    private OperacionesMenu operaciones;
    private Stage stage;
    private VistaCarreraEnVivo vistaCarreraActual;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        cargarDatosDelProyecto();
        operaciones = new OperacionesMenu(circuitManager, pilotManager, teamManager, vehicleManager,
                configManager, historial, recordsHistoricos);

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

        ImageView logo = new ImageView(new Image(
                getClass().getResourceAsStream("/images/logo-f1.png")));
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

        // El logo crece lento y fluido, como si estuviera cargando.
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
    // Menu principal con botones tematizados
    // -----------------------------------------------------------------
    private BorderPane construirMenuPrincipal() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0b0e14;");

        VBox encabezado = new VBox(4);
        encabezado.setAlignment(Pos.CENTER);
        encabezado.setPadding(new Insets(28, 0, 18, 0));
        encabezado.setStyle("-fx-background-color: linear-gradient(to bottom, #d32026, #0b0e14);");
        Label titulo = new Label("SIMULADOR DE FORMULA 1");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 30));
        titulo.setTextFill(Color.WHITE);
        Label subtitulo = new Label("Gestiona la temporada o salta directo a la pista");
        subtitulo.setFont(Font.font("System", 14));
        subtitulo.setTextFill(Color.web("#f5dde0"));
        encabezado.getChildren().addAll(titulo, subtitulo);
        root.setTop(encabezado);

        GridPane grilla = new GridPane();
        grilla.setAlignment(Pos.CENTER);
        grilla.setHgap(18);
        grilla.setVgap(18);
        grilla.setPadding(new Insets(30));

        grilla.add(botonMenu("Gestionar circuitos", operaciones::menuCircuitos), 0, 0);
        grilla.add(botonMenu("Gestionar pilotos", operaciones::menuPilotos), 1, 0);
        grilla.add(botonMenu("Gestionar equipos", operaciones::menuEquipos), 2, 0);
        grilla.add(botonMenu("Gestionar vehiculos", operaciones::menuVehiculos), 0, 1);
        grilla.add(botonMenu("Configurar un vehiculo", operaciones::menuConfiguracion), 1, 1);
        grilla.add(botonMenu("Simular clasificacion", operaciones::menuSimulacion), 2, 1);
        grilla.add(botonMenu("Ver historial de clasificaciones", operaciones::menuHistorial), 0, 2);
        grilla.add(botonMenu("Ver records de vuelta", operaciones::menuRecords), 1, 2);
        grilla.add(botonSalir(), 2, 2);

        VBox centro = new VBox(24);
        centro.setAlignment(Pos.CENTER);
        centro.getChildren().addAll(grilla, botonARodar());
        root.setCenter(centro);
        return root;
    }

    private Button botonMenu(String texto, Runnable accion) {
        Button boton = new Button(texto);
        boton.setPrefSize(340, 64);
        boton.setFont(Font.font("System", FontWeight.BOLD, 14));
        boton.setStyle("-fx-background-color: #161b22; -fx-text-fill: #f0f6fc; -fx-background-radius: 8; "
                + "-fx-border-color: #30363d; -fx-border-radius: 8; -fx-cursor: hand;");
        boton.setOnMouseEntered(e -> boton.setStyle("-fx-background-color: #21262d; -fx-text-fill: #f0f6fc; "
                + "-fx-background-radius: 8; -fx-border-color: #d32026; -fx-border-radius: 8; -fx-cursor: hand;"));
        boton.setOnMouseExited(e -> boton.setStyle("-fx-background-color: #161b22; -fx-text-fill: #f0f6fc; "
                + "-fx-background-radius: 8; -fx-border-color: #30363d; -fx-border-radius: 8; -fx-cursor: hand;"));
        boton.setOnAction(e -> accion.run());
        return boton;
    }

    private Button botonSalir() {
        Button boton = new Button("Salir");
        boton.setPrefSize(340, 64);
        boton.setFont(Font.font("System", FontWeight.BOLD, 14));
        boton.setStyle("-fx-background-color: #21262d; -fx-text-fill: #f0f6fc; -fx-background-radius: 8; "
                + "-fx-border-color: #30363d; -fx-border-radius: 8; -fx-cursor: hand;");
        boton.setOnAction(e -> stage.close());
        return boton;
    }

    private Button botonARodar() {
        Button boton = new Button("¡A RODAR!");
        boton.setPrefSize(420, 84);
        boton.setFont(Font.font("System", FontWeight.BOLD, 22));
        boton.setStyle("-fx-background-color: #d32026; -fx-text-fill: white; -fx-background-radius: 12; -fx-cursor: hand;");
        boton.setOnMouseEntered(e -> boton.setStyle(
                "-fx-background-color: #ff2d33; -fx-text-fill: white; -fx-background-radius: 12; -fx-cursor: hand;"));
        boton.setOnMouseExited(e -> boton.setStyle(
                "-fx-background-color: #d32026; -fx-text-fill: white; -fx-background-radius: 12; -fx-cursor: hand;"));
        boton.setOnAction(e -> abrirConfiguracionYArrancarCarrera());
        return boton;
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
