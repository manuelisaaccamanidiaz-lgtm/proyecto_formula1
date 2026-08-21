package com.formula1.javafx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Point2D;

/**
 * Genera un {@link TrazadoPista} distinto para cada uno de los 10 circuitos
 * de {@code F1CircuitFactory} (ids 1-10). El objetivo del enunciado es que
 * "cada pista tenga forma diferente", asi que en vez de intentar copiar el
 * trazado real pixel a pixel de cada circuito (dato que el proyecto no
 * modela), cada uno se construye con una silueta propia y reconocible:
 * calle estrecha y angulosa, ovalo alargado, forma de rinon, "eses", un
 * ocho real (Suzuka), etc.
 *
 * Todas las formas se definen directamente en el espacio del Canvas
 * (960x640, centro aproximado en 480,320) para que CircuitoView solo tenga
 * que dibujarlas, sin transformaciones adicionales.
 */
public final class TrazadosPistaFactory {

    private static final double CX = 480;
    private static final double CY = 330;

    private static final Map<Integer, TrazadoPista> CACHE = new HashMap<>();

    private TrazadosPistaFactory() {
    }

    /** Devuelve el trazado del circuito con ese id, o un ovalo por defecto si no hay uno definido. */
    public static TrazadoPista obtener(int idCircuito) {
        return CACHE.computeIfAbsent(idCircuito, TrazadosPistaFactory::construir);
    }

    private static TrazadoPista construir(int idCircuito) {
        return switch (idCircuito) {
            case 1 -> monaco();
            case 2 -> silverstone();
            case 3 -> spa();
            case 4 -> monza();
            case 5 -> interlagos();
            case 6 -> yasMarina();
            case 7 -> suzuka();
            case 8 -> baku();
            case 9 -> zandvoort();
            case 10 -> cota();
            default -> ovaloPorDefecto();
        };
    }

    // -----------------------------------------------------------------
    // Utilidades de construccion
    // -----------------------------------------------------------------

    private static Point2D p(double x, double y) {
        return new Point2D(CX + x, CY + y);
    }

    /**
     * Genera puntos de control repartidos en un elipse (rx, ry), cada uno
     * escalado por un multiplicador de radio distinto, para crear una
     * silueta "organica" (no un ovalo perfecto). Cuanto mas separados entre
     * si esten los multiplicadores, mas angulosa/irregular se ve la pista.
     */
    private static List<Point2D> lobulo(double rx, double ry, double[] multiplicadores) {
        List<Point2D> controles = new ArrayList<>();
        int n = multiplicadores.length;
        for (int i = 0; i < n; i++) {
            double angulo = (2 * Math.PI * i) / n;
            double m = multiplicadores[i];
            controles.add(p(rx * m * Math.cos(angulo), ry * m * Math.sin(angulo)));
        }
        return controles;
    }

    // -----------------------------------------------------------------
    // 1. Monaco: calle estrecha, muy angosta y con curvas cerradas
    // -----------------------------------------------------------------
    private static TrazadoPista monaco() {
        double[] mult = {1.00, 1.00, 0.55, 0.55, 0.85, 0.40, 0.40, 0.75, 1.05, 1.05, 0.70, 0.70};
        List<Point2D> controles = lobulo(300, 190, mult);
        return TrazadoPista.desdeControles(controles, 8); // pocos segmentos = curvas mas cerradas
    }

    // -----------------------------------------------------------------
    // 2. Silverstone: trazado rapido, amplio y redondeado
    // -----------------------------------------------------------------
    private static TrazadoPista silverstone() {
        double[] mult = {1.00, 0.92, 1.05, 0.88, 1.10, 0.95, 1.00, 0.90, 1.08, 0.93, 1.02, 0.90, 1.05, 0.95};
        List<Point2D> controles = lobulo(410, 235, mult);
        return TrazadoPista.desdeControles(controles, 20);
    }

    // -----------------------------------------------------------------
    // 3. Spa-Francorchamps: muy alargado, con un "pellizco" (Eau Rouge)
    // -----------------------------------------------------------------
    private static TrazadoPista spa() {
        List<Point2D> controles = List.of(
                p(-420, 0), p(-300, -160), p(-120, -190), p(0, -140),
                p(40, -60), p(120, -30), // pellizco de Eau Rouge
                p(260, -170), p(420, -40), p(380, 120),
                p(180, 200), p(-40, 190), p(-260, 150), p(-400, 60)
        );
        return TrazadoPista.desdeControles(controles, 18);
    }

    // -----------------------------------------------------------------
    // 4. Monza: "templo de la velocidad", muy largo, plano y con chicanas
    // -----------------------------------------------------------------
    private static TrazadoPista monza() {
        List<Point2D> controles = List.of(
                p(-420, -60), p(-200, -140), p(-190, -140), p(0, -130),
                p(180, -140), p(420, -60), p(420, 20),
                p(240, 60), p(230, 60), p(60, 100),
                p(-240, 100), p(-420, 40)
        );
        return TrazadoPista.desdeControles(controles, 10);
    }

    // -----------------------------------------------------------------
    // 5. Interlagos: forma de "rinon", con desnivel visual marcado
    // -----------------------------------------------------------------
    private static TrazadoPista interlagos() {
        double[] mult = {1.00, 0.95, 0.60, 0.65, 0.95, 1.05, 1.00, 0.90, 1.08, 0.92, 1.00, 0.88};
        List<Point2D> controles = lobulo(340, 220, mult);
        return TrazadoPista.desdeControles(controles, 18);
    }

    // -----------------------------------------------------------------
    // 6. Yas Marina: trazado moderno y ondulado
    // -----------------------------------------------------------------
    private static TrazadoPista yasMarina() {
        double[] mult = {1.00, 0.85, 1.05, 0.85, 1.05, 0.85, 1.05, 0.85, 1.05, 0.85, 1.05, 0.85, 1.05, 0.85};
        List<Point2D> controles = lobulo(390, 210, mult);
        return TrazadoPista.desdeControles(controles, 16);
    }

    // -----------------------------------------------------------------
    // 7. Suzuka: el unico trazado real "en ocho" del calendario
    // -----------------------------------------------------------------
    private static TrazadoPista suzuka() {
        double a = 330;
        double b = 200;
        return TrazadoPista.desdeParametrica(t -> {
            double angulo = t * 2 * Math.PI;
            double denom = 1 + Math.sin(angulo) * Math.sin(angulo);
            double x = (a * Math.cos(angulo)) / denom;
            double y = (b * Math.sin(angulo) * Math.cos(angulo)) / denom;
            return p(x, y);
        }, 400);
    }

    // -----------------------------------------------------------------
    // 8. Baku: circuito callejero larguisimo y angosto (recta + castillo)
    // -----------------------------------------------------------------
    private static TrazadoPista baku() {
        List<Point2D> controles = List.of(
                p(-430, 40), p(-430, 40), p(-260, -60), p(-260, -60), p(-150, -40),
                p(-90, -80), p(-90, -80), p(-30, -40), p(60, -70), p(60, -70),
                p(150, -30), p(420, -30), p(420, 30),
                p(140, 60), p(-40, 40), p(-40, 40), p(-160, 90), p(-300, 90)
        );
        return TrazadoPista.desdeControles(controles, 8);
    }

    // -----------------------------------------------------------------
    // 9. Zandvoort: ovalo corto, asimetrico, con curva peraltada final
    // -----------------------------------------------------------------
    private static TrazadoPista zandvoort() {
        double[] mult = {1.00, 0.90, 0.80, 0.85, 1.00, 1.10, 1.05, 0.95, 0.85, 0.95};
        List<Point2D> controles = lobulo(360, 205, mult);
        return TrazadoPista.desdeControles(controles, 18);
    }

    // -----------------------------------------------------------------
    // 10. Circuito de las Americas: sector inicial de "eses" muy marcado
    // -----------------------------------------------------------------
    private static TrazadoPista cota() {
        double[] mult = {1.00, 0.80, 1.00, 0.80, 1.00, 0.80, 1.00, 0.85, 0.95, 1.05, 0.95, 1.05, 0.90, 0.60};
        List<Point2D> controles = lobulo(400, 230, mult);
        return TrazadoPista.desdeControles(controles, 14);
    }

    private static TrazadoPista ovaloPorDefecto() {
        double[] mult = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        List<Point2D> controles = lobulo(380, 220, mult);
        return TrazadoPista.desdeControles(controles, 20);
    }
}
