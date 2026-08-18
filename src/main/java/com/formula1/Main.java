package com.formula1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
import com.formula1.vehicles.ModoConduccion;
import com.formula1.vehiclefactories.MercedesFactory;
import com.formula1.vehiclefactories.RedBullFactory;
import com.formula1.vehicles.Vehicle;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static final CircuitManager circuitManager = new CircuitManager();
    private static final PilotManager pilotManager = new PilotManager();
    private static final TeamManager teamManager = new TeamManager();
    private static final VehicleManager vehicleManager = new VehicleManager();
    private static final ConfiguracionManager configManager = new ConfiguracionManager();
    private static final HistorialClasificacion historial = new HistorialClasificacion();
    private static final List<Result> recordsHistoricos = new ArrayList<>();

    public static void main(String[] args) {
        cargarDatosIniciales();
        System.out.println("=== Simulacion de Formula 1 ===");
        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Selecciona una opcion: ");
            switch (opcion) {
                case 1: menuCircuitos(); break;
                case 2: menuPilotos(); break;
                case 3: menuEquipos(); break;
                case 4: menuVehiculos(); break;
                case 5: menuConfiguracion(); break;
                case 6: menuSimulacion(); break;
                case 7: menuHistorial(); break;
                case 8: menuRecords(); break;
                case 0: salir = true; System.out.println("Hasta la proxima carrera!"); break;
                default: System.out.println("Opcion invalida.");
            }
        }
        sc.close();
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

    // MENU PRINCIPAL

    private static void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println("1. Gestionar circuitos");
        System.out.println("2. Gestionar pilotos");
        System.out.println("3. Gestionar equipos");
        System.out.println("4. Gestionar vehiculos");
        System.out.println("5. Configurar un vehiculo");
        System.out.println("6. Simular clasificacion");
        System.out.println("7. Ver historial de clasificaciones");
        System.out.println("8. Ver records de vuelta por circuito");
        System.out.println("0. Salir");
        System.out.println("------------------------------------------");
    }

    // ---------------------------------------------------------------
    // CIRCUITOS
    // ---------------------------------------------------------------
    private static void menuCircuitos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n-- Gestion de circuitos --");
            System.out.println("1. Listar  2. Agregar  3. Editar  4. Eliminar  5. Buscar  0. Volver");
            int op = leerEntero("Opcion: ");
            switch (op) {
                case 1:
                    for (Circuit c : circuitManager.listar()) System.out.println(c);
                    break;
                case 2: {
                    System.out.print("Nombre: "); String nombre = sc.nextLine();
                    System.out.print("Pais: "); String pais = sc.nextLine();
                    double km = leerDouble("Longitud (km): ");
                    byte vueltas = (byte) leerEntero("Vueltas: ");
                    System.out.print("Descripcion: "); String desc = sc.nextLine();
                    Circuit nuevo = new Circuit(0, nombre, pais, km, vueltas, desc, "-", "-", 0);
                    circuitManager.agregar(nuevo);
                    System.out.println("Circuito agregado con id " + nuevo.getID_circuito());
                    break;
                }
                case 3: {
                    int id = leerEntero("Id del circuito a editar: ");
                    Circuit existente = circuitManager.buscarPorId(id);
                    if (existente == null) { System.out.println("No existe."); break; }
                    System.out.print("Nuevo nombre (" + existente.getNombre() + "): "); String nombre = sc.nextLine();
                    System.out.print("Nuevo pais (" + existente.getPais() + "): "); String pais = sc.nextLine();
                    double km = leerDouble("Nueva longitud km (" + existente.getLongitud_km() + "): ");
                    byte vueltas = (byte) leerEntero("Nuevas vueltas (" + existente.getVueltas() + "): ");
                    Circuit editado = new Circuit(id, nombre.isBlank() ? existente.getNombre() : nombre,
                            pais.isBlank() ? existente.getPais() : pais, km > 0 ? km : existente.getLongitud_km(),
                            vueltas > 0 ? vueltas : existente.getVueltas(), existente.getDescripcion(),
                            existente.getRecordVueltaTiempo(), existente.getRecordVueltaPiloto(), existente.getRecordVueltaAnio());
                    circuitManager.editar(id, editado);
                    System.out.println("Circuito actualizado.");
                    break;
                }
                case 4: {
                    int id = leerEntero("Id del circuito a eliminar: ");
                    System.out.println(circuitManager.eliminar(id) ? "Eliminado." : "No existe.");
                    break;
                }
                case 5: {
                    System.out.print("Buscar por nombre o pais: "); String texto = sc.nextLine();
                    circuitManager.buscarPorNombreOPais(texto).forEach(System.out::println);
                    break;
                }
                case 0: volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
        }
    }

    // ---------------------------------------------------------------
    // PILOTOS
    // ---------------------------------------------------------------
    private static void menuPilotos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n-- Gestion de pilotos --");
            System.out.println("1. Listar  2. Agregar  3. Editar  4. Eliminar  5. Buscar  0. Volver");
            int op = leerEntero("Opcion: ");
            switch (op) {
                case 1:
                    for (Pilot p : pilotManager.listar()) System.out.println(p);
                    break;
                case 2: {
                    System.out.print("Nombre: "); String nombre = sc.nextLine();
                    System.out.print("Rol (Lider/Escudero): "); String rol = sc.nextLine();
                    System.out.print("Equipo: "); String equipo = sc.nextLine();
                    int idEquipo = leerEntero("Id de equipo: ");
                    int habilidad = leerEntero("Habilidad (1-100): ");
                    Pilot nuevo = new Pilot(0, nombre, rol, equipo, idEquipo, habilidad);
                    pilotManager.agregar(nuevo);
                    System.out.println("Piloto agregado con id " + nuevo.getID_pilot());
                    break;
                }
                case 3: {
                    int id = leerEntero("Id del piloto a editar: ");
                    Pilot existente = pilotManager.buscarPorId(id);
                    if (existente == null) { System.out.println("No existe."); break; }
                    System.out.print("Nuevo rol (" + existente.getRol() + "): "); String rol = sc.nextLine();
                    int habilidad = leerEntero("Nueva habilidad (" + existente.getHabilidad() + "): ");
                    Pilot editado = new Pilot(id, existente.getNombre(), rol.isBlank() ? existente.getRol() : rol,
                            existente.getEquipo(), existente.getIdEquipo(), habilidad > 0 ? habilidad : existente.getHabilidad());
                    pilotManager.editar(id, editado);
                    System.out.println("Piloto actualizado.");
                    break;
                }
                case 4: {
                    int id = leerEntero("Id del piloto a eliminar: ");
                    System.out.println(pilotManager.eliminar(id) ? "Eliminado." : "No existe.");
                    break;
                }
                case 5: {
                    System.out.print("Buscar por nombre o equipo: "); String texto = sc.nextLine();
                    pilotManager.buscarPorNombreOEquipo(texto).forEach(System.out::println);
                    break;
                }
                case 0: volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
        }
    }

    // ---------------------------------------------------------------
    // EQUIPOS
    // ---------------------------------------------------------------
    private static void menuEquipos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n-- Gestion de equipos --");
            System.out.println("1. Listar  2. Agregar  3. Editar  4. Eliminar  5. Buscar  0. Volver");
            int op = leerEntero("Opcion: ");
            switch (op) {
                case 1:
                    for (Team t : teamManager.listar()) System.out.println(t);
                    break;
                case 2: {
                    System.out.print("Nombre: "); String nombre = sc.nextLine();
                    System.out.print("Pais: "); String pais = sc.nextLine();
                    System.out.print("Motor: "); String motor = sc.nextLine();
                    Team nuevo = new Team(0, nombre, pais, motor, new ArrayList<>());
                    teamManager.agregar(nuevo);
                    System.out.println("Equipo agregado con id " + nuevo.getID_equipo());
                    break;
                }
                case 3: {
                    int id = leerEntero("Id del equipo a editar: ");
                    Team existente = teamManager.buscarPorId(id);
                    if (existente == null) { System.out.println("No existe."); break; }
                    System.out.print("Nuevo motor (" + existente.getMotor() + "): "); String motor = sc.nextLine();
                    Team editado = new Team(id, existente.getNombre(), existente.getPais(),
                            motor.isBlank() ? existente.getMotor() : motor, existente.getIdsPilotos());
                    teamManager.editar(id, editado);
                    System.out.println("Equipo actualizado.");
                    break;
                }
                case 4: {
                    int id = leerEntero("Id del equipo a eliminar: ");
                    System.out.println(teamManager.eliminar(id) ? "Eliminado." : "No existe.");
                    break;
                }
                case 5: {
                    System.out.print("Buscar por nombre o pais: "); String texto = sc.nextLine();
                    teamManager.buscarPorNombreOPais(texto).forEach(System.out::println);
                    break;
                }
                case 0: volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
        }
    }

    // ---------------------------------------------------------------
    // VEHICULOS
    // ---------------------------------------------------------------
    private static void menuVehiculos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n-- Gestion de vehiculos --");
            System.out.println("1. Listar  2. Agregar  3. Editar  4. Eliminar  5. Buscar  6. Asignar piloto  7. Comparar  0. Volver");
            int op = leerEntero("Opcion: ");
            switch (op) {
                case 1:
                    for (Vehicle v : vehicleManager.listar()) System.out.println(v);
                    break;
                case 2: {
                    System.out.print("Motor: "); String motor = sc.nextLine();
                    System.out.print("Modelo: "); String modelo = sc.nextLine();
                    double acel = leerDouble("Aceleracion 0-100 (s): ");
                    int velMax = leerEntero("Velocidad maxima (km/h): ");
                    System.out.print("Equipo: "); String equipo = sc.nextLine();
                    Vehicle nuevo = new Vehicle(0, motor, modelo, acel, velMax);
                    nuevo.setEquipo(equipo);
                    vehicleManager.agregar(nuevo);
                    System.out.println("Vehiculo agregado con id " + nuevo.getId_vehiculo()
                            + " (perfiles de rendimiento generados automaticamente).");
                    break;
                }
                case 3: {
                    int id = leerEntero("Id del vehiculo a editar: ");
                    Vehicle existente = vehicleManager.buscarPorId(id);
                    if (existente == null) { System.out.println("No existe."); break; }
                    int velMax = leerEntero("Nueva velocidad maxima (" + existente.getVelocidad_maxima() + "): ");
                    double acel = leerDouble("Nueva aceleracion (" + existente.getAceleracion() + "): ");
                    if (velMax > 0) existente.setVelocidad_maxima(velMax);
                    if (acel > 0) existente.setAceleracion(acel);
                    vehicleManager.editar(id, existente);
                    System.out.println("Vehiculo actualizado.");
                    break;
                }
                case 4: {
                    int id = leerEntero("Id del vehiculo a eliminar: ");
                    System.out.println(vehicleManager.eliminar(id) ? "Eliminado." : "No existe.");
                    break;
                }
                case 5: {
                    System.out.print("Buscar por modelo o equipo: "); String texto = sc.nextLine();
                    vehicleManager.buscarPorEquipoOModelo(texto).forEach(System.out::println);
                    break;
                }
                case 6: {
                    int idVehiculo = leerEntero("Id del vehiculo: ");
                    int idPiloto = leerEntero("Id del piloto a asignar: ");
                    System.out.println(vehicleManager.asignarPiloto(idVehiculo, idPiloto) ? "Piloto asignado." : "Vehiculo no encontrado.");
                    break;
                }
                case 7: {
                    System.out.print("Ids de vehiculos a comparar, separados por coma: ");
                    String linea = sc.nextLine();
                    List<Integer> ids = new ArrayList<>();
                    for (String s : linea.split(",")) {
                        try { ids.add(Integer.parseInt(s.trim())); } catch (NumberFormatException ignored) {}
                    }
                    System.out.println(vehicleManager.comparar(ids));
                    break;
                }
                case 0: volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
        }
    }

    // ---------------------------------------------------------------
    // CONFIGURACION DEL VEHICULO
    // ---------------------------------------------------------------
    private static void menuConfiguracion() {
        int idVehiculo = leerEntero("Id del vehiculo a configurar: ");
        Vehicle v = vehicleManager.buscarPorId(idVehiculo);
        if (v == null) { System.out.println("No existe ese vehiculo."); return; }

        ConfiguracionVehiculo actual = configManager.obtener(idVehiculo);
        System.out.println("Configuracion actual -> " + actual);

        System.out.println("Modo de conduccion: 1) NORMAL  2) AGRESIVA  3) AHORRO_COMBUSTIBLE");
        ModoConduccion modo = switch (leerEntero("Opcion: ")) {
            case 2 -> ModoConduccion.AGRESIVA;
            case 3 -> ModoConduccion.AHORRO_COMBUSTIBLE;
            default -> ModoConduccion.NORMAL;
        };

        System.out.println("Carga aerodinamica: 1) BAJA  2) MEDIA  3) ALTA");
        CargaAerodinamica aero = switch (leerEntero("Opcion: ")) {
            case 1 -> CargaAerodinamica.BAJA;
            case 3 -> CargaAerodinamica.ALTA;
            default -> CargaAerodinamica.MEDIA;
        };

        System.out.println("Presion de neumaticos: 1) BAJA  2) ESTANDAR  3) ALTA");
        PresionNeumaticos presion = switch (leerEntero("Opcion: ")) {
            case 1 -> PresionNeumaticos.BAJA;
            case 3 -> PresionNeumaticos.ALTA;
            default -> PresionNeumaticos.ESTANDAR;
        };

        System.out.println("Estrategia de combustible: 1) AGRESIVA  2) BALANCEADA  3) AHORRO");
        EstrategiaCombustible combustible = switch (leerEntero("Opcion: ")) {
            case 1 -> EstrategiaCombustible.AGRESIVA;
            case 3 -> EstrategiaCombustible.AHORRO;
            default -> EstrategiaCombustible.BALANCEADA;
        };

        ConfiguracionVehiculo nueva = new ConfiguracionVehiculo(idVehiculo, modo, aero, presion, combustible);
        configManager.guardar(nueva);
        System.out.println("Configuracion guardada -> " + nueva);
    }

    // ---------------------------------------------------------------
    // SIMULACION DE CLASIFICACION
    // ---------------------------------------------------------------
    private static void menuSimulacion() {
        for (Circuit c : circuitManager.listar()) {
            System.out.println(c.getID_circuito() + ". " + c.getNombre());
        }
        int idCircuito = leerEntero("Elige el id del circuito para la clasificacion: ");
        Circuit circuito = circuitManager.buscarPorId(idCircuito);
        if (circuito == null) { System.out.println("No existe ese circuito."); return; }

        Clima clima = Clima.aleatorio();
        System.out.println("Clima de la sesion: " + clima);

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
            System.out.println("No hay pilotos con vehiculo asignado. Asigna pilotos a un vehiculo primero (menu 4, opcion 6).");
            return;
        }

        SimuladorClasificacion simulador = new SimuladorClasificacion();
        List<ResultadoClasificacion> resultados = simulador.simular(circuito, participantes, clima);

        System.out.println("\n-- Resultado de clasificacion en " + circuito.getNombre() + " (" + clima + ") --");
        for (ResultadoClasificacion r : resultados) {
            System.out.println(r);
        }

        historial.guardar(circuito.getNombre(), clima, resultados);
        System.out.println("(Sesion guardada en el historial)");
    }

    private static void menuHistorial() {
        List<SesionClasificacion> sesiones = historial.listar();
        if (sesiones.isEmpty()) { System.out.println("Aun no hay sesiones simuladas."); return; }
        for (SesionClasificacion s : sesiones) {
            System.out.println("\nSesion #" + s.getIdSesion() + " - " + s.getCircuito() + " - Clima: " + s.getClima());
            for (ResultadoClasificacion r : s.getResultados()) {
                System.out.println("  " + r);
            }
        }
    }

    private static void menuRecords() {
        System.out.println("\n-- Records de vuelta por circuito --");
        for (Result r : recordsHistoricos) {
            System.out.println(r);
        }
    }

    // ---------------------------------------------------------------
    // Utilidades de lectura
    // ---------------------------------------------------------------
    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.print("Ingresa un numero valido: ");
        }
        int valor = sc.nextInt();
        sc.nextLine();
        return valor;
    }

    private static double leerDouble(String mensaje) {
        System.out.print(mensaje);
        while (!sc.hasNextDouble()) {
            sc.next();
            System.out.print("Ingresa un numero valido: ");
        }
        double valor = sc.nextDouble();
        sc.nextLine();
        return valor;
    }
}