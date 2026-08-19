package com.formula1.javafx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.formula1.circuits.Circuit;
import com.formula1.factories.VehicleFactory;
import com.formula1.pilots.Pilot;
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
import com.formula1.vehicles.Vehicle;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RaceTrackApp extends Application {

    // ------------------------------------------------------------------
    // Geometria de la pista (Estilizada y carretera mas delgada)
    // ------------------------------------------------------------------
    private static final double CENTRO_X = 480;
    private static final double CENTRO_Y = 340;
    private static final double RX_EXTERIOR = 420;
    private static final double RY_EXTERIOR = 250;
    private static final double ANCHO_PISTA = 40; // Ancho reducido para un estilo mas estilizado
    private static final int MAX_VUELTAS = 3;      // Meta de la carrera

    private static final double REFERENCIA_KMH = 350.0;
    private static final double SEGUNDOS_POR_VUELTA_REF = 9.0;

    private final List<RaceCar> autos = new ArrayList<>();
    private final Label[] filasClasificacion = new Label[10];

    private boolean enPausa = false;
    private boolean carreraFinalizada = false;
    private RaceCar ganador = null;
    private long ultimoFrameNs = -1;
    private double multiplicadorVelocidad = 1.0;

    private Canvas canvas;
    private Circuit circuito;
    private AnimationTimer timer;
    private Label lblEstadoCarrera;

    @Override
    public void start(Stage stage) {
        circuito = new Circuit(1, "Silverstone", 5.89,
                "Circuito rapido y estilizado - F1 Simulation", (byte) 52);

        crearAutos();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f141c;");

        canvas = new Canvas(960, 680);
        root.setCenter(canvas);

        root.setRight(crearPanelClasificacion());
        root.setBottom(crearPanelControles());

        dibujarEscena();

        Scene scene = new Scene(root, 1340, 760);
        stage.setTitle("F1 Simulacion - " + circuito.getNombre());
        stage.setScene(scene);
        stage.show();

        iniciarBucleAnimacion();
    }

    private void crearAutos() {
        String[] nombresPilotos = {
                "Max Verstappen", "Lewis Hamilton", "Charles Leclerc", "Lando Norris",
                "Fernando Alonso", "Esteban Ocon", "Valtteri Bottas", "Kevin Magnussen",
                "Yuki Tsunoda", "Alexander Albon"
        };
        String[] equiposNombres = {
                "Red Bull Racing", "Mercedes-AMG", "Ferrari", "McLaren",
                "Aston Martin", "Alpine", "Alfa Romeo", "Haas", "AlphaTauri", "Williams"
        };
        VehicleFactory[] fabricas = {
                new RedBullFactory(), new MercedesFactory(), new FerrariFactory(), new McLarenFactory(),
                new AstonMartinFactory(), new AlpineFactory(), new AlfaRomeoFactory(), new HaasFactory(),
                new AlphaTauriFactory(), new WilliamsFactory()
        };
        Color[] colores = {
                Color.web("#1E5BC6"), Color.web("#00D7B6"), Color.web("#E8002D"), Color.web("#FF8700"),
                Color.web("#229971"), Color.web("#2293D1"), Color.web("#9B2226"), Color.web("#B6BABD"),
                Color.web("#2B4562"), Color.web("#37BEDD")
        };

        autos.clear();
        for (int i = 0; i < 10; i++) {
            Pilot piloto = new Pilot(i + 1, nombresPilotos[i], "Lider");
            Vehicle vehiculo = fabricas[i].crearVehiculo(i + 1);
            autos.add(new RaceCar(piloto, vehiculo, equiposNombres[i], colores[i], i));
        }
    }

    // ------------------------------------------------------------------
    // Tabla / Panel lateral derecho: Clasificacion en vivo
    // ------------------------------------------------------------------
    private VBox crearPanelClasificacion() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(16));
        panel.setPrefWidth(380);
        panel.setStyle("-fx-background-color: #161b22; -fx-border-color: #21262d; -fx-border-width: 0 0 0 2;");

        Label titulo = new Label("CLASIFICACIÓN EN TIEMPO REAL");
        titulo.setTextFill(Color.web("#f0f6fc"));
        titulo.setFont(Font.font("System", FontWeight.BOLD, 15));

        lblEstadoCarrera = new Label("Carrera a 3 vueltas");
        lblEstadoCarrera.setTextFill(Color.web("#e3b341"));
        lblEstadoCarrera.setFont(Font.font("System", FontWeight.BOLD, 13));

        // Encabezados de la tabla
        HBox cabecera = new HBox();
        cabecera.setPadding(new Insets(6, 4, 6, 4));
        cabecera.setStyle("-fx-background-color: #21262d; -fx-background-radius: 4;");
        
        Label colPos = new Label("POS");
        colPos.setPrefWidth(40);
        colPos.setTextFill(Color.web("#8b949e"));
        colPos.setFont(Font.font("System", FontWeight.BOLD, 11));

        Label colPiloto = new Label("PILOTO");
        colPiloto.setPrefWidth(160);
        colPiloto.setTextFill(Color.web("#8b949e"));
        colPiloto.setFont(Font.font("System", FontWeight.BOLD, 11));

        Label colVueltas = new Label("VUELTAS");
        colVueltas.setPrefWidth(80);
        colVueltas.setTextFill(Color.web("#8b949e"));
        colVueltas.setFont(Font.font("System", FontWeight.BOLD, 11));

        Label colVel = new Label("VEL");
        colVel.setPrefWidth(60);
        colVel.setTextFill(Color.web("#8b949e"));
        colVel.setFont(Font.font("System", FontWeight.BOLD, 11));

        cabecera.getChildren().addAll(colPos, colPiloto, colVueltas, colVel);
        panel.getChildren().addAll(titulo, lblEstadoCarrera, cabecera);

        // Filas de los 10 pilotos
        for (int i = 0; i < filasClasificacion.length; i++) {
            Label fila = new Label();
            fila.setTextFill(Color.WHITE);
            fila.setFont(Font.font("Monospaced", 12));
            fila.setStyle("-fx-background-color: #0d1117; -fx-padding: 6px; -fx-background-radius: 4px;");
            filasClasificacion[i] = fila;
            panel.getChildren().add(fila);
        }

        actualizarClasificacion();
        return panel;
    }

    private HBox crearPanelControles() {
        HBox controles = new HBox(16);
        controles.setPadding(new Insets(12, 16, 16, 16));
        controles.setAlignment(Pos.CENTER_LEFT);
        controles.setStyle("-fx-background-color: #161b22;");

        Button btnPausa = new Button("Pausar");
        btnPausa.setStyle("-fx-background-color: #21262d; -fx-text-fill: white; -fx-cursor: hand;");
        btnPausa.setOnAction(e -> {
            if (!carreraFinalizada) {
                enPausa = !enPausa;
                btnPausa.setText(enPausa ? "Reanudar" : "Pausar");
            }
        });

        Button btnReiniciar = new Button("Reiniciar carrera");
        btnReiniciar.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-cursor: hand;");
        btnReiniciar.setOnAction(e -> {
            carreraFinalizada = false;
            enPausa = false;
            ganador = null;
            lblEstadoCarrera.setText("Carrera a 3 vueltas");
            lblEstadoCarrera.setTextFill(Color.web("#e3b341"));
            btnPausa.setText("Pausar");
            crearAutos();
        });

        Label lblVelocidad = new Label("Velocidad de simulacion: x1.00");
        lblVelocidad.setTextFill(Color.WHITE);

        Slider slider = new Slider(0.25, 3.0, 1.0);
        slider.setPrefWidth(200);
        slider.valueProperty().addListener((obs, oldV, newV) -> {
            multiplicadorVelocidad = newV.doubleValue();
            lblVelocidad.setText(String.format("Velocidad: x%.2f", multiplicadorVelocidad));
        });

        controles.getChildren().addAll(btnPausa, btnReiniciar, slider, lblVelocidad);
        return controles;
    }

    private void iniciarBucleAnimacion() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (ultimoFrameNs < 0) {
                    ultimoFrameNs = now;
                    return;
                }
                double dtSeg = (now - ultimoFrameNs) / 1_000_000_000.0;
                ultimoFrameNs = now;

                if (!enPausa && !carreraFinalizada) {
                    double dtEfectivo = dtSeg * multiplicadorVelocidad;
                    for (RaceCar auto : autos) {
                        double velKmh = auto.getVehiculo().getVelocidad_maxima();
                        double omega = (velKmh / REFERENCIA_KMH) * (2 * Math.PI / SEGUNDOS_POR_VUELTA_REF);
                        auto.avanzar(omega, dtEfectivo);

                        // Verificar condicion de fin de carrera (3 vueltas)
                        if (auto.getVueltasCompletas() >= MAX_VUELTAS && !carreraFinalizada) {
                            carreraFinalizada = true;
                            ganador = auto;
                            lblEstadoCarrera.setText("¡CARRERA FINALIZADA!");
                            lblEstadoCarrera.setTextFill(Color.web("#3fb950"));
                        }
                    }
                }

                dibujarEscena();
                actualizarClasificacion();
            }
        };
        timer.start();
    }

    // ------------------------------------------------------------------
    // Dibujo del circuito estilizado
    // ------------------------------------------------------------------
    private void dibujarEscena() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Fondo oscuro tipo circuito nocturno
        gc.setFill(Color.web("#0b0e14"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double rxInterior = RX_EXTERIOR - ANCHO_PISTA;
        double ryInterior = RY_EXTERIOR - ANCHO_PISTA;

        // Borde exterior - Piano/Kerbs (Rojo y Blanco)
        gc.setStroke(Color.web("#d73a49"));
        gc.setLineWidth(ANCHO_PISTA + 8);
        gc.strokeOval(CENTRO_X - (RX_EXTERIOR - ANCHO_PISTA / 2), CENTRO_Y - (RY_EXTERIOR - ANCHO_PISTA / 2),
                (RX_EXTERIOR - ANCHO_PISTA / 2) * 2, (RY_EXTERIOR - ANCHO_PISTA / 2) * 2);

        // Pista de Asfalto delgada
        gc.setStroke(Color.web("#1f242d"));
        gc.setLineWidth(ANCHO_PISTA);
        gc.strokeOval(CENTRO_X - (RX_EXTERIOR - ANCHO_PISTA / 2), CENTRO_Y - (RY_EXTERIOR - ANCHO_PISTA / 2),
                (RX_EXTERIOR - ANCHO_PISTA / 2) * 2, (RY_EXTERIOR - ANCHO_PISTA / 2) * 2);

        // Linea discontinua en el centro de la pista
        gc.setStroke(Color.web("#30363d"));
        gc.setLineWidth(1.5);
        gc.setLineDashes(10, 10);
        gc.strokeOval(CENTRO_X - (RX_EXTERIOR - ANCHO_PISTA / 2), CENTRO_Y - (RY_EXTERIOR - ANCHO_PISTA / 2),
                (RX_EXTERIOR - ANCHO_PISTA / 2) * 2, (RY_EXTERIOR - ANCHO_PISTA / 2) * 2);
        gc.setLineDashes(null); // Resetear discontinuidad

        // Bordes blancos de seguridad
        gc.setStroke(Color.web("#8b949e"));
        gc.setLineWidth(1.5);
        gc.strokeOval(CENTRO_X - RX_EXTERIOR, CENTRO_Y - RY_EXTERIOR, RX_EXTERIOR * 2, RY_EXTERIOR * 2);
        gc.strokeOval(CENTRO_X - rxInterior, CENTRO_Y - ryInterior, rxInterior * 2, ryInterior * 2);

        // Linea de Meta (Meta a Cuadros)
        double metaXExt = CENTRO_X;
        double metaYExt = CENTRO_Y - RY_EXTERIOR;
        double metaYInt = CENTRO_Y - ryInterior;
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);
        gc.strokeLine(metaXExt, metaYExt, metaXExt, metaYInt);

        // Autos en la pista
        for (RaceCar auto : autos) {
            double proporcion = auto.getCarril() / 9.0;
            double rx = RX_EXTERIOR - 6 - proporcion * (ANCHO_PISTA - 12);
            double ry = rx * (RY_EXTERIOR / RX_EXTERIOR);

            double theta = auto.getAnguloRad();
            double x = CENTRO_X + rx * Math.sin(theta);
            double y = CENTRO_Y - ry * Math.cos(theta);

            // Resplandor/Sombra del auto
            gc.setFill(auto.getColor());
            gc.fillOval(x - 6, y - 6, 12, 12);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.strokeOval(x - 6, y - 6, 12, 12);
        }

        // Cartel del Ganador cuando termina la carrera
        if (carreraFinalizada && ganador != null) {
            gc.setFill(Color.web("#000000", 0.75));
            gc.fillRect(CENTRO_X - 180, CENTRO_Y - 45, 360, 90);
            gc.setStroke(Color.web("#3fb950"));
            gc.setLineWidth(2);
            gc.strokeRect(CENTRO_X - 180, CENTRO_Y - 45, 360, 90);

            gc.setFill(Color.web("#3fb950"));
            gc.setFont(Font.font("System", FontWeight.BOLD, 20));
            gc.fillText("¡GANADOR DE LA CARRERA!", CENTRO_X - 140, CENTRO_Y - 10);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("System", FontWeight.BOLD, 16));
            gc.fillText(ganador.getPiloto().getNombre() + " (" + ganador.getEquipo() + ")", CENTRO_X - 140, CENTRO_Y + 20);
        }
    }

    // ------------------------------------------------------------------
    // Actualizacion de lista/tabla de posiciones en tiempo real
    // ------------------------------------------------------------------
    private void actualizarClasificacion() {
        List<RaceCar> ordenados = new ArrayList<>(autos);
        ordenados.sort(Comparator.comparingDouble(RaceCar::getDistanciaTotal).reversed());

        for (int i = 0; i < ordenados.size(); i++) {
            RaceCar auto = ordenados.get(i);
            int vueltas = Math.min((int) auto.getVueltasCompletas(), MAX_VUELTAS);

            String texto = String.format(" P%-2d  %-16s   %d/%d v    %3d km/h",
                    i + 1,
                    acortar(auto.getPiloto().getNombre(), 16),
                    vueltas,
                    MAX_VUELTAS,
                    (int) auto.getVehiculo().getVelocidad_maxima());
            
            filasClasificacion[i].setText(texto);
        }
    }

    private String acortar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max - 1) + ".";
    }

    public static void main(String[] args) {
        launch(args);
    }
}