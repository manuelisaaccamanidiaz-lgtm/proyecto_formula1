package com.formula1.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.formula1.circuits.Circuit;
import com.formula1.repositories.CircuitRepository;

public class CircuitRepositoryJDBC implements CircuitRepository {
    private Connection conexion;

    public CircuitRepositoryJDBC(Connection conexion) {
        this.conexion = conexion;
    }

    //guardar vehiculo con cada dato
    @Override
    public void guardar(Circuit circuito) {
        String sql = "INSERT INTO team (nombre, longitud_km, descripcion, vueltas) VALUES (?, ?, ? , ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, circuito.getNombre());
            stmt.setDouble(2, circuito.getLongitud_km());
            stmt.setString(3, circuito.getDescripcion());
            stmt.setInt(4, circuito.getVueltas());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // buscarPorId, listarTodos y eliminar quedan como tu ejercicio
    //buscar vehiculo por id, se ingresa un ide y se hace la consulta en mysql
    @Override
    public Circuit buscarPorId(int idCircuito) {
        String sql = "SELECT id_circuito, nombre, longitud_km, descripcion, vueltas FROM circuit WHERE id_circuito = ?";
        Circuit circuito = null; // por si no se encuentra nada
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idCircuito);
            ResultSet rs = stmt.executeQuery(); // aquí SÍ guardas el resultado
            if (rs.next()) {
                circuito = new Circuit(
                        rs.getInt("id_circuito"),
                        rs.getString("nombre"),
                        rs.getDouble("longitud_km"),
                        rs.getString("descripcion"),
                        rs.getByte("vueltas")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return circuito; // fuera del try-catch, así siempre hay un return
    }

    //listar todos los vehiculos, consulta para traer todas las columnas de la tabla vehiculos
    @Override
    public List<Circuit> listarTodos() {
        String sql = "SELECT id_circuito, nombre, longitud_km, descripcion, vueltas FROM circuit";
        List<Circuit> circuitoList = new ArrayList<>(); // la lista donde vas acumulando
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // arma un Vehicle igual que en buscarPorId...
                // y agrégalo a la lista con vehiculos.add(...)
                Circuit circuito = new Circuit(
                        rs.getInt("id_circuito"),
                        rs.getString("nombre"),
                        rs.getDouble("longitud_km"),
                        rs.getString("descripcion"),
                        rs.getByte("vueltas"));
                circuitoList.add(circuito);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return circuitoList;
    }

    //eliminar algun vehiculo
    @Override
    public void eliminar(int idCircuito) {
        String sql = "DELETE FROM circuit WHERE id_circuito = ? ";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idCircuito);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
