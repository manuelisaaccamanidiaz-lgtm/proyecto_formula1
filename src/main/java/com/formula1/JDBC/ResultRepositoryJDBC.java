package com.formula1.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.formula1.repositories.ResultRepository;
import com.formula1.results.Result;

public class ResultRepositoryJDBC implements ResultRepository {
    private Connection conexion;

    public ResultRepositoryJDBC(Connection conexion) {
        this.conexion = conexion;
    }

    //guardar vehiculo con cada dato
    @Override
    public void guardar(Result resultado) {
        String sql = "INSERT INTO result (tiempo, id_circuito, id_vehiculo) VALUES (?, ?, ? )";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, resultado.getTiempo());
            stmt.setInt(2, resultado.getIdCircuito());
            stmt.setInt(3, resultado.getIdVehiculo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // buscarPorId, listarTodos y eliminar quedan como tu ejercicio
    //buscar vehiculo por id, se ingresa un ide y se hace la consulta en mysql
    @Override
    public Result buscarPorId(int idResult) {
        String sql = "SELECT id_result, tiempo, id_circuito, id_vehiculo FROM result WHERE id_result = ?";
        Result resultado = null; // por si no se encuentra nada
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idResult);
            ResultSet rs = stmt.executeQuery(); // aquí SÍ guardas el resultado
            if (rs.next()) {
                resultado = new Result(
                        rs.getInt("id_result"),
                        rs.getDouble("tiempo"),
                        rs.getInt("id_circuito"),
                        rs.getInt("id_vehiculo")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado; // fuera del try-catch, así siempre hay un return
    }

    //listar todos los vehiculos, consulta para traer todas las columnas de la tabla vehiculos
    @Override
    public List<Result> listarTodos() {
        String sql = "SELECT id_result, tiempo, id_circuito, id_vehiculo FROM result";
        List<Result> resultados = new ArrayList<>(); // la lista donde vas acumulando
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // arma un Vehicle igual que en buscarPorId...
                // y agrégalo a la lista con vehiculos.add(...)
                Result resultado = new Result(
                        rs.getInt("id_result"),
                        rs.getDouble("tiempo"),
                        rs.getInt("id_circuito"),
                        rs.getInt("id_vehiculo")
                );
                resultados.add(resultado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultados;
    }

    //eliminar algun vehiculo
    @Override
    public void eliminar(int idResult) {
        String sql = "DELETE FROM result WHERE id_result = ? ";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idResult);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
