package com.formula1.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.formula1.repositories.VehicleRepository;
import com.formula1.vehicles.Vehicle;

public class VehicleRepositoryJDBC implements VehicleRepository {

    private Connection conexion;

    public VehicleRepositoryJDBC(Connection conexion) {
        this.conexion = conexion;
    }

    //guardar vehiculo con cada dato
    @Override
    public void guardar(Vehicle vehiculo) {
        String sql = "INSERT INTO vehicle (motor, modelo, aceleracion, velocidad_maxima, id_equipo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, vehiculo.getMotor());
            stmt.setString(2, vehiculo.getModelo());
            stmt.setDouble(3, vehiculo.getAceleracion());
            stmt.setInt(4, vehiculo.getVelocidadMaxima());
            stmt.setInt(5, vehiculo.getIdEquipo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // buscarPorId, listarTodos y eliminar quedan como tu ejercicio
    //buscar vehiculo por id, se ingresa un ide y se hace la consulta en mysql
    @Override
    public Vehicle buscarPorId(int idVehiculo) {
        String sql = "SELECT id_vehiculo, motor, modelo, aceleracion, velocidad_maxima, id_equipo FROM vehicle WHERE id_vehiculo = ?";
        Vehicle vehiculo = null; // por si no se encuentra nada
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idVehiculo);
            ResultSet rs = stmt.executeQuery(); // aquí SÍ guardas el resultado
            if (rs.next()) {
                vehiculo = new Vehicle(
                        rs.getInt("id_vehiculo"),
                        rs.getString("motor"),
                        rs.getString("modelo"),
                        rs.getDouble("aceleracion"),
                        rs.getInt("velocidad_maxima"),
                        rs.getInt("id_equipo")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehiculo; // fuera del try-catch, así siempre hay un return
    }

    //listar todos los vehiculos, consulta para traer todas las columnas de la tabla vehiculos
    @Override
    public List<Vehicle> listarTodos() {
        String sql = "SELECT id_vehiculo, motor, modelo, aceleracion, velocidad_maxima, id_equipo FROM vehicle";
        List<Vehicle> vehiculos = new ArrayList<>(); // la lista donde vas acumulando
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // arma un Vehicle igual que en buscarPorId...
                // y agrégalo a la lista con vehiculos.add(...)
                Vehicle vehiculo = new Vehicle(
                        rs.getInt("id_vehiculo"),
                        rs.getString("motor"),
                        rs.getString("modelo"),
                        rs.getDouble("aceleracion"),
                        rs.getInt("velocidad_maxima"),
                        rs.getInt("id_equipo"));
                vehiculos.add(vehiculo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehiculos;
    }

    //eliminar algun vehiculo
    @Override
    public void eliminar(int idVehiculo) {
        String sql = "DELETE FROM vehicle WHERE id_vehiculo = ? ";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idVehiculo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
