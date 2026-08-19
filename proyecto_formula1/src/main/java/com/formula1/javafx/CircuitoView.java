package com.formula1.javafx;

import java.util.List;

import com.formula1.circuits.Circuit;
import com.formula1.model.EstadoVehiculo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Representacion visual del circuito y de los vehiculos sobre el.
 *
 * El trazado se dibuja como un ovalo estilizado (Canvas + strokeOval), que es
 * la misma tecnica que ya traia el prototipo original de RaceTrackApp. Cada
 * vehiculo se ubica sobre ese ovalo segun su angulo actual
 * (EstadoVehiculo.getAnguloRad()), que a su vez viene directamente del
 * progreso calculado por MotorSimulacion — es decir, la posicion visual SI
 * corresponde al estado real de la simulacion, no a una animacion aparte.
 *
 * PROPUESTA: el circuito real del proyecto (Circuit) no trae coordenadas de
 * trazado (rectas/curvas/sectores), solo nombre/pais/longitud/vueltas. Por
 * eso se usa un ovalo generico para representarlo; si se desea un trazado
 * fiel a cada circuito (Monaco, Spa, Monza, etc.) habria que modelar esas
 * coordenadas como un dato nuevo, que hoy no existe en el proyecto.
 */
public class CircuitoView {

    private static final double CENTRO_X = 480;
    private static final double CENTRO_Y = 320;
    private static final double RX_EXTERIOR = 420;
    private static final double RY_EXTERIOR = 240;
    private static final double ANCHO_PISTA = 40;

    private final Canvas canvas;

    public CircuitoView(Canvas canvas) {
        this.canvas = canvas;
    }

    public void dibujar(Circuit circuito, List<EstadoVehiculo> vehiculos, boolean finalizado, EstadoVehiculo ganador) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.web("#0b0e14"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        dibujarPista();
        dibujarVehiculos(vehiculos);
        dibujarEncabezado(circuito);

        if (finalizado && ganador != null) {
            dibujarCartelGanador(ganador);
        }
    }

    private void dibujarPista() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double rxInterior = RX_EXTERIOR - ANCHO_PISTA;
        double ryInterior = RY_EXTERIOR - ANCHO_PISTA;

        gc.setStroke(Color.web("#d73a49"));
        gc.setLineWidth(ANCHO_PISTA + 8);
        gc.strokeOval(CENTRO_X - (RX_EXTERIOR - ANCHO_PISTA / 2), CENTRO_Y - (RY_EXTERIOR - ANCHO_PISTA / 2),
                (RX_EXTERIOR - ANCHO_PISTA / 2) * 2, (RY_EXTERIOR - ANCHO_PISTA / 2) * 2);

        gc.setStroke(Color.web("#1f242d"));
        gc.setLineWidth(ANCHO_PISTA);
        gc.strokeOval(CENTRO_X - (RX_EXTERIOR - ANCHO_PISTA / 2), CENTRO_Y - (RY_EXTERIOR - ANCHO_PISTA / 2),
                (RX_EXTERIOR - ANCHO_PISTA / 2) * 2, (RY_EXTERIOR - ANCHO_PISTA / 2) * 2);

        gc.setStroke(Color.web("#30363d"));
        gc.setLineWidth(1.5);
        gc.setLineDashes(10, 10);
        gc.strokeOval(CENTRO_X - (RX_EXTERIOR - ANCHO_PISTA / 2), CENTRO_Y - (RY_EXTERIOR - ANCHO_PISTA / 2),
                (RX_EXTERIOR - ANCHO_PISTA / 2) * 2, (RY_EXTERIOR - ANCHO_PISTA / 2) * 2);
        gc.setLineDashes(null);

        gc.setStroke(Color.web("#8b949e"));
        gc.setLineWidth(1.5);
        gc.strokeOval(CENTRO_X - RX_EXTERIOR, CENTRO_Y - RY_EXTERIOR, RX_EXTERIOR * 2, RY_EXTERIOR * 2);
        gc.strokeOval(CENTRO_X - rxInterior, CENTRO_Y - ryInterior, rxInterior * 2, ryInterior * 2);

        // Linea de salida / meta
        double metaYExt = CENTRO_Y - RY_EXTERIOR;
        double metaYInt = CENTRO_Y - ryInterior;
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);
        gc.strokeLine(CENTRO_X, metaYExt, CENTRO_X, metaYInt);
    }

    private void dibujarVehiculos(List<EstadoVehiculo> vehiculos) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (EstadoVehiculo estado : vehiculos) {
            double proporcion = estado.getCarril() / 9.0;
            double rx = RX_EXTERIOR - 6 - proporcion * (ANCHO_PISTA - 12);
            double ry = rx * (RY_EXTERIOR / RX_EXTERIOR);

            double theta = estado.getAnguloRad();
            double x = CENTRO_X + rx * Math.sin(theta);
            double y = CENTRO_Y - ry * Math.cos(theta);

            gc.setFill(estado.getColor());
            gc.fillOval(x - 6, y - 6, 12, 12);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.strokeOval(x - 6, y - 6, 12, 12);
        }
    }

    private void dibujarEncabezado(Circuit circuito) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#f0f6fc"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 16));
        gc.fillText(circuito.getNombre() + " (" + circuito.getPais() + ") - "
                + circuito.getLongitud_km() + " km", 20, 26);
    }

    private void dibujarCartelGanador(EstadoVehiculo ganador) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#000000", 0.75));
        gc.fillRect(CENTRO_X - 190, CENTRO_Y - 45, 380, 90);
        gc.setStroke(Color.web("#3fb950"));
        gc.setLineWidth(2);
        gc.strokeRect(CENTRO_X - 190, CENTRO_Y - 45, 380, 90);

        gc.setFill(Color.web("#3fb950"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 20));
        gc.fillText("¡GANADOR DE LA CARRERA!", CENTRO_X - 150, CENTRO_Y - 10);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 16));
        gc.fillText(ganador.getParticipante().getPiloto().getNombre() + " (" + ganador.getEquipo() + ")",
                CENTRO_X - 150, CENTRO_Y + 20);
    }
}
