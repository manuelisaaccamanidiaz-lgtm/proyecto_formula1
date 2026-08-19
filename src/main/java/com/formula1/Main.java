package com.formula1;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.formula1.circuitfactories.F1CircuitFactory;
import com.formula1.circuits.Circuit;
import com.formula1.config.CargaAerodinamica;
import com.formula1.config.ConfiguracionManager;
import com.formula1.config.ConfiguracionVehiculo;
import com.formula1.config.EstrategiaCombustible;
import com.formula1.config.PresionNeumaticos;
import com.formula1.managers.CircuitManager;
import com.formula1.managers.PilotManager;
import com.formula1.managers.TeamManager;
import com.formula1.managers.VehicleManager;
import com.formula1.pilotfactories.F1PilotFactory;
import com.formula1.pilots.Pilot;
import com.formula1.resultfactories.F1ResultFactory;
import com.formula1.results.Result;
import com.formula1.simulation.Clima;
import com.formula1.simulation.HistorialClasificacion;
import com.formula1.simulation.Participante;
import com.formula1.simulation.ResultadoClasificacion;
import com.formula1.simulation.SesionClasificacion;
import com.formula1.simulation.SimuladorClasificacion;
import com.formula1.teamfactories.FerrariTeamFactory;
import com.formula1.teamfactories.MercedesTeamFactory;
import com.formula1.teamfactories.RedBullTeamFactory;
import com.formula1.teams.Team;
import com.formula1.vehiclefactories.MercedesFactory;
import com.formula1.vehiclefactories.RedBullFactory;
import com.formula1.vehicles.ModoConduccion;
import com.formula1.vehicles.Vehicle;

public class Main {

    private static final String TITULO = "Simulacion de Formula 1";

    private static final CircuitManager circuitManager = new CircuitManager();
    private static final PilotManager pilotManager = new PilotManager();
    private static final TeamManager teamManager = new TeamManager();
    private static final VehicleManager vehicleManager = new VehicleManager();
    private static final ConfiguracionManager configManager = new ConfiguracionManager();
    private static final HistorialClasificacion historial = new HistorialClasificacion();
    private static final List<Result> recordsHistoricos = new ArrayList<>();

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
                case 1: menuCircuitos(); break;
                case 2: menuPilotos(); break;
                case 3: menuEquipos(); break;
                case 4: menuVehiculos(); break;
                case 5: menuConfiguracion(); break;
                case 6: menuSimulacion(); break;
                case 7: menuHistorial(); break;
                case 8: menuRecords(); break;
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
        for (int i = 1; i <= 7; i++) {
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

    // ---------------------------------------------------------------
    // CIRCUITOS
    // ---------------------------------------------------------------
    private static void menuCircuitos() {
        boolean volver = false;
        while (!volver) {
            int op = leerEntero("-- Gestion de circuitos --\n\n" +
                    "1. Listar\n2. Agregar\n3. Editar\n4. Eliminar\n5. Buscar\n0. Volver\n\nOpcion:");
            switch (op) {
                case 1:
                    mostrar(unirLineas(circuitManager.listar()));
                    break;
                case 2: {
                    String nombre = leerTexto("Nombre:");
                    String pais = leerTexto("Pais:");
                    double km = leerDouble("Longitud (km):");
                    byte vueltas = (byte) leerEntero("Vueltas:");
                    String desc = leerTexto("Descripcion:");
                    Circuit nuevo = new Circuit(0, nombre, pais, km, vueltas, desc, "-", "-", 0);
                    circuitManager.agregar(nuevo);
                    mostrar("Circuito agregado con id " + nuevo.getID_circuito());
                    break;
                }
                case 3: {
                    int id = leerEntero("Id del circuito a editar:");
                    Circuit existente = circuitManager.buscarPorId(id);
                    if (existente == null) { mostrarError("No existe."); break; }
                    String nombre = leerTexto("Nuevo nombre (" + existente.getNombre() + "):");
                    String pais = leerTexto("Nuevo pais (" + existente.getPais() + "):");
                    double km = leerDouble("Nueva longitud km (" + existente.getLongitud_km() + "):");
                    byte vueltas = (byte) leerEntero("Nuevas vueltas (" + existente.getVueltas() + "):");
                    Circuit editado = new Circuit(id, nombre.isBlank() ? existente.getNombre() : nombre,
                            pais.isBlank() ? existente.getPais() : pais, km > 0 ? km : existente.getLongitud_km(),
                            vueltas > 0 ? vueltas : existente.getVueltas(), existente.getDescripcion(),
                            existente.getRecordVueltaTiempo(), existente.getRecordVueltaPiloto(), existente.getRecordVueltaAnio());
                    circuitManager.editar(id, editado);
                    mostrar("Circuito actualizado.");
                    break;
                }
                case 4: {
                    int id = leerEntero("Id del circuito a eliminar:");
                    mostrar(circuitManager.eliminar(id) ? "Eliminado." : "No existe.");
                    break;
                }
                case 5: {
                    String texto = leerTexto("Buscar por nombre o pais:");
                    mostrar(unirLineas(circuitManager.buscarPorNombreOPais(texto)));
                    break;
                }
                case 0: volver = true; break;
                default: mostrarError("Opcion invalida.");
            }
        }
    }

    // ---------------------------------------------------------------
    // PILOTOS
    // ---------------------------------------------------------------
    private static void menuPilotos() {
        boolean volver = false;
        while (!volver) {
            int op = leerEntero("-- Gestion de pilotos --\n\n" +
                    "1. Listar\n2. Agregar\n3. Editar\n4. Eliminar\n5. Buscar\n0. Volver\n\nOpcion:");
            switch (op) {
                case 1:
                    mostrar(unirLineas(pilotManager.listar()));
                    break;
                case 2: {
                    String nombre = leerTexto("Nombre:");
                    String rol = leerTexto("Rol (Lider/Escudero):");
                    String equipo = leerTexto("Equipo:");
                    int idEquipo = leerEntero("Id de equipo:");
                    int habilidad = leerEntero("Habilidad (1-100):");
                    Pilot nuevo = new Pilot(0, nombre, rol, equipo, idEquipo, habilidad);
                    pilotManager.agregar(nuevo);
                    mostrar("Piloto agregado con id " + nuevo.getID_pilot());
                    break;
                }
                case 3: {
                    int id = leerEntero("Id del piloto a editar:");
                    Pilot existente = pilotManager.buscarPorId(id);
                    if (existente == null) { mostrarError("No existe."); break; }
                    String rol = leerTexto("Nuevo rol (" + existente.getRol() + "):");
                    int habilidad = leerEntero("Nueva habilidad (" + existente.getHabilidad() + "):");
                    Pilot editado = new Pilot(id, existente.getNombre(), rol.isBlank() ? existente.getRol() : rol,
                            existente.getEquipo(), existente.getIdEquipo(), habilidad > 0 ? habilidad : existente.getHabilidad());
                    pilotManager.editar(id, editado);
                    mostrar("Piloto actualizado.");
                    break;
                }
                case 4: {
                    int id = leerEntero("Id del piloto a eliminar:");
                    mostrar(pilotManager.eliminar(id) ? "Eliminado." : "No existe.");
                    break;
                }
                case 5: {
                    String texto = leerTexto("Buscar por nombre o equipo:");
                    mostrar(unirLineas(pilotManager.buscarPorNombreOEquipo(texto)));
                    break;
                }
                case 0: volver = true; break;
                default: mostrarError("Opcion invalida.");
            }
        }
    }

    // ---------------------------------------------------------------
    // EQUIPOS
    // ---------------------------------------------------------------
    private static void menuEquipos() {
        boolean volver = false;
        while (!volver) {
            int op = leerEntero("-- Gestion de equipos --\n\n" +
                    "1. Listar\n2. Agregar\n3. Editar\n4. Eliminar\n5. Buscar\n0. Volver\n\nOpcion:");
            switch (op) {
                case 1:
                    mostrar(unirLineas(teamManager.listar()));
                    break;
                case 2: {
                    String nombre = leerTexto("Nombre:");
                    String pais = leerTexto("Pais:");
                    String motor = leerTexto("Motor:");
                    Team nuevo = new Team(0, nombre, pais, motor, new ArrayList<>());
                    teamManager.agregar(nuevo);
                    mostrar("Equipo agregado con id " + nuevo.getID_equipo());
                    break;
                }
                case 3: {
                    int id = leerEntero("Id del equipo a editar:");
                    Team existente = teamManager.buscarPorId(id);
                    if (existente == null) { mostrarError("No existe."); break; }
                    String motor = leerTexto("Nuevo motor (" + existente.getMotor() + "):");
                    Team editado = new Team(id, existente.getNombre(), existente.getPais(),
                            motor.isBlank() ? existente.getMotor() : motor, existente.getIdsPilotos());
                    teamManager.editar(id, editado);
                    mostrar("Equipo actualizado.");
                    break;
                }
                case 4: {
                    int id = leerEntero("Id del equipo a eliminar:");
                    mostrar(teamManager.eliminar(id) ? "Eliminado." : "No existe.");
                    break;
                }
                case 5: {
                    String texto = leerTexto("Buscar por nombre o pais:");
                    mostrar(unirLineas(teamManager.buscarPorNombreOPais(texto)));
                    break;
                }
                case 0: volver = true; break;
                default: mostrarError("Opcion invalida.");
            }
        }
    }

    // ---------------------------------------------------------------
    // VEHICULOS
    // ---------------------------------------------------------------
    private static void menuVehiculos() {
        boolean volver = false;
        while (!volver) {
            int op = leerEntero("-- Gestion de vehiculos --\n\n" +
                    "1. Listar\n2. Agregar\n3. Editar\n4. Eliminar\n5. Buscar\n6. Asignar piloto\n7. Comparar\n0. Volver\n\nOpcion:");
            switch (op) {
                case 1:
                    mostrar(unirLineas(vehicleManager.listar()));
                    break;
                case 2: {
                    String motor = leerTexto("Motor:");
                    String modelo = leerTexto("Modelo:");
                    double acel = leerDouble("Aceleracion 0-100 (s):");
                    int velMax = leerEntero("Velocidad maxima (km/h):");
                    String equipo = leerTexto("Equipo:");
                    Vehicle nuevo = new Vehicle(0, motor, modelo, acel, velMax);
                    nuevo.setEquipo(equipo);
                    vehicleManager.agregar(nuevo);
                    mostrar("Vehiculo agregado con id " + nuevo.getId_vehiculo()
                            + " (perfiles de rendimiento generados automaticamente).");
                    break;
                }
                case 3: {
                    int id = leerEntero("Id del vehiculo a editar:");
                    Vehicle existente = vehicleManager.buscarPorId(id);
                    if (existente == null) { mostrarError("No existe."); break; }
                    int velMax = leerEntero("Nueva velocidad maxima (" + existente.getVelocidad_maxima() + "):");
                    double acel = leerDouble("Nueva aceleracion (" + existente.getAceleracion() + "):");
                    if (velMax > 0) existente.setVelocidad_maxima(velMax);
                    if (acel > 0) existente.setAceleracion(acel);
                    vehicleManager.editar(id, existente);
                    mostrar("Vehiculo actualizado.");
                    break;
                }
                case 4: {
                    int id = leerEntero("Id del vehiculo a eliminar:");
                    mostrar(vehicleManager.eliminar(id) ? "Eliminado." : "No existe.");
                    break;
                }
                case 5: {
                    String texto = leerTexto("Buscar por modelo o equipo:");
                    mostrar(unirLineas(vehicleManager.buscarPorEquipoOModelo(texto)));
                    break;
                }
                case 6: {
                    int idVehiculo = leerEntero("Id del vehiculo:");
                    int idPiloto = leerEntero("Id del piloto a asignar:");
                    mostrar(vehicleManager.asignarPiloto(idVehiculo, idPiloto) ? "Piloto asignado." : "Vehiculo no encontrado.");
                    break;
                }
                case 7: {
                    String linea = leerTexto("Ids de vehiculos a comparar, separados por coma:");
                    List<Integer> ids = new ArrayList<>();
                    for (String s : linea.split(",")) {
                        try { ids.add(Integer.parseInt(s.trim())); } catch (NumberFormatException ignored) {}
                    }
                    mostrar(String.valueOf(vehicleManager.comparar(ids)));
                    break;
                }
                case 0: volver = true; break;
                default: mostrarError("Opcion invalida.");
            }
        }
    }

    // ---------------------------------------------------------------
    // CONFIGURACION DEL VEHICULO
    // ---------------------------------------------------------------
    private static void menuConfiguracion() {
        int idVehiculo = leerEntero("Id del vehiculo a configurar:");
        Vehicle v = vehicleManager.buscarPorId(idVehiculo);
        if (v == null) { mostrarError("No existe ese vehiculo."); return; }

        ConfiguracionVehiculo actual = configManager.obtener(idVehiculo);

        ModoConduccion modo = switch (leerEntero("Configuracion actual -> " + actual +
                "\n\nModo de conduccion:\n1) NORMAL\n2) AGRESIVA\n3) AHORRO_COMBUSTIBLE\n\nOpcion:")) {
            case 2 -> ModoConduccion.AGRESIVA;
            case 3 -> ModoConduccion.AHORRO_COMBUSTIBLE;
            default -> ModoConduccion.NORMAL;
        };

        CargaAerodinamica aero = switch (leerEntero("Carga aerodinamica:\n1) BAJA\n2) MEDIA\n3) ALTA\n\nOpcion:")) {
            case 1 -> CargaAerodinamica.BAJA;
            case 3 -> CargaAerodinamica.ALTA;
            default -> CargaAerodinamica.MEDIA;
        };

        PresionNeumaticos presion = switch (leerEntero("Presion de neumaticos:\n1) BAJA\n2) ESTANDAR\n3) ALTA\n\nOpcion:")) {
            case 1 -> PresionNeumaticos.BAJA;
            case 3 -> PresionNeumaticos.ALTA;
            default -> PresionNeumaticos.ESTANDAR;
        };

        EstrategiaCombustible combustible = switch (leerEntero("Estrategia de combustible:\n1) AGRESIVA\n2) BALANCEADA\n3) AHORRO\n\nOpcion:")) {
            case 1 -> EstrategiaCombustible.AGRESIVA;
            case 3 -> EstrategiaCombustible.AHORRO;
            default -> EstrategiaCombustible.BALANCEADA;
        };

        ConfiguracionVehiculo nueva = new ConfiguracionVehiculo(idVehiculo, modo, aero, presion, combustible);
        configManager.guardar(nueva);
        mostrar("Configuracion guardada -> " + nueva);
    }

    // ---------------------------------------------------------------
    // SIMULACION DE CLASIFICACION
    // ---------------------------------------------------------------
    private static void menuSimulacion() {
        StringBuilder listaCircuitos = new StringBuilder("Circuitos disponibles:\n\n");
        for (Circuit c : circuitManager.listar()) {
            listaCircuitos.append(c.getID_circuito()).append(". ").append(c.getNombre()).append("\n");
        }
        int idCircuito = leerEntero(listaCircuitos + "\nElige el id del circuito para la clasificacion:");
        Circuit circuito = circuitManager.buscarPorId(idCircuito);
        if (circuito == null) { mostrarError("No existe ese circuito."); return; }

        Clima clima = Clima.aleatorio();

        List<Participante> participantes = new ArrayList<>();
        for (Vehicle v : vehicleManager.listar()) {
            ConfiguracionVehiculo config = configManager.obtener(v.getId_vehiculo());
            for (Integer idPiloto : v.getIdsPilotos()) {
                Pilot piloto = pilotManager.buscarPorId(idPiloto);
                if (piloto != null) {
                    participantes.add(new Participante(piloto, v, config));
                }
            }
        }

        if (participantes.isEmpty()) {
            mostrarError("No hay pilotos con vehiculo asignado. Asigna pilotos a un vehiculo primero (menu 4, opcion 6).");
            return;
        }

        SimuladorClasificacion simulador = new SimuladorClasificacion();
        List<ResultadoClasificacion> resultados = simulador.simular(circuito, participantes, clima);

        StringBuilder sb = new StringBuilder();
        sb.append("-- Resultado de clasificacion en ").append(circuito.getNombre())
                .append(" (").append(clima).append(") --\n\n");
        for (ResultadoClasificacion r : resultados) {
            sb.append(r).append("\n");
        }
        historial.guardar(circuito.getNombre(), clima, resultados);
        sb.append("\n(Sesion guardada en el historial)");
        mostrar(sb.toString());
    }

    private static void menuHistorial() {
        List<SesionClasificacion> sesiones = historial.listar();
        if (sesiones.isEmpty()) { mostrar("Aun no hay sesiones simuladas."); return; }
        StringBuilder sb = new StringBuilder();
        for (SesionClasificacion s : sesiones) {
            sb.append("\nSesion #").append(s.getIdSesion()).append(" - ").append(s.getCircuito())
                    .append(" - Clima: ").append(s.getClima()).append("\n");
            for (ResultadoClasificacion r : s.getResultados()) {
                sb.append("  ").append(r).append("\n");
            }
        }
        mostrar(sb.toString());
    }

    private static void menuRecords() {
        mostrar("-- Records de vuelta por circuito --\n\n" + unirLineas(recordsHistoricos));
    }

    // ---------------------------------------------------------------
    // Utilidades de lectura / muestra con JOptionPane
    // ---------------------------------------------------------------
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

    private static double leerDouble(String mensaje) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, mensaje, TITULO, JOptionPane.QUESTION_MESSAGE);
            if (input == null || input.isBlank()) return 0;
            try {
                return Double.parseDouble(input.trim().replace(",", "."));
            } catch (NumberFormatException e) {
                mostrarError("Ingresa un numero valido.");
            }
        }
    }

    private static String leerTexto(String mensaje) {
        String input = JOptionPane.showInputDialog(null, mensaje, TITULO, JOptionPane.QUESTION_MESSAGE);
        return input == null ? "" : input.trim();
    }

    private static void mostrar(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, TITULO, JOptionPane.INFORMATION_MESSAGE);
    }

    private static void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, TITULO, JOptionPane.ERROR_MESSAGE);
    }

    private static <T> String unirLineas(Iterable<T> items) {
        StringBuilder sb = new StringBuilder();
        for (T item : items) {
            sb.append(item).append("\n");
        }
        return sb.length() == 0 ? "No hay elementos registrados." : sb.toString();
    }
}