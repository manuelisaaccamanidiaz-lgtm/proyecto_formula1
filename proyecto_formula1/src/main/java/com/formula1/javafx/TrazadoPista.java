package com.formula1.javafx;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;

import javafx.geometry.Point2D;

/**
 * Representa el trazado (linea central) de UN circuito como una curva
 * CERRADA arbitraria, en vez del ovalo fijo que usaba la version anterior
 * de CircuitoView. Permite que cada circuito tenga una forma distinta
 * (calle estrecha con curvas cerradas, trazado rapido y redondeado, un
 * "ocho" tipo Suzuka, etc.).
 *
 * El trazado se guarda como una lista de puntos muestreados sobre la curva,
 * junto con la longitud de arco acumulada hasta cada punto. Eso permite
 * ubicar un vehiculo por "progreso" (0.0 a 1.0 = una vuelta completa) de
 * forma proporcional a la distancia real recorrida sobre la curva, en vez
 * de por angulo (que solo tiene sentido para un ovalo perfecto).
 */
public class TrazadoPista {

    private final List<Point2D> puntos; // muestreados, forman un lazo cerrado
    private final double[] longitudAcumulada; // longitud de arco hasta el punto i
    private final double longitudTotal;

    private TrazadoPista(List<Point2D> puntosMuestreados) {
        this.puntos = puntosMuestreados;
        this.longitudAcumulada = new double[puntos.size()];
        double acumulado = 0.0;
        for (int i = 0; i < puntos.size(); i++) {
            longitudAcumulada[i] = acumulado;
            Point2D actual = puntos.get(i);
            Point2D siguiente = puntos.get((i + 1) % puntos.size());
            acumulado += actual.distance(siguiente);
        }
        this.longitudTotal = acumulado;
    }

    /**
     * Construye un trazado cerrado y suave a partir de unos pocos puntos de
     * control, usando una spline de Catmull-Rom cerrada (cada tramo entre
     * dos puntos de control se subdivide en {@code segmentosPorTramo}
     * muestras). Repetir un mismo punto de control dos veces seguidas en la
     * lista produce una curva cerrada (chicane/hairpin) en ese lugar en vez
     * de una curva suave.
     */
    public static TrazadoPista desdeControles(List<Point2D> controles, int segmentosPorTramo) {
        List<Point2D> muestreados = new ArrayList<>();
        int n = controles.size();
        for (int i = 0; i < n; i++) {
            Point2D p0 = controles.get(Math.floorMod(i - 1, n));
            Point2D p1 = controles.get(i);
            Point2D p2 = controles.get((i + 1) % n);
            Point2D p3 = controles.get((i + 2) % n);
            for (int s = 0; s < segmentosPorTramo; s++) {
                double t = s / (double) segmentosPorTramo;
                muestreados.add(catmullRom(p0, p1, p2, p3, t));
            }
        }
        return new TrazadoPista(muestreados);
    }

    /** Construye un trazado cerrado a partir de una funcion parametrica explicita (t en [0,1)). */
    public static TrazadoPista desdeParametrica(DoubleFunction<Point2D> funcion, int muestras) {
        List<Point2D> muestreados = new ArrayList<>();
        for (int i = 0; i < muestras; i++) {
            muestreados.add(funcion.apply(i / (double) muestras));
        }
        return new TrazadoPista(muestreados);
    }

    private static Point2D catmullRom(Point2D p0, Point2D p1, Point2D p2, Point2D p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;
        double x = 0.5 * ((2 * p1.getX())
                + (-p0.getX() + p2.getX()) * t
                + (2 * p0.getX() - 5 * p1.getX() + 4 * p2.getX() - p3.getX()) * t2
                + (-p0.getX() + 3 * p1.getX() - 3 * p2.getX() + p3.getX()) * t3);
        double y = 0.5 * ((2 * p1.getY())
                + (-p0.getY() + p2.getY()) * t
                + (2 * p0.getY() - 5 * p1.getY() + 4 * p2.getY() - p3.getY()) * t2
                + (-p0.getY() + 3 * p1.getY() - 3 * p2.getY() + p3.getY()) * t3);
        return new Point2D(x, y);
    }

    /** Punto sobre el eje central del trazado para un progreso t (se normaliza a [0,1)). */
    public Point2D punto(double t) {
        return puntos.get(indiceParaProgreso(t));
    }

    /** Direccion de avance (tangente normalizada) en el progreso t. */
    public Point2D tangente(double t) {
        int i = indiceParaProgreso(t);
        Point2D actual = puntos.get(i);
        Point2D siguiente = puntos.get((i + 1) % puntos.size());
        Point2D delta = siguiente.subtract(actual);
        double norma = delta.magnitude();
        return norma == 0 ? new Point2D(1, 0) : delta.multiply(1.0 / norma);
    }

    /** Punto desplazado perpendicularmente a la tangente (para separar carriles dentro del ancho de pista). */
    public Point2D puntoConDesplazamiento(double t, double desplazamientoLateral) {
        Point2D p = punto(t);
        Point2D tan = tangente(t);
        Point2D normal = new Point2D(-tan.getY(), tan.getX());
        return p.add(normal.multiply(desplazamientoLateral));
    }

    private int indiceParaProgreso(double t) {
        double tNorm = t - Math.floor(t);
        double distanciaObjetivo = tNorm * longitudTotal;
        int lo = 0;
        int hi = puntos.size() - 1;
        while (lo < hi) {
            int mid = (lo + hi + 1) / 2;
            if (longitudAcumulada[mid] <= distanciaObjetivo) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }
        return lo;
    }

    public List<Point2D> getPuntos() {
        return puntos;
    }

    public double getLongitudTotal() {
        return longitudTotal;
    }
}
