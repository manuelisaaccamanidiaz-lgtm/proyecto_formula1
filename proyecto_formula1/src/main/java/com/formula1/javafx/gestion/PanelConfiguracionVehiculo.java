package com.formula1.javafx.gestion;

import com.formula1.config.CargaAerodinamica;
import com.formula1.config.ConfiguracionManager;
import com.formula1.config.ConfiguracionVehiculo;
import com.formula1.config.EstrategiaCombustible;
import com.formula1.config.PresionNeumaticos;
import com.formula1.managers.VehicleManager;
import com.formula1.vehicles.ModoConduccion;
import com.formula1.vehicles.Vehicle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/** Panel para elegir el reglaje (modo, aero, neumaticos, combustible) de un vehiculo, sin JOptionPane. */
public class PanelConfiguracionVehiculo {

    private final VehicleManager vehicleManager;
    private final ConfiguracionManager configManager;

    private final ComboBox<Vehicle> comboVehiculo = new ComboBox<>();
    private final ComboBox<ModoConduccion> comboModo = new ComboBox<>();
    private final ComboBox<CargaAerodinamica> comboAero = new ComboBox<>();
    private final ComboBox<PresionNeumaticos> comboPresion = new ComboBox<>();
    private final ComboBox<EstrategiaCombustible> comboCombustible = new ComboBox<>();
    private final Label actual = EstiloGestion.estado();
    private final Label estado = EstiloGestion.estado();

    public PanelConfiguracionVehiculo(VehicleManager vehicleManager, ConfiguracionManager configManager) {
        this.vehicleManager = vehicleManager;
        this.configManager = configManager;
    }

    public BorderPane construirVista() {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(24));
        raiz.setStyle(EstiloGestion.FONDO);
        raiz.setTop(EstiloGestion.titulo("Configurar un vehiculo"));
        BorderPane.setMargin(raiz.getTop(), new Insets(0, 0, 20, 0));

        comboVehiculo.getItems().setAll(vehicleManager.listar());
        comboVehiculo.setConverter(convertidorVehiculo());
        comboVehiculo.setPrefWidth(340);
        comboVehiculo.setOnAction(e -> cargarConfiguracionActual());

        comboModo.getItems().setAll(ModoConduccion.values());
        comboAero.getItems().setAll(CargaAerodinamica.values());
        comboPresion.getItems().setAll(PresionNeumaticos.values());
        comboCombustible.getItems().setAll(EstrategiaCombustible.values());
        for (ComboBox<?> combo : new ComboBox<?>[]{comboModo, comboAero, comboPresion, comboCombustible}) {
            combo.setPrefWidth(220);
        }

        GridPane grilla = new GridPane();
        grilla.setHgap(20);
        grilla.setVgap(8);
        grilla.add(EstiloGestion.etiquetaCampo("Vehiculo"), 0, 0);
        grilla.add(comboVehiculo, 0, 1, 2, 1);
        grilla.add(EstiloGestion.etiquetaCampo("Modo de conduccion"), 0, 2);
        grilla.add(comboModo, 0, 3);
        grilla.add(EstiloGestion.etiquetaCampo("Carga aerodinamica"), 1, 2);
        grilla.add(comboAero, 1, 3);
        grilla.add(EstiloGestion.etiquetaCampo("Presion de neumaticos"), 0, 4);
        grilla.add(comboPresion, 0, 5);
        grilla.add(EstiloGestion.etiquetaCampo("Estrategia de combustible"), 1, 4);
        grilla.add(comboCombustible, 1, 5);

        var botonGuardar = EstiloGestion.botonPrimario("Guardar configuracion");
        botonGuardar.setOnAction(e -> guardar());
        HBox botones = new HBox(10, botonGuardar);
        botones.setAlignment(Pos.CENTER_LEFT);

        VBox contenedor = new VBox(14, grilla, EstiloGestion.etiquetaCampo("Configuracion actual:"), actual, botones, estado);
        contenedor.setPadding(new Insets(18));
        contenedor.setStyle(EstiloGestion.TARJETA);
        contenedor.setMaxWidth(560);
        raiz.setCenter(contenedor);
        BorderPane.setAlignment(contenedor, Pos.TOP_LEFT);

        if (!comboVehiculo.getItems().isEmpty()) {
            comboVehiculo.getSelectionModel().selectFirst();
            cargarConfiguracionActual();
        }
        return raiz;
    }

    private StringConverter<Vehicle> convertidorVehiculo() {
        return new StringConverter<>() {
            @Override
            public String toString(Vehicle v) {
                return v == null ? "" : "#" + v.getId_vehiculo() + " " + v.getModelo() + " (" + v.getEquipo() + ")";
            }

            @Override
            public Vehicle fromString(String s) {
                return null;
            }
        };
    }

    private void cargarConfiguracionActual() {
        Vehicle seleccionado = comboVehiculo.getValue();
        if (seleccionado == null) {
            return;
        }
        ConfiguracionVehiculo config = configManager.obtener(seleccionado.getId_vehiculo());
        comboModo.setValue(config.getModo());
        comboAero.setValue(config.getCargaAerodinamica());
        comboPresion.setValue(config.getPresionNeumaticos());
        comboCombustible.setValue(config.getEstrategiaCombustible());
        actual.setText(config.toString());
    }

    private void guardar() {
        Vehicle seleccionado = comboVehiculo.getValue();
        if (seleccionado == null) {
            EstiloGestion.mostrarError(estado, "Elige un vehiculo primero.");
            return;
        }
        ConfiguracionVehiculo nueva = new ConfiguracionVehiculo(seleccionado.getId_vehiculo(),
                comboModo.getValue(), comboAero.getValue(), comboPresion.getValue(), comboCombustible.getValue());
        configManager.guardar(nueva);
        actual.setText(nueva.toString());
        EstiloGestion.mostrarInfo(estado, "Configuracion guardada para el vehiculo #" + seleccionado.getId_vehiculo() + ".");
    }
}
