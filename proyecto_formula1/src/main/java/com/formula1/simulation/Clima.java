package com.formula1.simulation;

import java.util.Random;

public enum Clima {
    SECO,
    LLUVIOSO,
    EXTREMO;

    private static final Random RANDOM = new Random();

    /** Genera un clima aleatorio para la sesion de clasificacion. */
    public static Clima aleatorio() {
        Clima[] valores = values();
        return valores[RANDOM.nextInt(valores.length)];
    }
}
