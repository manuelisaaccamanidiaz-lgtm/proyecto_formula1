package com.formula1.managers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.formula1.vehicles.Vehicle;

public class VehicleManager {

    private final Map<Integer, Vehicle> vehiculos = new LinkedHashMap<>();
    private int siguienteId = 1;

    public void agregar(Vehicle vehiculo) {
        int id = vehiculo.getId_vehiculo();
        if (id <= 0) {
            id = siguienteId;
            vehiculo.setId_vehiculo(id);
        }
        siguienteId = Math.max(siguienteId, id + 1);
        vehiculos.put(id, vehiculo);
    }

    public List<Vehicle> listar() {
        return new ArrayList<>(vehiculos.values());
    }

    public Vehicle buscarPorId(int id) {
        return vehiculos.get(id);
    }

    public void editar(int id, Vehicle editado) {
        if (vehiculos.containsKey(id)) {
            editado.setId_vehiculo(id);
            vehiculos.put(id, editado);
        }
    }

    public boolean eliminar(int id) {
        return vehiculos.remove(id) != null;
    }

    public List<Vehicle> buscarPorEquipoOModelo(String texto) {
        String buscado = texto == null ? "" : texto.toLowerCase();
        List<Vehicle> resultado = new ArrayList<>();
        for (Vehicle v : vehiculos.values()) {
            boolean coincideModelo = v.getModelo() != null && v.getModelo().toLowerCase().contains(buscado);
            boolean coincideEquipo = v.getEquipo() != null && v.getEquipo().toLowerCase().contains(buscado);
            if (coincideModelo || coincideEquipo) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    /** Asigna un piloto a un vehiculo. Devuelve false si el vehiculo no existe. */
    public boolean asignarPiloto(int idVehiculo, int idPiloto) {
        Vehicle vehiculo = vehiculos.get(idVehiculo);
        if (vehiculo == null) {
            return false;
        }
        vehiculo.asignarPiloto(idPiloto);
        return true;
    }

    /** Genera una tabla de texto comparando velocidad, aceleracion y equipo de varios vehiculos. */
    public String comparar(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s %-12s %-12s %-16s %-10s %-8s%n",
                "Id", "Motor", "Modelo", "Equipo", "Vel.Max", "Acel"));
        for (Integer id : ids) {
            Vehicle v = vehiculos.get(id);
            if (v == null) {
                sb.append("(vehiculo id ").append(id).append(" no encontrado)").append(System.lineSeparator());
                continue;
            }
            sb.append(String.format("%-4d %-12s %-12s %-16s %-10d %-8.1f%n",
                    v.getId_vehiculo(), v.getMotor(), v.getModelo(),
                    v.getEquipo() == null ? "-" : v.getEquipo(),
                    v.getVelocidad_maxima(), v.getAceleracion()));
        }
        return sb.toString();
    }
}
