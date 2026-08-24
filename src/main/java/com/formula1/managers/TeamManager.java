package com.formula1.managers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.formula1.teams.Team;

public class TeamManager {

    private final Map<Integer, Team> equipos = new LinkedHashMap<>();
    private int siguienteId = 1;

    public void agregar(Team equipo) {
        int id = equipo.getID_equipo();
        if (id <= 0) {
            id = siguienteId;
            equipo.setID_equipo(id);
        }
        siguienteId = Math.max(siguienteId, id + 1);
        equipos.put(id, equipo);
    }

    public List<Team> listar() {
        return new ArrayList<>(equipos.values());
    }

    public Team buscarPorId(int id) {
        return equipos.get(id);
    }

    public void editar(int id, Team editado) {
        if (equipos.containsKey(id)) {
            editado.setID_equipo(id);
            equipos.put(id, editado);
        }
    }

    public boolean eliminar(int id) {
        return equipos.remove(id) != null;
    }

    public List<Team> buscarPorNombreOPais(String texto) {
        String buscado = texto == null ? "" : texto.toLowerCase();
        List<Team> resultado = new ArrayList<>();
        for (Team t : equipos.values()) {
            boolean coincideNombre = t.getNombre() != null && t.getNombre().toLowerCase().contains(buscado);
            boolean coincidePais = t.getPais() != null && t.getPais().toLowerCase().contains(buscado);
            if (coincideNombre || coincidePais) {
                resultado.add(t);
            }
        }
        return resultado;
    }
}
