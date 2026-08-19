package com.formula1.circuitfactories;

import com.formula1.circuits.Circuit;
import com.formula1.factories.CircuitFactory;

/**
 * Crea los 7 circuitos definidos en el documento de requerimientos
 * (f1project.md), con su record de vuelta historico.
 */
public class F1CircuitFactory implements CircuitFactory {

    @Override
    public Circuit createCircuit(int id_circuito) {
        return switch (id_circuito) {
            case 1 -> new Circuit(1, "Circuito de Monaco", "Monaco", 3.34, (byte) 78,
                    "Uno de los circuitos mas prestigiosos y dificiles del calendario, conocido por sus "
                            + "calles angostas y la falta de zonas de adelantamiento.",
                    "1:10.166", "Lewis Hamilton", 2019);
            case 2 -> new Circuit(2, "Silverstone", "Reino Unido", 5.89, (byte) 52,
                    "Uno de los circuitos mas rapidos del calendario, con curvas de alta velocidad "
                            + "como Maggotts y Becketts.",
                    "1:27.097", "Max Verstappen", 2020);
            case 3 -> new Circuit(3, "Circuito de Spa-Francorchamps", "Belgica", 7.00, (byte) 44,
                    "Famoso por la curva Eau Rouge y la larga recta de Kemmel, un circuito donde la "
                            + "potencia del motor es clave.",
                    "1:46.286", "Valtteri Bottas", 2018);
            case 4 -> new Circuit(4, "Circuito de Monza", "Italia", 5.79, (byte) 53,
                    "Conocido como 'El Templo de la Velocidad', Monza es el circuito mas rapido del "
                            + "calendario con largas rectas y chicanes iconicas.",
                    "1:21.046", "Rubens Barrichello", 2004);
            case 5 -> new Circuit(5, "Interlagos", "Brasil", 4.31, (byte) 71,
                    "Interlagos es un circuito legendario con cambios de elevacion y un trazado tecnico "
                            + "que ha sido sede de algunas de las carreras mas emocionantes de la historia.",
                    "1:10.540", "Valtteri Bottas", 2018);
            case 6 -> new Circuit(6, "Circuito de Yas Marina", "Emiratos Arabes Unidos", 5.28, (byte) 58,
                    "Ubicado en Abu Dhabi, es famoso por ser el circuito donde se definen muchos "
                            + "campeonatos, con un diseno moderno y una espectacular carrera nocturna.",
                    "1:39.283", "Lewis Hamilton", 2019);
            case 7 -> new Circuit(7, "Circuito de Suzuka", "Japon", 5.81, (byte) 53,
                    "Un circuito desafiante con un diseno en forma de ocho, famoso por sus curvas de alta "
                            + "velocidad como 130R y la 'S' de Senna.",
                    "1:30.983", "Lewis Hamilton", 2019);
            default -> null;
        };
    }
}
