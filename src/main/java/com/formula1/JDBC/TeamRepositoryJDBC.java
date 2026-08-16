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
            stmt.setString(1, equipo.getNombre());
            stmt.setString(2, equipo.getPais());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // buscarPorId, listarTodos y eliminar quedan como tu ejercicio
    //buscar vehiculo por id, se ingresa un ide y se hace la consulta en mysql
    @Override
    public Team buscarPorId(int idTeam) {
        String sql = "SELECT id_equipo, nombre, pais FROM team WHERE id_equipo  = ?";
        Team equipo = null; // por si no se encuentra nada
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idTeam);
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
        String sql = "SELECT id_equipo, nombre, pais FROM team";
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
        String sql = "DELETE FROM team WHERE id_equipo = ? ";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idEquipo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
