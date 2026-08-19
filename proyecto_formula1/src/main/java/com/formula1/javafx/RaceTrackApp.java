package com.formula1.javafx;

import java.util.ArrayList;
import java.util.List;

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
import com.formula1.managers.VehicleManager;
import com.formula1.model.EstadoVehiculo;
import com.formula1.pilotfactories.F1PilotFactory;
import com.formula1.pilots.Pilot;
import com.formula1.simulation.Clima;
import com.formula1.simulation.MotorSimulacion;
import com.formula1.simulation.Participante;
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

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Controlador/arranque de la interfaz JavaFX. Esta clase SOLO conecta
 * (managers/factories del proyecto) -> (MotorSimulacion) -> (vistas), sin
 * contener logica de simulacion propia: esa vive en
 * com.formula1.simulation.MotorSimulacion y en
 * com.formula1.simulation.SimuladorClasificacion (reutilizado).
 *
 * ¿Por que AnimationTimer y no un hilo aparte + Platform.runLater()?
 * AnimationTimer.handle(long now) ya se ejecuta en el hilo de aplicacion de
 * JavaFX (JavaFX Application Thread), una vez por cuadro (~60 veces/seg). Por
 * eso aqui es seguro llamar directamente a motor.actualizar(dt) y luego a los
 * metodos de las vistas: no hay otro hilo tocando los nodos de JavaFX, asi
 * que Platform.runLater() no es necesario. Si en el futuro se quisiera cargar
 * datos pesados en segundo plano (ej. leer un archivo grande), eso si
 * ameritaria un hilo aparte + Platform.runLater() para volver al hilo de
 * JavaFX antes de tocar la interfaz.
 */
public class RaceTrackApp extends Application {

    // Managers reales del proyecto (mismos que usa Main.java)
    private final CircuitManager circuitManager = new CircuitManager();
    private final PilotManager pilotManager = new PilotManager();
    private final VehicleManager vehicleManager = new VehicleManager();
    private final ConfiguracionManager configManager = new ConfiguracionManager();

    private MotorSimulacion motor;
    private CircuitoView circuitoView;
    private final ClasificacionView clasificacionView = new ClasificacionView();
    private final PanelTelemetria panelTelemetria = new PanelTelemetria();
    private final PanelControles panelControles = new PanelControles();

    private Canvas canvas;
    private AnimationTimer timer;
    private long ultimoFrameNs = -1;
    private boolean corriendo = false;

    @Override
    public void start(Stage stage) {
        cargarDatosDelProyecto();
        Circuit circuito = circuitManager.buscarPorId(2); // Silverstone (F1CircuitFactory)
        motor = crearMotor(circuito);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f141c;");

        canvas = new Canvas(960, 640);
        circuitoView = new CircuitoView(canvas);
        root.setCenter(canvas);

        VBox panelDerecho = new VBox(10);
        panelDerecho.getChildren().addAll(clasificacionView.construirPanel(), panelTelemetria.construirPanel(motor.getClima()));
        root.setRight(panelDerecho);

        HBox controles = panelControles.construirPanel(this::alternarIniciarPausar, this::reiniciarCarrera, this::cambiarVelocidad);
        root.setBottom(controles);

        refrescarVistas();

        Scene scene = new Scene(root, 1400, 760);
        stage.setTitle("F1 Simulación en tiempo real - " + circuito.getNombre());
        stage.setScene(scene);
        stage.show();

        iniciarBucleAnimacion();
    }

    /**
     * Carga los datos REALES del proyecto: circuitos (F1CircuitFactory),
     * pilotos (F1PilotFactory) y vehiculos (vehiclefactories), igual que
     * Main.cargarDatosIniciales(). No se inventan nombres ni valores nuevos.
     */
    private void cargarDatosDelProyecto() {
        F1CircuitFactory circuitFactory = new F1CircuitFactory();
        for (int i = 1; i <= 7; i++) {
            circuitManager.agregar(circuitFactory.createCircuit(i));
        }

        F1PilotFactory pilotFactory = new F1PilotFactory();
        for (int i = 1; i <= 20; i++) {
            pilotManager.agregar(pilotFactory.createPilot(i));
        }

        // Una fabrica de vehiculo por equipo, en el mismo orden que idEquipo
        // (1 Red Bull ... 10 Williams) usado por F1PilotFactory.
        VehicleFactory[] fabricas = {
                new RedBullFactory(), new MercedesFactory(), new FerrariFactory(), new McLarenFactory(),
                new AstonMartinFactory(), new AlpineFactory(), new AlfaRomeoFactory(), new HaasFactory(),
                new AlphaTauriFactory(), new WilliamsFactory()
        };
        for (int i = 0; i < fabricas.length; i++) {
            Vehicle vehiculo = fabricas[i].crearVehiculo(i + 1);
            vehicleManager.agregar(vehiculo);
        }

        // Reglaje inicial por vehiculo (usa las mismas enums que ya expone el
        // menu "Configurar vehiculo" de Main.java). Se varia por vehiculo solo
        // para que el grid no sea identico; sigue siendo reglaje valido segun
        // el proyecto, no una regla nueva.
        ModoConduccion[] modos = ModoConduccion.values();
        CargaAerodinamica[] aeros = CargaAerodinamica.values();
        PresionNeumaticos[] presiones = PresionNeumaticos.values();
        EstrategiaCombustible[] combustibles = EstrategiaCombustible.values();
        for (int i = 1; i <= 10; i++) {
            configManager.guardar(new ConfiguracionVehiculo(i,
                    modos[i % modos.length],
                    aeros[i % aeros.length],
                    presiones[i % presiones.length],
                    combustibles[i % combustibles.length]));
        }
    }

    private MotorSimulacion crearMotor(Circuit circuito) {
        Color[] colores = {
                Color.web("#1E5BC6"), Color.web("#00D7B6"), Color.web("#E8002D"), Color.web("#FF8700"),
                Color.web("#229971"), Color.web("#2293D1"), Color.web("#9B2226"), Color.web("#B6BABD"),
                Color.web("#2B4562"), Color.web("#37BEDD")
        };

        MotorSimulacion motorTemporal = new MotorSimulacion(circuito, new ArrayList<>(), Clima.aleatorio(),
                circuito.getVueltas());

        List<EstadoVehiculo> vehiculos = new ArrayList<>();
        for (int idVehiculo = 1; idVehiculo <= 10; idVehiculo++) {
            int idPilotoLider = 2 * idVehiculo - 1; // ids 1,3,5,...,19 = pilotos "Lider" de cada equipo
            Pilot piloto = pilotManager.buscarPorId(idPilotoLider);
            Vehicle vehiculo = vehicleManager.buscarPorId(idVehiculo);
            ConfiguracionVehiculo configuracion = configManager.obtener(idVehiculo);

            Participante participante = new Participante(piloto, vehiculo, configuracion);
            EstadoVehiculo estado = new EstadoVehiculo(participante, piloto.getEquipo(),
                    colores[idVehiculo - 1], idVehiculo - 1, motorTemporal.sortearVariacionCarrera());
            vehiculos.add(estado);
        }

        return new MotorSimulacion(circuito, vehiculos, motorTemporal.getClima(), circuito.getVueltas());
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

                if (corriendo) {
                    motor.actualizar(dtSeg);
                }
                refrescarVistas();

                if (motor.isFinalizado()) {
                    corriendo = false;
                    panelControles.actualizarEstadoBoton(false, motor.isPausado(), true);
                }
            }
        };
        timer.start();
    }

    private void refrescarVistas() {
        List<EstadoVehiculo> ordenados = motor.obtenerClasificacionOrdenada();
        circuitoView.dibujar(motor.getCircuito(), motor.getVehiculos(), motor.isFinalizado(), motor.getGanador());
        clasificacionView.actualizar(ordenados, motor.getVueltaObjetivo());
        if (!ordenados.isEmpty()) {
            panelTelemetria.actualizarLider(ordenados.get(0), motor.getVueltaObjetivo());
        }
    }

    private void alternarIniciarPausar() {
        if (motor.isFinalizado()) {
            return;
        }
        if (!corriendo) {
            corriendo = true;
            motor.setPausado(false);
        } else {
            motor.setPausado(!motor.isPausado());
        }
        panelControles.actualizarEstadoBoton(corriendo, motor.isPausado(), motor.isFinalizado());
    }

    private void reiniciarCarrera() {
        motor.reiniciar();
        corriendo = false;
        ultimoFrameNs = -1;
        panelControles.actualizarEstadoBoton(false, false, false);
        refrescarVistas();
    }

    private void cambiarVelocidad(double multiplicador) {
        motor.setMultiplicadorVelocidad(multiplicador);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
