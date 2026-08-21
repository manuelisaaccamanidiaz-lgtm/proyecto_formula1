package com.formula1.javafx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.formula1.config.ConfiguracionManager;
import com.formula1.config.ConfiguracionVehiculo;
import com.formula1.managers.PilotManager;
import com.formula1.managers.VehicleManager;
import com.formula1.model.EstadoVehiculo;
import com.formula1.persistence.HistorialCarrerasJson;
import com.formula1.pilots.Pilot;
import com.formula1.simulation.ConfiguracionCarrera;
import com.formula1.simulation.MotorSimulacion;
import com.formula1.simulation.Participante;
import com.formula1.vehicles.Vehicle;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Construye y controla la vista de carrera en tiempo real (pista + autos +
 * clasificacion + telemetria + controles) a partir de una
 * {@link ConfiguracionCarrera} elegida por el usuario (pista, clima,
 * estrategia de combustible global y combustible inicial).
 *
 * Es la version reutilizable de lo que antes vivia directo dentro de
 * RaceTrackApp.start(): esta clase NO extiende Application, para poder
 * incrustarse dentro de {@link MenuPrincipalApp} (con un boton "Volver al
 * menu") o usarse de forma independiente desde {@link RaceTrackApp}.
 *
 * Ademas de dibujar la carrera, al terminar guarda el resultado (ganador +
 * tiempos de todos) en {@code historial_carreras.json} mediante
 * {@link HistorialCarrerasJson}.
 */
public class VistaCarreraEnVivo {

    private static final Color[] COLORES_EQUIPO = {
            Color.web("#1E5BC6"), Color.web("#00D7B6"), Color.web("#E8002D"), Color.web("#FF8700"),
            Color.web("#229971"), Color.web("#2293D1"), Color.web("#9B2226"), Color.web("#B6BABD"),
            Color.web("#2B4562"), Color.web("#37BEDD")
    };

    private final PilotManager pilotManager;
    private final VehicleManager vehicleManager;
    private final ConfiguracionManager configManager;
    private final ConfiguracionCarrera configCarrera;
    private final Random random = new Random();
    private final HistorialCarrerasJson historialJson = new HistorialCarrerasJson();

    private MotorSimulacion motor;
    private CircuitoView circuitoView;
    private final ClasificacionView clasificacionView = new ClasificacionView();
    private final PanelTelemetria panelTelemetria = new PanelTelemetria();
    private final PanelControles panelControles = new PanelControles();

    private AnimationTimer timer;
    private long ultimoFrameNs = -1;
    private boolean corriendo = false;
    private boolean carreraGuardada = false;

    public VistaCarreraEnVivo(PilotManager pilotManager, VehicleManager vehicleManager,
                               ConfiguracionManager configManager, ConfiguracionCarrera configCarrera) {
        this.pilotManager = pilotManager;
        this.vehicleManager = vehicleManager;
        this.configManager = configManager;
        this.configCarrera = configCarrera;
    }

    /** Construye el nodo raiz de la vista. Llamar a {@link #iniciarBucle()} despues de mostrarla en un Stage. */
    public BorderPane construirVista(Runnable onVolverAlMenu) {
        motor = crearMotor();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f141c;");

        Canvas canvas = new Canvas(960, 640);
        circuitoView = new CircuitoView(canvas);
        root.setCenter(canvas);

        VBox panelDerecho = new VBox(10);
        panelDerecho.getChildren().addAll(clasificacionView.construirPanel(), panelTelemetria.construirPanel(motor.getClima()));
        root.setRight(panelDerecho);

        HBox controles = panelControles.construirPanel(this::alternarIniciarPausar, this::reiniciarCarrera, this::cambiarVelocidad);
        if (onVolverAlMenu != null) {
            Button btnVolver = new Button("< Volver al menu");
            btnVolver.setStyle("-fx-background-color: #21262d; -fx-text-fill: #f0f6fc; -fx-cursor: hand;");
            btnVolver.setOnAction(e -> {
                detener();
                onVolverAlMenu.run();
            });
            controles.getChildren().add(0, btnVolver);
        }
        root.setBottom(controles);
        root.setTop(construirEncabezado());

        refrescarVistas();
        return root;
    }

    private VBox construirEncabezado() {
        VBox caja = new VBox(2);
        caja.setPadding(new Insets(10, 16, 8, 16));
        caja.setStyle("-fx-background-color: linear-gradient(to right, #d32026, #0f141c 85%);");

        Label titulo = new Label("F1 - " + configCarrera.getCircuito().getNombre()
                + " (" + configCarrera.getCircuito().getPais() + ")");
        titulo.setTextFill(Color.WHITE);
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));

        Label subtitulo = new Label(String.format(
                "Clima: %s   |   Combustible inicial: %.0f%%   |   Estrategia de combustible: %s   |   %d vueltas",
                configCarrera.getClima(), configCarrera.getCombustibleInicialPorcentaje(),
                configCarrera.getEstrategiaCombustibleGlobal(), configCarrera.getCircuito().getVueltas()));
        subtitulo.setTextFill(Color.web("#f5dde0"));
        subtitulo.setFont(Font.font("System", 12));

        caja.getChildren().addAll(titulo, subtitulo);
        return caja;
    }

    private MotorSimulacion crearMotor() {
        MotorSimulacion motorTemporal = new MotorSimulacion(configCarrera.getCircuito(), new ArrayList<>(),
                configCarrera.getClima(), configCarrera.getCircuito().getVueltas());

        List<EstadoVehiculo> vehiculos = new ArrayList<>();
        List<Vehicle> baseVehiculos = vehicleManager.listar();
        int cupo = Math.min(COLORES_EQUIPO.length, baseVehiculos.size());

        for (int i = 0; i < cupo; i++) {
            Vehicle base = baseVehiculos.get(i);
            Vehicle vehiculoDelDia = variarVehiculoParaCarrera(base);

            Pilot piloto = obtenerPilotoPrincipal(base);
            if (piloto == null) {
                continue;
            }

            ConfiguracionVehiculo configBase = configManager.obtener(base.getId_vehiculo());
            ConfiguracionVehiculo configDeCarrera = new ConfiguracionVehiculo(base.getId_vehiculo(),
                    configBase.getModo(), configBase.getCargaAerodinamica(), configBase.getPresionNeumaticos(),
                    configCarrera.getEstrategiaCombustibleGlobal());

            Participante participante = new Participante(piloto, vehiculoDelDia, configDeCarrera);
            EstadoVehiculo estado = new EstadoVehiculo(participante, piloto.getEquipo(),
                    COLORES_EQUIPO[i % COLORES_EQUIPO.length], i, motorTemporal.sortearVariacionCarrera(),
                    configCarrera.getCombustibleInicialPorcentaje());
            vehiculos.add(estado);
        }

        return new MotorSimulacion(configCarrera.getCircuito(), vehiculos, configCarrera.getClima(),
                configCarrera.getCircuito().getVueltas());
    }

    /** El piloto "Lider" del equipo (ids impares 1,3,5,...) o, si no existe, el primer piloto asignado al vehiculo. */
    private Pilot obtenerPilotoPrincipal(Vehicle base) {
        Pilot piloto = pilotManager.buscarPorId(2 * base.getId_vehiculo() - 1);
        if (piloto != null) {
            return piloto;
        }
        for (Integer idPiloto : base.getIdsPilotos()) {
            Pilot alterno = pilotManager.buscarPorId(idPiloto);
            if (alterno != null) {
                return alterno;
            }
        }
        return null;
    }

    /**
     * PROPUESTA (pedida explicitamente en el enunciado): genera un vehiculo
     * "del dia" con velocidad maxima y aceleracion ligeramente distintas a
     * las guardadas en VehicleManager (+/-6%), sorteadas de nuevo en cada
     * carrera. Esto hace que los DATOS del vehiculo varien carrera a
     * carrera (no solo un factor interno oculto), y que el ganador no sea
     * siempre el mismo, sin modificar el vehiculo original del catalogo.
     */
    private Vehicle variarVehiculoParaCarrera(Vehicle base) {
        double factor = 0.94 + random.nextDouble() * 0.12; // +/-6%
        int velocidadVariada = (int) Math.round(base.getVelocidad_maxima() * factor);
        double aceleracionVariada = Math.round((base.getAceleracion() / factor) * 100.0) / 100.0;

        Vehicle variado = new Vehicle(base.getId_vehiculo(), base.getMotor(), base.getModelo(),
                aceleracionVariada, velocidadVariada);
        variado.setEquipo(base.getEquipo());
        return variado;
    }

    public void iniciarBucle() {
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
                    guardarResultadoSiFalta();
                }
            }
        };
        timer.start();
    }

    private void guardarResultadoSiFalta() {
        if (carreraGuardada) {
            return;
        }
        carreraGuardada = true;
        historialJson.guardarCarrera(configCarrera.getCircuito(), configCarrera.getClima(),
                motor.obtenerClasificacionOrdenada());
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
        carreraGuardada = false;
        ultimoFrameNs = -1;
        panelControles.actualizarEstadoBoton(false, false, false);
        refrescarVistas();
    }

    private void cambiarVelocidad(double multiplicador) {
        motor.setMultiplicadorVelocidad(multiplicador);
    }

    /** Detiene el AnimationTimer (llamar siempre al salir de esta vista, para no seguir consumiendo CPU). */
    public void detener() {
        if (timer != null) {
            timer.stop();
        }
    }
}
