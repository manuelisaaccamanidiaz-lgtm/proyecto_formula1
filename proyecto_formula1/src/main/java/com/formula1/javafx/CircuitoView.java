package com.formula1.javafx;

import java.util.List;

import com.formula1.circuits.Circuit;
import com.formula1.model.EstadoVehiculo;

<<<<<<< HEAD
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
=======
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
>>>>>>> 172cf7defab89d315d8e4c14da82520809b98f82
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Representacion visual del circuito y de los vehiculos sobre el.
 *
<<<<<<< HEAD
 * A diferencia de la version original (un unico ovalo generico para todos
 * los circuitos), aqui cada circuito tiene su propio trazado
 * ({@link TrazadosPistaFactory#obtener(int)}), asi que la forma de la pista
 * cambia segun el circuito elegido. La posicion de cada vehiculo se calcula
 * a partir de su progreso de vuelta (EstadoVehiculo.getAnguloRad() / 2*PI)
 * sobre ese trazado, desplazado lateralmente segun su carril para que los
 * autos no queden todos exactamente encima de la linea central.
 */
public class CircuitoView {

=======
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
>>>>>>> 172cf7defab89d315d8e4c14da82520809b98f82
    private static final double ANCHO_PISTA = 40;

    private final Canvas canvas;

    public CircuitoView(Canvas canvas) {
        this.canvas = canvas;
    }

<<<<<<< HEAD
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
=======
    public void dibujar(Circuit circuito, List<EstadoVehiculo> vehiculos, boolean finalizado, EstadoVehiculo ganador) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
>>>>>>> 172cf7defab89d315d8e4c14da82520809b98f82

        gc.setFill(Color.web("#0b0e14"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

<<<<<<< HEAD
        dibujarPista(trazado);
        dibujarVehiculos(trazado, vehiculos);
=======
        dibujarPista();
        dibujarVehiculos(vehiculos);
>>>>>>> 172cf7defab89d315d8e4c14da82520809b98f82
        dibujarEncabezado(circuito);

        if (finalizado && ganador != null) {
            dibujarCartelGanador(ganador);
        }
    }

<<<<<<< HEAD
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
=======
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
>>>>>>> 172cf7defab89d315d8e4c14da82520809b98f82
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
<<<<<<< HEAD
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
=======
        gc.setFill(Color.web("#000000", 0.75));
        gc.fillRect(CENTRO_X - 190, CENTRO_Y - 45, 380, 90);
        gc.setStroke(Color.web("#3fb950"));
        gc.setLineWidth(2);
        gc.strokeRect(CENTRO_X - 190, CENTRO_Y - 45, 380, 90);

        gc.setFill(Color.web("#3fb950"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 20));
        gc.fillText("¡GANADOR DE LA CARRERA!", CENTRO_X - 150, CENTRO_Y - 10);
>>>>>>> 172cf7defab89d315d8e4c14da82520809b98f82

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 16));
        gc.fillText(ganador.getParticipante().getPiloto().getNombre() + " (" + ganador.getEquipo() + ")",
<<<<<<< HEAD
                centroX - 150, centroY + 20);
=======
                CENTRO_X - 150, CENTRO_Y + 20);
>>>>>>> 172cf7defab89d315d8e4c14da82520809b98f82
    }
}
