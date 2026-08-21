package com.formula1.pilotfactories;

import com.formula1.factories.PilotFactory;
import com.formula1.pilots.Pilot;

/**
 * Crea los 20 pilotos definidos en el documento de requerimientos
 * (f1project.md), con su equipo, rol y una habilidad estimada (1-100).
 * idEquipo sigue el orden en que aparecen los equipos en el documento:
 * 1 Red Bull, 2 Mercedes, 3 Ferrari, 4 McLaren, 5 Aston Martin,
 * 6 Alpine, 7 Alfa Romeo, 8 Haas, 9 AlphaTauri, 10 Williams.
 */
public class F1PilotFactory implements PilotFactory {

    private static final Object[][] DATOS = {
            // nombre, equipo, rol, idEquipo, habilidad
            {"Max Verstappen", "Red Bull Racing", "Lider", 1, 98},
            {"Sergio Perez", "Red Bull Racing", "Escudero", 1, 88},
            {"Lewis Hamilton", "Mercedes-AMG Petronas", "Lider", 2, 95},
            {"George Russell", "Mercedes-AMG Petronas", "Escudero", 2, 89},
            {"Charles Leclerc", "Ferrari", "Lider", 3, 93},
            {"Carlos Sainz", "Ferrari", "Escudero", 3, 90},
            {"Lando Norris", "McLaren", "Lider", 4, 92},
            {"Oscar Piastri", "McLaren", "Escudero", 4, 87},
            {"Fernando Alonso", "Aston Martin", "Lider", 5, 94},
            {"Lance Stroll", "Aston Martin", "Escudero", 5, 78},
            {"Esteban Ocon", "Alpine", "Lider", 6, 85},
            {"Pierre Gasly", "Alpine", "Escudero", 6, 86},
            {"Valtteri Bottas", "Alfa Romeo", "Lider", 7, 84},
            {"Zhou Guanyu", "Alfa Romeo", "Escudero", 7, 76},
            {"Kevin Magnussen", "Haas", "Lider", 8, 79},
            {"Nico Hulkenberg", "Haas", "Escudero", 8, 81},
            {"Yuki Tsunoda", "AlphaTauri", "Lider", 9, 82},
            {"Daniel Ricciardo", "AlphaTauri", "Escudero", 9, 83},
            {"Alexander Albon", "Williams", "Lider", 10, 85},
            {"Logan Sargeant", "Williams", "Escudero", 10, 72}
    };

    @Override
    public Pilot createPilot(int id_piloto) {
        if (id_piloto < 1 || id_piloto > DATOS.length) {
            return null;
        }
        Object[] fila = DATOS[id_piloto - 1];
        String nombre = (String) fila[0];
        String equipo = (String) fila[1];
        String rol = (String) fila[2];
        int idEquipo = (int) fila[3];
        int habilidad = (int) fila[4];
        return new Pilot(id_piloto, nombre, rol, equipo, idEquipo, habilidad);
    }
}
