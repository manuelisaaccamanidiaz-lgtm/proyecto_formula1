package com.formula1.javafx;

import java.util.List;

import com.formula1.circuits.Circuit;
import com.formula1.model.EstadoVehiculo;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Representacion visual del circuito y de los vehiculos sobre el.
 *
 * A diferencia de la version original (un unico ovalo generico para todos
 * los circuitos), aqui cada circuito tiene su propio trazado
 * ({@link TrazadosPistaFactory#obtener(int)}), asi que la forma de la pista
 * cambia segun el circuito elegido. La posicion de cada vehiculo se calcula
 * a partir de su progreso de vuelta (EstadoVehiculo.getAnguloRad() / 2*PI)
 * sobre ese trazado, desplazado lateralmente segun su carril para que los
 * autos no queden todos exactamente encima de la linea central.
 */
public class CircuitoView {

    private static final double ANCHO_PISTA = 40;

    private final Canvas canvas;

    public CircuitoView(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Dibuja unicamente el trazado y el encabezado del circuito (sin
     * vehiculos ni cartel de ganador). Se usa para mostrar una imagen de
     * vista previa de la pista en la pantalla de configuracion de carrera,
     * antes de que exista ninguna simulacion en curso.
     */
    public void dibujarVistaPrevia(Circuit circuito) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        TrazadoPista trazado = TrazadosPistaFactory.obtener(circuito.getID_circuito());

        gc.setFill(Color.web("#0b0e14"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        dibujarPista(trazado);
        dibujarEncabezado(circuito);
    }

    public void dibujar(Circuit circuito, List<EstadoVehiculo> vehiculos, boolean finalizado, EstadoVehiculo ganador) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        TrazadoPista trazado = TrazadosPistaFactory.obtener(circuito.getID_circuito());

        gc.setFill(Color.web("#0b0e14"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        dibujarPista(trazado);
        dibujarVehiculos(trazado, vehiculos);
        dibujarEncabezado(circuito);

        if (finalizado && ganador != null) {
            dibujarCartelGanador(ganador);
        }
    }

    private void dibujarPista(TrazadoPista trazado) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        List<Point2D> puntos = trazado.getPuntos();

        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.setLineCap(StrokeLineCap.ROUND);

        // Asfalto con "curbs" rojos/blancos asomando por debajo (mismo truco que el ovalo original:
        // una linea mas ancha de un color y encima una mas angosta de otro).
        trazarRuta(gc, puntos, Color.web("#d73a49"), ANCHO_PISTA + 8);
        trazarRuta(gc, puntos, Color.web("#1f242d"), ANCHO_PISTA);

        gc.setLineDashes(10, 10);
        trazarRuta(gc, puntos, Color.web("#30363d"), 1.5);
        gc.setLineDashes(null);

        dibujarBorde(gc, trazado, ANCHO_PISTA / 2.0, Color.web("#8b949e"));
        dibujarBorde(gc, trazado, -ANCHO_PISTA / 2.0, Color.web("#8b949e"));

        // Linea de salida / meta, perpendicular a la tangente en t=0.
        Point2D interior = trazado.puntoConDesplazamiento(0, -ANCHO_PISTA / 2.0);
        Point2D exterior = trazado.puntoConDesplazamiento(0, ANCHO_PISTA / 2.0);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);
        gc.strokeLine(interior.getX(), interior.getY(), exterior.getX(), exterior.getY());
    }

    private void trazarRuta(GraphicsContext gc, List<Point2D> puntos, Color color, double ancho) {
        gc.setStroke(color);
        gc.setLineWidth(ancho);
        gc.beginPath();
        Point2D primero = puntos.get(0);
        gc.moveTo(primero.getX(), primero.getY());
        for (int i = 1; i < puntos.size(); i++) {
            Point2D punto = puntos.get(i);
            gc.lineTo(punto.getX(), punto.getY());
        }
        gc.closePath();
        gc.stroke();
    }

    private void dibujarBorde(GraphicsContext gc, TrazadoPista trazado, double desplazamiento, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(1.5);
        int muestras = 240;
        gc.beginPath();
        for (int i = 0; i <= muestras; i++) {
            double t = i / (double) muestras;
            Point2D punto = trazado.puntoConDesplazamiento(t, desplazamiento);
            if (i == 0) {
                gc.moveTo(punto.getX(), punto.getY());
            } else {
                gc.lineTo(punto.getX(), punto.getY());
            }
        }
        gc.stroke();
    }

    private void dibujarVehiculos(TrazadoPista trazado, List<EstadoVehiculo> vehiculos) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (EstadoVehiculo estado : vehiculos) {
            double proporcion = estado.getCarril() / 9.0;
            double desplazamiento = -(ANCHO_PISTA / 2.0 - 6) + proporcion * (ANCHO_PISTA - 12);

            double t = estado.getAnguloRad() / (2 * Math.PI);
            Point2D posicion = trazado.puntoConDesplazamiento(t, desplazamiento);

            gc.setFill(estado.getColor());
            gc.fillOval(posicion.getX() - 6, posicion.getY() - 6, 12, 12);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.strokeOval(posicion.getX() - 6, posicion.getY() - 6, 12, 12);
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
        double centroX = canvas.getWidth() / 2.0;
        double centroY = canvas.getHeight() / 2.0;

        gc.setFill(Color.web("#000000", 0.75));
        gc.fillRect(centroX - 190, centroY - 45, 380, 90);
        gc.setStroke(Color.web("#3fb950"));
        gc.setLineWidth(2);
        gc.strokeRect(centroX - 190, centroY - 45, 380, 90);

        gc.setFill(Color.web("#3fb950"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 20));
        gc.fillText("¡GANADOR DE LA CARRERA!", centroX - 150, centroY - 10);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 16));
        gc.fillText(ganador.getParticipante().getPiloto().getNombre() + " (" + ganador.getEquipo() + ")",
                centroX - 150, centroY + 20);
    }
}
