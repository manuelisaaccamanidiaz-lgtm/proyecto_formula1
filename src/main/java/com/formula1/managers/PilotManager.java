package com.formula1.managers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.formula1.pilots.Pilot;

public class PilotManager {

    private final Map<Integer, Pilot> pilotos = new LinkedHashMap<>();
    private int siguienteId = 1;

    public void agregar(Pilot piloto) {
        int id = piloto.getID_pilot();
        if (id <= 0) {
            id = siguienteId;
            piloto.setID_pilot(id);
        }
        siguienteId = Math.max(siguienteId, id + 1);
        pilotos.put(id, piloto);
    }

    public List<Pilot> listar() {
        return new ArrayList<>(pilotos.values());
    }

    public Pilot buscarPorId(int id) {
        return pilotos.get(id);
    }

    public void editar(int id, Pilot editado) {
        if (pilotos.containsKey(id)) {
            editado.setID_pilot(id);
            pilotos.put(id, editado);
        }
    }

    public boolean eliminar(int id) {
        return pilotos.remove(id) != null;
    }

    public List<Pilot> buscarPorNombreOEquipo(String texto) {
        String buscado = texto == null ? "" : texto.toLowerCase();
        List<Pilot> resultado = new ArrayList<>();
        for (Pilot p : pilotos.values()) {
            boolean coincideNombre = p.getNombre() != null && p.getNombre().toLowerCase().contains(buscado);
            boolean coincideEquipo = p.getEquipo() != null && p.getEquipo().toLowerCase().contains(buscado);
            if (coincideNombre || coincideEquipo) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
