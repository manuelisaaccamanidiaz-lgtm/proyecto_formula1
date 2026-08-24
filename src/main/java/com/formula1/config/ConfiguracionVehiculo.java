package com.formula1.config;

public class ConfiguracionVehiculo {

    private int idVehiculo;
    private com.formula1.vehicles.ModoConduccion modo;
    private CargaAerodinamica cargaAerodinamica;
    private PresionNeumaticos presionNeumaticos;
    private EstrategiaCombustible estrategiaCombustible;

    public ConfiguracionVehiculo(int idVehiculo, com.formula1.vehicles.ModoConduccion modo,
                                  CargaAerodinamica cargaAerodinamica, PresionNeumaticos presionNeumaticos,
                                  EstrategiaCombustible estrategiaCombustible) {
        this.idVehiculo = idVehiculo;
        this.modo = modo;
        this.cargaAerodinamica = cargaAerodinamica;
        this.presionNeumaticos = presionNeumaticos;
        this.estrategiaCombustible = estrategiaCombustible;
    }

    /** Configuracion neutra usada cuando un vehiculo aun no ha sido configurado. */
    public static ConfiguracionVehiculo porDefecto(int idVehiculo) {
        return new ConfiguracionVehiculo(idVehiculo, com.formula1.vehicles.ModoConduccion.NORMAL,
                CargaAerodinamica.MEDIA, PresionNeumaticos.ESTANDAR, EstrategiaCombustible.BALANCEADA);
    }

    public int getIdVehiculo() {
        return idVehiculo;
    }

    public com.formula1.vehicles.ModoConduccion getModo() {
        return modo;
    }

    public CargaAerodinamica getCargaAerodinamica() {
        return cargaAerodinamica;
    }

    public PresionNeumaticos getPresionNeumaticos() {
        return presionNeumaticos;
    }

    public EstrategiaCombustible getEstrategiaCombustible() {
        return estrategiaCombustible;
    }

    @Override
    public String toString() {
        return String.format("Modo: %s | Carga aero: %s | Presion neumaticos: %s | Combustible: %s",
                modo, cargaAerodinamica, presionNeumaticos, estrategiaCombustible);
    }
}
