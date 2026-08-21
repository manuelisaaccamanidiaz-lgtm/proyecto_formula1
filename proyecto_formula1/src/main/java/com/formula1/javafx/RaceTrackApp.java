package com.formula1.javafx;

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
import com.formula1.pilotfactories.F1PilotFactory;
import com.formula1.simulation.Clima;
import com.formula1.simulation.ConfiguracionCarrera;
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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de arranque rapido de la vista JavaFX en tiempo real (sin pasar por
 * el menu principal): carga los datos reales del proyecto y arranca
 * directamente una carrera en Silverstone con clima aleatorio, usando la
 * vista reutilizable {@link VistaCarreraEnVivo}.
 *
 * El punto de entrada recomendado para usar TODAS las opciones del
 * enunciado (menu con botones + elegir pista/clima/combustible antes de
 * correr) es {@link MenuPrincipalApp}. Esta clase se conserva para poder
 * seguir compilando/ejecutando `mvn javafx:run` tal como estaba configurado
 * en el pom.xml original, y como referencia de "arranque minimo".
 */
public class RaceTrackApp extends Application {

    private final CircuitManager circuitManager = new CircuitManager();
    private final PilotManager pilotManager = new PilotManager();
    private final VehicleManager vehicleManager = new VehicleManager();
    private final ConfiguracionManager configManager = new ConfiguracionManager();

    private VistaCarreraEnVivo vistaCarrera;

    @Override
    public void start(Stage stage) {
        cargarDatosDelProyecto();
        Circuit circuito = circuitManager.buscarPorId(2); // Silverstone

        ConfiguracionCarrera configCarrera = new ConfiguracionCarrera(circuito, Clima.aleatorio(),
                EstrategiaCombustible.BALANCEADA, 100.0);
        vistaCarrera = new VistaCarreraEnVivo(pilotManager, vehicleManager, configManager, configCarrera);

        Scene scene = new Scene(vistaCarrera.construirVista(null), 1400, 760);
        stage.setTitle("F1 Simulación en tiempo real - " + circuito.getNombre());
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> vistaCarrera.detener());
        stage.show();

        vistaCarrera.iniciarBucle();
    }

    private void cargarDatosDelProyecto() {
        F1CircuitFactory circuitFactory = new F1CircuitFactory();
        for (int i = 1; i <= 10; i++) {
            circuitManager.agregar(circuitFactory.createCircuit(i));
        }

        F1PilotFactory pilotFactory = new F1PilotFactory();
        for (int i = 1; i <= 20; i++) {
            pilotManager.agregar(pilotFactory.createPilot(i));
        }

        VehicleFactory[] fabricas = {
                new RedBullFactory(), new MercedesFactory(), new FerrariFactory(), new McLarenFactory(),
                new AstonMartinFactory(), new AlpineFactory(), new AlfaRomeoFactory(), new HaasFactory(),
                new AlphaTauriFactory(), new WilliamsFactory()
        };
        for (int i = 0; i < fabricas.length; i++) {
            vehicleManager.agregar(fabricas[i].crearVehiculo(i + 1));
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
