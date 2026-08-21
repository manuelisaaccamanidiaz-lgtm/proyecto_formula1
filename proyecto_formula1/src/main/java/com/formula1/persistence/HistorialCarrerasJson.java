package com.formula1.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.formula1.circuits.Circuit;
import com.formula1.model.EstadoVehiculo;
import com.formula1.simulation.Clima;

/**
 * Guarda cada carrera terminada (ganador y tiempos de todos los pilotos) en
 * un archivo JSON en disco ({@code historial_carreras.json}, en el
 * directorio desde el que se ejecuta la aplicacion), agregando una entrada
 * nueva por carrera sin borrar las anteriores.
 *
 * El proyecto no trae ninguna libreria de JSON en el pom.xml (solo
 * javafx-controls) y este entorno no tiene forma de descargar una nueva
 * dependencia desde Maven Central, asi que el JSON se arma "a mano" con
 * StringBuilder. El formato generado es JSON valido estandar (se puede
 * abrir con cualquier lector de JSON); como el archivo SIEMPRE se reescribe
 * con este mismo formato, agregar una carrera nueva solo implica insertar
 * un objeto mas antes del corchete de cierre del arreglo.
 */
public class HistorialCarrerasJson {

    private static final Path ARCHIVO = Paths.get("historial_carreras.json");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Agrega al archivo JSON el resultado de una carrera ya finalizada (clasificacionFinal ordenada, P1 primero). */
    public synchronized void guardarCarrera(Circuit circuito, Clima clima, List<EstadoVehiculo> clasificacionFinal) {
        agregarAlArray(construirObjetoCarrera(circuito, clima, clasificacionFinal));
    }

    private String construirObjetoCarrera(Circuit circuito, Clima clima, List<EstadoVehiculo> clasificacionFinal) {
        StringBuilder json = new StringBuilder();
        json.append("  {\n");
        json.append("    \"fecha\": \"").append(escapar(LocalDateTime.now().format(FORMATO_FECHA))).append("\",\n");
        json.append("    \"circuito\": \"").append(escapar(circuito.getNombre())).append("\",\n");
        json.append("    \"pais\": \"").append(escapar(circuito.getPais())).append("\",\n");
        json.append("    \"clima\": \"").append(clima.name()).append("\",\n");
        json.append("    \"vueltas\": ").append(circuito.getVueltas()).append(",\n");

        if (!clasificacionFinal.isEmpty()) {
            EstadoVehiculo ganador = clasificacionFinal.get(0);
            json.append("    \"ganador\": {\n");
            json.append("      \"piloto\": \"").append(escapar(ganador.getParticipante().getPiloto().getNombre())).append("\",\n");
            json.append("      \"equipo\": \"").append(escapar(ganador.getEquipo())).append("\",\n");
            json.append("      \"tiempoTotalSeg\": ").append(numero(ganador.getTiempoTotalSeg())).append(",\n");
            json.append("      \"mejorVueltaSeg\": ").append(numero(ganador.getMejorVueltaSeg())).append("\n");
            json.append("    },\n");
        }

        json.append("    \"clasificacion\": [\n");
        for (int i = 0; i < clasificacionFinal.size(); i++) {
            EstadoVehiculo e = clasificacionFinal.get(i);
            json.append("      {");
            json.append("\"posicion\": ").append(i + 1).append(", ");
            json.append("\"piloto\": \"").append(escapar(e.getParticipante().getPiloto().getNombre())).append("\", ");
            json.append("\"equipo\": \"").append(escapar(e.getEquipo())).append("\", ");
            json.append("\"vehiculo\": \"").append(escapar(e.getParticipante().getVehiculo().getModelo())).append("\", ");
            json.append("\"vueltasCompletas\": ").append(e.getVueltasCompletas()).append(", ");
            json.append("\"tiempoTotalSeg\": ").append(numero(e.getTiempoTotalSeg())).append(", ");
            json.append("\"mejorVueltaSeg\": ").append(numero(e.getMejorVueltaSeg()));
            json.append("}");
            json.append(i < clasificacionFinal.size() - 1 ? ",\n" : "\n");
        }
        json.append("    ]\n");
        json.append("  }");
        return json.toString();
    }

    /** Inserta el nuevo objeto dentro del arreglo JSON del archivo, creando el archivo si no existe. */
    private void agregarAlArray(String nuevoObjeto) {
        try {
            String contenidoNuevo;
            if (!Files.exists(ARCHIVO)) {
                contenidoNuevo = "[\n" + nuevoObjeto + "\n]\n";
            } else {
                String actual = Files.readString(ARCHIVO, StandardCharsets.UTF_8).trim();
                if (actual.isEmpty() || actual.equals("[]")) {
                    contenidoNuevo = "[\n" + nuevoObjeto + "\n]\n";
                } else {
                    int ultimoCorchete = actual.lastIndexOf(']');
                    String sinCierre = ultimoCorchete >= 0 ? actual.substring(0, ultimoCorchete).trim() : actual;
                    contenidoNuevo = sinCierre.endsWith("[")
                            ? sinCierre + "\n" + nuevoObjeto + "\n]\n"
                            : sinCierre + ",\n" + nuevoObjeto + "\n]\n";
                }
            }
            Files.writeString(ARCHIVO, contenidoNuevo, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el historial de carreras en JSON", e);
        }
    }

    private static String numero(Double valor) {
        return valor == null ? "null" : String.format(Locale.US, "%.3f", valor);
    }

    private static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
