package com.formula1.javafx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.formula1.circuits.Circuit;
import com.formula1.factories.VehicleFactory;
import com.formula1.pilots.Pilot;
import com.formula1.vehicles.Vehicle;
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

/**
 * Simulacion visual de una carrera de Formula 1 en una pista ovalada.
 * Cada auto se mueve a una velocidad angular proporcional a su
 * velocidad_maxima (km/h), de modo que los autos mas rapidos completan
 * vueltas mas rapido y se van adelantando entre si.
 *
 * Esta clase es independiente del Main.java de consola: no lo modifica
 * ni depende de el. Solo reutiliza Pilot, Vehicle, VehicleFactory y las
 * fabricas de vehiculos ya existentes en el proyecto.
 */
public class RaceTrackApp extends Application {

    // ------------------------------------------------------------------
    // Geometria de la pista
    // ------------------------------------------------------------------
    private static final double CENTRO_X = 480;
    private static final double CENTRO_Y = 340;
    private static final double RX_EXTERIOR = 420;
    private static final double RY_EXTERIOR = 250;
    private static final double ANCHO_PISTA = 100; // grosor del anillo de asfalto

    // Velocidad angular de referencia: un auto a REFERENCIA_KMH completa
    // una vuelta en SEGUNDOS_POR_VUELTA_REF segundos (a velocidad de simulacion x1)
    private static final double REFERENCIA_KMH = 350.0;
    private static final double SEGUNDOS_POR_VUELTA_REF = 9.0;

    private final List<RaceCar> autos = new ArrayList<>();
    private final Label[] filasClasificacion = new Label[10];

    private boolean enPausa = false;
    private long ultimoFrameNs = -1;
    private double multiplicadorVelocidad = 1.0;

    private Canvas canvas;
    private Circuit circuito;

    @Override
    public void start(Stage stage) {
        circuito = new Circuit(1, "Silverstone", 5.89,
                "Uno de los circuitos mas rapidos del calendario, con curvas de alta velocidad "
                        + "como Maggotts y Becketts.", (byte) 52);

        crearAutos();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #101418;");

        canvas = new Canvas(960, 680);
        root.setCenter(canvas);

        root.setRight(crearPanelClasificacion());
        root.setBottom(crearPanelControles());

        dibujarEscena(); // primer frame estatico antes de iniciar

        Scene scene = new Scene(root, 1300, 760);
        stage.setTitle("F1 Simulacion - " + circuito.getNombre());
        stage.setScene(scene);
        stage.show();

        iniciarBucleAnimacion();
    }

    // ------------------------------------------------------------------
    // Creacion de los 10 pilotos + 10 vehiculos (uno por equipo)
    // ------------------------------------------------------------------
    private void crearAutos() {
        String[] nombresPilotos = {
                "Max Verstappen", "Lewis Hamilton", "Charles Leclerc", "Lando Norris",
                "Fernando Alonso", "Esteban Ocon", "Valtteri Bottas", "Kevin Magnussen",
                "Yuki Tsunoda", "Alexander Albon"
        };
        String[] equiposNombres = {
                "Red Bull Racing", "Mercedes-AMG Petronas", "Ferrari", "McLaren",
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

        for (int i = 0; i < 10; i++) {
            Pilot piloto = new Pilot(i + 1, nombresPilotos[i], "Lider");
            Vehicle vehiculo = fabricas[i].crearVehiculo(i + 1);
            autos.add(new RaceCar(piloto, vehiculo, equiposNombres[i], colores[i], i));
        }
    }

    // ------------------------------------------------------------------
    // Panel lateral: clasificacion en vivo
    // ------------------------------------------------------------------
    private VBox crearPanelClasificacion() {
        VBox panel = new VBox(6);
        panel.setPadding(new Insets(16));
        panel.setPrefWidth(340);
        panel.setStyle("-fx-background-color: #181c22;");

        Label titulo = new Label("Clasificacion en vivo");
        titulo.setTextFill(Color.WHITE);
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label subtitulo = new Label(circuito.getNombre() + " - " + circuito.getVueltas() + " vueltas en carrera real");
        subtitulo.setTextFill(Color.web("#9aa4b2"));
        subtitulo.setFont(Font.font(12));
        subtitulo.setWrapText(true);

        panel.getChildren().addAll(titulo, subtitulo);

        for (int i = 0; i < filasClasificacion.length; i++) {
            Label fila = new Label();
            fila.setTextFill(Color.WHITE);
            fila.setFont(Font.font("Monospaced", 13));
            fila.setPadding(new Insets(4, 6, 4, 6));
            filasClasificacion[i] = fila;
            panel.getChildren().add(fila);
        }

        actualizarClasificacion();
        return panel;
    }

    // ------------------------------------------------------------------
    // Panel inferior: controles de la simulacion
    // ------------------------------------------------------------------
    private HBox crearPanelControles() {
        HBox controles = new HBox(16);
        controles.setPadding(new Insets(12, 16, 16, 16));
        controles.setAlignment(Pos.CENTER_LEFT);
        controles.setStyle("-fx-background-color: #181c22;");

        Button btnPausa = new Button("Pausar");
        btnPausa.setOnAction(e -> {
            enPausa = !enPausa;
            btnPausa.setText(enPausa ? "Reanudar" : "Pausar");
        });

        Button btnReiniciar = new Button("Reiniciar carrera");
        btnReiniciar.setOnAction(e -> {
            for (RaceCar auto : autos) {
                auto.avanzar(0, 0); // no-op, mantiene el objeto
            }
            autos.clear();
            crearAutos();
        });

        Label lblVelocidad = new Label("Velocidad de simulacion: x1.00");
        lblVelocidad.setTextFill(Color.WHITE);

        Slider slider = new Slider(0.25, 3.0, 1.0);
        slider.setPrefWidth(220);
        slider.valueProperty().addListener((obs, oldV, newV) -> {
            multiplicadorVelocidad = newV.doubleValue();
            lblVelocidad.setText(String.format("Velocidad de simulacion: x%.2f", multiplicadorVelocidad));
        });

        controles.getChildren().addAll(btnPausa, btnReiniciar, slider, lblVelocidad);
        return controles;
    }

    // ------------------------------------------------------------------
    // Bucle de animacion
    // ------------------------------------------------------------------
    private void iniciarBucleAnimacion() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (ultimoFrameNs < 0) {
                    ultimoFrameNs = now;
                    return;
                }
                double dtSeg = (now - ultimoFrameNs) / 1_000_000_000.0;
                ultimoFrameNs = now;

                if (!enPausa) {
                    double dtEfectivo = dtSeg * multiplicadorVelocidad;
                    for (RaceCar auto : autos) {
                        double velKmh = auto.getVehiculo().getVelocidad_maxima();
                        double omega = (velKmh / REFERENCIA_KMH) * (2 * Math.PI / SEGUNDOS_POR_VUELTA_REF);
                        auto.avanzar(omega, dtEfectivo);
                    }
                }

                dibujarEscena();
                actualizarClasificacion();
            }
        };
        timer.start();
    }

    // ------------------------------------------------------------------
    // Dibujo de la pista y los autos
    // ------------------------------------------------------------------
    private void dibujarEscena() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Cesped de fondo
        gc.setFill(Color.web("#274b21"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double rxInterior = RX_EXTERIOR - ANCHO_PISTA;
        double ryInterior = RY_EXTERIOR - ANCHO_PISTA;

        // Asfalto: ovalo exterior relleno, luego "recortamos" el centro con el color del cesped
        gc.setFill(Color.web("#333333"));
        gc.fillOval(CENTRO_X - RX_EXTERIOR, CENTRO_Y - RY_EXTERIOR, RX_EXTERIOR * 2, RY_EXTERIOR * 2);
        gc.setFill(Color.web("#274b21"));
        gc.fillOval(CENTRO_X - rxInterior, CENTRO_Y - ryInterior, rxInterior * 2, ryInterior * 2);

        // Bordes blancos de la pista
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.5);
        gc.strokeOval(CENTRO_X - RX_EXTERIOR, CENTRO_Y - RY_EXTERIOR, RX_EXTERIOR * 2, RY_EXTERIOR * 2);
        gc.strokeOval(CENTRO_X - rxInterior, CENTRO_Y - ryInterior, rxInterior * 2, ryInterior * 2);

        // Linea de meta (arriba, theta = 0)
        double metaXExt = CENTRO_X;
        double metaYExt = CENTRO_Y - RY_EXTERIOR;
        double metaYInt = CENTRO_Y - ryInterior;
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(6);
        gc.strokeLine(metaXExt, metaYExt, metaXExt, metaYInt);

        // Autos
        for (RaceCar auto : autos) {
            double proporcion = auto.getCarril() / 9.0; // 0 (exterior) a 1 (interior)
            double rx = RX_EXTERIOR - 12 - proporcion * (ANCHO_PISTA - 24);
            double ry = rx * (RY_EXTERIOR / RX_EXTERIOR);

            double theta = auto.getAnguloRad();
            double x = CENTRO_X + rx * Math.sin(theta);
            double y = CENTRO_Y - ry * Math.cos(theta);

            gc.setFill(auto.getColor());
            gc.fillOval(x - 9, y - 9, 18, 18);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.2);
            gc.strokeOval(x - 9, y - 9, 18, 18);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("System", FontWeight.BOLD, 10));
            gc.fillText(String.valueOf(auto.getPiloto().getID_pilot()), x - 3, y + 4);
        }
    }

    // ------------------------------------------------------------------
    // Actualiza el texto del panel de clasificacion (orden por distancia)
    // ------------------------------------------------------------------
    private void actualizarClasificacion() {
        List<RaceCar> ordenados = new ArrayList<>(autos);
        ordenados.sort(Comparator.comparingDouble(RaceCar::getDistanciaTotal).reversed());

        for (int i = 0; i < ordenados.size(); i++) {
            RaceCar auto = ordenados.get(i);
            String texto = String.format("%2d. %-16s %-8s %3d km/h",
                    i + 1,
                    acortar(auto.getPiloto().getNombre(), 16),
                    (int) auto.getVueltasCompletas() + " v.",
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
