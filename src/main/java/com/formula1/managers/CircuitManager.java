package com.formula1.managers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.formula1.circuits.Circuit;

public class CircuitManager {

    private final Map<Integer, Circuit> circuitos = new LinkedHashMap<>();
    private int siguienteId = 1;

    public void agregar(Circuit circuito) {
        int id = circuito.getID_circuito();
        if (id <= 0) {
            id = siguienteId;
            circuito.setID_circuito(id);
        }
        siguienteId = Math.max(siguienteId, id + 1);
        circuitos.put(id, circuito);
    }

    public List<Circuit> listar() {
        return new ArrayList<>(circuitos.values());
    }

    public Circuit buscarPorId(int id) {
        return circuitos.get(id);
    }

    public void editar(int id, Circuit editado) {
        if (circuitos.containsKey(id)) {
            editado.setID_circuito(id);
            circuitos.put(id, editado);
        }
    }

    public boolean eliminar(int id) {
        return circuitos.remove(id) != null;
    }

    public List<Circuit> buscarPorNombreOPais(String texto) {
        String buscado = texto == null ? "" : texto.toLowerCase();
        List<Circuit> resultado = new ArrayList<>();
        for (Circuit c : circuitos.values()) {
            boolean coincideNombre = c.getNombre() != null && c.getNombre().toLowerCase().contains(buscado);
            boolean coincidePais = c.getPais() != null && c.getPais().toLowerCase().contains(buscado);
            if (coincideNombre || coincidePais) {
                resultado.add(c);
            }
        }
        return resultado;
    }
}
