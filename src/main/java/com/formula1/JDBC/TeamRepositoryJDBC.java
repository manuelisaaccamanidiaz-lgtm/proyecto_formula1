package com.formula1.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.formula1.repositories.TeamRepository;
import com.formula1.teams.Team;

public class TeamRepositoryJDBC implements TeamRepository{
    private Connection conexion;

    public TeamRepositoryJDBC(Connection conexion) {
        this.conexion = conexion;
    }

    //guardar vehiculo con cada dato
    @Override
    public void guardar(Team equipo) {
        String sql = "INSERT INTO team (nombre, pais) VALUES (?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(2, equipo.getNombre());
            stmt.setString(3, equipo.getPais());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // buscarPorId, listarTodos y eliminar quedan como tu ejercicio
    //buscar vehiculo por id, se ingresa un ide y se hace la consulta en mysql
    @Override
    public Team buscarPorId(int idVehiculo) {
        String sql = "SELECT id_vehiculo, motor, modelo, aceleracion, velocidad_maxima, id_equipo FROM vehicle WHERE id_vehiculo = ?";
        Team equipo = null; // por si no se encuentra nada
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idVehiculo);
            ResultSet rs = stmt.executeQuery(); // aquí SÍ guardas el resultado
            if (rs.next()) {
                equipo = new Team(
                        rs.getInt("id_equipo"),
                        rs.getString("nombre"),
                        rs.getString("pais")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipo; // fuera del try-catch, así siempre hay un return
    }

    //listar todos los vehiculos, consulta para traer todas las columnas de la tabla vehiculos
    @Override
    public List<Team> listarTodos() {
        String sql = "SELECT id_vehiculo, motor, modelo, aceleracion, velocidad_maxima, id_equipo FROM vehicle";
        List<Team> equipoList = new ArrayList<>(); // la lista donde vas acumulando
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // arma un Vehicle igual que en buscarPorId...
                // y agrégalo a la lista con vehiculos.add(...)
                Team equipo = new Team(
                        rs.getInt("id_equipo"),
                        rs.getString("nombre"),
                        rs.getString("pais"));
                equipoList.add(equipo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipoList;
    }

    //eliminar algun vehiculo
    @Override
    public void eliminar(int idEquipo) {
        String sql = "DELETE FROM vehicle WHERE id_vehiculo = ? ";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idEquipo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
