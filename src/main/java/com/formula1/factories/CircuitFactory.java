package com.formula1.factories;

import com.formula1.circuits.Circuit;

public interface CircuitFactory {
    Circuit createCircuit(int id_circuito);
}
