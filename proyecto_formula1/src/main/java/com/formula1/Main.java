package com.formula1;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.formula1.circuitfactories.F1CircuitFactory;
import com.formula1.config.ConfiguracionManager;
import com.formula1.managers.CircuitManager;
import com.formula1.managers.PilotManager;
import com.formula1.managers.TeamManager;
import com.formula1.managers.VehicleManager;
import com.formula1.menu.OperacionesMenu;
import com.formula1.pilotfactories.F1PilotFactory;
import com.formula1.resultfactories.F1ResultFactory;
import com.formula1.results.Result;
import com.formula1.simulation.HistorialClasificacion;
import com.formula1.teamfactories.FerrariTeamFactory;
import com.formula1.teamfactories.MercedesTeamFactory;
import com.formula1.teamfactories.RedBullTeamFactory;
import com.formula1.teams.Team;
import com.formula1.vehiclefactories.MercedesFactory;
import com.formula1.vehiclefactories.RedBullFactory;
import com.formula1.vehicles.Vehicle;

/**
 * Aplicacion de consola (JOptionPane) original del proyecto. La logica de
 * cada opcion del menu vive ahora en {@link OperacionesMenu} (se extrajo
 * para poder reutilizarla tambien desde el menu con botones JavaFX,
 * {@code com.formula1.javafx.MenuPrincipalApp}); esta clase solo arma el
 * menu principal y delega.
 */
public class Main {

    private static final String TITULO = "Simulacion de Formula 1";

    private static final CircuitManager circuitManager = new CircuitManager();
    private static final PilotManager pilotManager = new PilotManager();
    private static final TeamManager teamManager = new TeamManager();
    private static final VehicleManager vehicleManager = new VehicleManager();
    private static final ConfiguracionManager configManager = new ConfiguracionManager();
    private static final HistorialClasificacion historial = new HistorialClasificacion();
    private static final List<Result> recordsHistoricos = new ArrayList<>();

    private static final OperacionesMenu operaciones = new OperacionesMenu(circuitManager, pilotManager,
            teamManager, vehicleManager, configManager, historial, recordsHistoricos);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        cargarDatosIniciales();
        boolean salir = false;
        while (!salir) {
            int opcion = leerEntero(menuPrincipalTexto());
            switch (opcion) {
                case 1: operaciones.menuCircuitos(); break;
                case 2: operaciones.menuPilotos(); break;
                case 3: operaciones.menuEquipos(); break;
                case 4: operaciones.menuVehiculos(); break;
                case 5: operaciones.menuConfiguracion(); break;
                case 6: operaciones.menuSimulacion(); break;
                case 7: operaciones.menuHistorial(); break;
                case 8: operaciones.menuRecords(); break;
                case 0: salir = true; mostrar("Hasta la proxima carrera!"); break;
                default: mostrarError("Opcion invalida.");
            }
        }
    }

    private static String menuPrincipalTexto() {
        return "=== Simulacion de Formula 1 ===\n\n" +
                "1. Gestionar circuitos\n" +
                "2. Gestionar pilotos\n" +
                "3. Gestionar equipos\n" +
                "4. Gestionar vehiculos\n" +
                "5. Configurar un vehiculo\n" +
                "6. Simular clasificacion\n" +
                "7. Ver historial de clasificaciones\n" +
                "8. Ver records de vuelta por circuito\n" +
                "0. Salir\n\n" +
                "Selecciona una opcion:";
    }

    private static void cargarDatosIniciales() {
        F1CircuitFactory circuitFactory = new F1CircuitFactory();
        for (int i = 1; i <= 10; i++) {
            circuitManager.agregar(circuitFactory.createCircuit(i));
        }

        F1PilotFactory pilotFactory = new F1PilotFactory();
        for (int i = 1; i <= 20; i++) {
            pilotManager.agregar(pilotFactory.createPilot(i));
        }

        Team redBull = new RedBullTeamFactory().createtTeam(1);
        Team mercedes = new MercedesTeamFactory().createtTeam(2);
        Team ferrari = new FerrariTeamFactory().createtTeam(3);
        teamManager.agregar(redBull);
        teamManager.agregar(mercedes);
        teamManager.agregar(ferrari);

        Vehicle rb20 = new RedBullFactory().crearVehiculo(1);
        Vehicle w15 = new MercedesFactory().crearVehiculo(2);
        vehicleManager.agregar(rb20);
        vehicleManager.agregar(w15);

        F1ResultFactory resultFactory = new F1ResultFactory();
        for (int i = 1; i <= 7; i++) {
            recordsHistoricos.add(resultFactory.createResult(i));
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, mensaje, TITULO, JOptionPane.QUESTION_MESSAGE);
            if (input == null || input.isBlank()) return 0;
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                mostrarError("Ingresa un numero entero valido.");
            }
        }
    }

    private static void mostrar(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, TITULO, JOptionPane.INFORMATION_MESSAGE);
    }

    private static void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, TITULO, JOptionPane.ERROR_MESSAGE);
    }
}
