package com.formula1.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.formula1.pilots.Pilot;
import com.formula1.repositories.PilotRepository;

public class PilotRepositoryJDBC implements PilotRepository{
    private Connection conexion;

    public PilotRepositoryJDBC(Connection conexion) {
        this.conexion = conexion;
    }

    //guardar vehiculo con cada dato
    @Override
    public void guardar(Pilot piloto) {
        String sql = "INSERT INTO pilot (nombre, rol, id_equipo, id_vehiculo) VALUES (?, ?, ? , ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, piloto.getNombre());
            stmt.setString(2, piloto.getRol());
            stmt.setInt(3, piloto.getIdEquipo());
            stmt.setInt(4, piloto.getIdVehiculo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // buscarPorId, listarTodos y eliminar quedan como tu ejercicio
    //buscar vehiculo por id, se ingresa un ide y se hace la consulta en mysql
    @Override
    public Pilot buscarPorId(int idPiloto) {
        String sql = "SELECT id_piloto, nombre, rol, id_equipo, id_vehiculo FROM pilot WHERE id_piloto = ?";
        Pilot piloto = null; // por si no se encuentra nada
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idPiloto);
            ResultSet rs = stmt.executeQuery(); // aquí SÍ guardas el resultado
            if (rs.next()) {
                piloto = new Pilot(
                        rs.getInt("id_piloto"),
                        rs.getString("nombre"),
                        rs.getString("rol"),
                        rs.getInt("id_equipo"),
                        rs.getInt("id_vehiculo")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return piloto; // fuera del try-catch, así siempre hay un return
    }

    //listar todos los vehiculos, consulta para traer todas las columnas de la tabla vehiculos
    @Override
    public List<Pilot> listarTodos() {
        String sql = "SELECT id_piloto, nombre, rol, id_equipo, id_vehiculo FROM pilot";
        List<Pilot> pilotos = new ArrayList<>(); // la lista donde vas acumulando
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // arma un Vehicle igual que en buscarPorId...
                // y agrégalo a la lista con vehiculos.add(...)
                Pilot piloto = new Pilot(
                        rs.getInt("id_piloto"),
                        rs.getString("nombre"),
                        rs.getString("rol"),
                        rs.getInt("id_equipo"),
                        rs.getInt("id_vehiculo")
                );
                pilotos.add(piloto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pilotos;
    }

    //eliminar algun vehiculo
    @Override
    public void eliminar(int idPilot) {
        String sql = "DELETE FROM pilot WHERE id_piloto = ? ";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idPilot);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
