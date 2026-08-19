# Simulador de Formula 1

Proyecto en Java que combina:

1. Una **aplicacion de consola** (`Main.java`) para administrar circuitos,
   pilotos, equipos y vehiculos de F1, configurar el reglaje de cada auto
   y simular sesiones de clasificacion con clima aleatorio.
2. Una **visualizacion JavaFX** (`RaceTrackApp.java`) que muestra una
   pista ovalada con los 10 autos moviendose en tiempo real segun su
   velocidad maxima.

Ambos modulos comparten los mismos modelos de datos (`Pilot`, `Vehicle`,
`Team`, `Circuit`, `Result`) pero son puntos de entrada independientes:
puedes compilar y correr uno sin el otro.

---

## Indice

- [Que resuelve el proyecto](#que-resuelve-el-proyecto)
- [Arquitectura y patrones de diseno](#arquitectura-y-patrones-de-diseno)
- [Estructura de paquetes](#estructura-de-paquetes)
- [Modulo 1: consola (Main.java)](#modulo-1-consola-mainjava)
- [Modulo 2: pista JavaFX (RaceTrackApp.java)](#modulo-2-pista-javafx-racetrackappjava)
- [Como compilar y ejecutar](#como-compilar-y-ejecutar)
- [Datos iniciales cargados](#datos-iniciales-cargados)
- [Estado de verificacion / limitaciones conocidas](#estado-de-verificacion--limitaciones-conocidas)
- [Posibles siguientes pasos](#posibles-siguientes-pasos)

---

## Que resuelve el proyecto

Simula el aspecto deportivo de una temporada de Formula 1 a nivel
educativo/demostrativo:

- **Gestion de datos** (CRUD) de circuitos, pilotos, equipos y vehiculos.
- **Configuracion de reglaje** por vehiculo: modo de conduccion, carga
  aerodinamica, presion de neumaticos y estrategia de combustible.
- **Simulacion de clasificacion**: genera un clima aleatorio, calcula un
  tiempo de vuelta por piloto (combinando vehiculo + habilidad + reglaje
  + clima + una variacion aleatoria) y produce un ranking tipo "pole
  position".
- **Historial**: guarda cada sesion simulada para consultarla despues.
- **Records historicos**: muestra el mejor tiempo de vuelta real de cada
  circuito.
- **Visualizacion**: una pista animada donde se ve, literalmente, por
  que un auto mas rapido le saca ventaja a uno mas lento.

---

## Arquitectura y patrones de diseno

- **Factory Method**: cada entidad (`Circuit`, `Pilot`, `Team`,
  `Vehicle`, `Result`) se crea a traves de una interfaz `XxxFactory` con
  implementaciones concretas (`F1CircuitFactory`, `RedBullFactory`,
  `FerrariTeamFactory`, etc.). Esto separa "como se construye un dato"
  de "donde se usa", y permite agregar nuevos equipos/circuitos sin
  tocar el codigo que ya los consume.
- **Manager / Repository en memoria**: cada entidad tiene un
  `XxxManager` (`CircuitManager`, `PilotManager`, `TeamManager`,
  `VehicleManager`) que guarda los datos en un `Map<Integer, T>` (id ->
  objeto) y expone operaciones CRUD + busqueda. Es la capa de
  "persistencia temporal" que pide el documento de requerimientos.
- **Separacion Modelo / Config / Simulacion**:
  - `circuits`, `pilots`, `teams`, `vehicles`, `results` -> datos puros
    (POJOs con getters/setters).
  - `config` -> el reglaje que el usuario elige para un vehiculo.
  - `simulation` -> la logica que combina modelo + config + clima para
    producir un resultado.
- **Independencia entre modulos**: `RaceTrackApp.java` (JavaFX) no
  depende de los managers ni de la simulacion; crea sus propios datos de
  ejemplo usando las mismas factories de vehiculo. Esto permite abrir la
  vista grafica sin pasar por todo el flujo de consola.

---

## Estructura de paquetes

```
com/formula1/
│
├── Main.java                     Punto de entrada de la app de consola
│
├── circuits/    Circuit.java
├── pilots/      Pilot.java
├── teams/       Team.java
├── vehicles/    Vehicle.java, ModoConduccion.java
├── results/     Result.java
│                └─ Modelos de datos (POJOs)
│
├── factories/   CircuitFactory, PilotFactory, ResultFactory,
│                TeamFactory, VehicleFactory
│                └─ Interfaces Factory Method, una por entidad
│
├── circuitfactories/  F1CircuitFactory.java        (7 circuitos reales)
├── pilotfactories/    F1PilotFactory.java           (20 pilotos reales)
├── resultfactories/   F1ResultFactory.java          (records por circuito)
├── teamfactories/     RedBullTeamFactory.java
│                      MercedesTeamFactory.java
│                      FerrariTeamFactory.java
├── vehiclefactories/  MercedesFactory.java, RedBullFactory.java (originales)
│                      FerrariFactory, McLarenFactory, AstonMartinFactory,
│                      AlpineFactory, WilliamsFactory, AlfaRomeoFactory,
│                      AlphaTauriFactory, HaasFactory  (agregadas para
│                      completar los 10 equipos de la pista JavaFX)
│
├── managers/    CircuitManager, PilotManager, TeamManager, VehicleManager
│                └─ CRUD + busqueda sobre Map<Integer, T>
│
├── config/      ModoConduccion (vive en vehicles/), CargaAerodinamica,
│                PresionNeumaticos, EstrategiaCombustible,
│                ConfiguracionVehiculo, ConfiguracionManager
│                └─ Reglaje configurable por vehiculo
│
├── simulation/  Clima.java                (clima aleatorio de la sesion)
│                Participante.java         (piloto + vehiculo + config)
│                ResultadoClasificacion.java (posicion + tiempo de vuelta)
│                SesionClasificacion.java  (una sesion guardada)
│                HistorialClasificacion.java (lista de sesiones)
│                SimuladorClasificacion.java (motor de calculo)
│
└── javafx/      RaceCar.java, RaceTrackApp.java
                 └─ Visualizacion animada de la pista (modulo independiente)
```

45 archivos `.java` en total.

---

## Modulo 1: consola (`Main.java`)

Menu principal:

```
1. Gestionar circuitos
2. Gestionar pilotos
3. Gestionar equipos
4. Gestionar vehiculos
5. Configurar un vehiculo
6. Simular clasificacion
7. Ver historial de clasificaciones
8. Ver records de vuelta por circuito
0. Salir
```

- **Opciones 1-4**: CRUD completo (listar, agregar, editar, eliminar,
  buscar por texto) para circuitos, pilotos, equipos y vehiculos. El
  menu de vehiculos ademas permite **asignar un piloto** a un vehiculo
  y **comparar** varios vehiculos lado a lado.
- **Opcion 5**: elige modo de conduccion, carga aerodinamica, presion de
  neumaticos y estrategia de combustible para un vehiculo puntual. Se
  guarda en `ConfiguracionManager` y se reutiliza en la simulacion.
- **Opcion 6**: elige un circuito, genera un clima aleatorio
  (`SECO` / `LLUVIOSO` / `EXTREMO`), arma la lista de participantes (los
  pilotos que tienen un vehiculo asignado) y corre
  `SimuladorClasificacion`, que calcula el tiempo de vuelta de cada uno
  y los ordena de mejor a peor. El resultado se guarda automaticamente
  en el historial.
- **Opcion 7**: lista todas las sesiones de clasificacion ya simuladas.
- **Opcion 8**: muestra el record de vuelta historico de cada uno de los
  7 circuitos.

### Como se calcula el tiempo de vuelta

`SimuladorClasificacion` parte de un tiempo base (longitud del circuito
/ velocidad maxima del vehiculo) y lo ajusta multiplicando por factores:

| Factor              | Origen                                      |
|----------------------|----------------------------------------------|
| Habilidad del piloto | `Pilot.habilidad` (1-100)                    |
| Modo de conduccion   | `ModoConduccion` (agresiva acelera, ahorro retrasa) |
| Carga aerodinamica   | `CargaAerodinamica` (alta = mas agarre, un poco mas lenta) |
| Presion de neumaticos| `PresionNeumaticos`                          |
| Estrategia combustible| `EstrategiaCombustible`                     |
| Clima                | `Clima` (lluvioso/extremo penalizan el tiempo) |
| Variacion aleatoria  | +/- 2%, para que no de siempre el mismo resultado |

---

## Modulo 2: pista JavaFX (`RaceTrackApp.java`)

Ventana independiente (no pasa por el menu de consola) que muestra:

- Una pista ovalada (asfalto sobre cesped, con linea de meta).
- Los 10 pilotos "lider" de cada equipo (Verstappen, Hamilton, Leclerc,
  Norris, Alonso, Ocon, Bottas, Magnussen, Tsunoda, Albon), cada uno en
  su auto con el color de su equipo.
- Cada auto gira a una velocidad angular **proporcional a la velocidad
  maxima real de su vehiculo** (Red Bull 360 km/h da la vuelta mas
  rapido que el Haas a 344 km/h, por ejemplo).
- Panel lateral de **clasificacion en vivo**, que reordena a los pilotos
  segun la distancia recorrida.
- Controles: Pausar/Reanudar, Reiniciar carrera, slider de velocidad de
  simulacion (x0.25 a x3).

Internamente usa las mismas `VehicleFactory` (`RedBullFactory`,
`FerrariFactory`, etc.) que el modulo de consola, pero construye sus
propios `Pilot` y no toca los managers ni la simulacion de clasificacion.

---

## Como compilar y ejecutar

### Consola (`Main.java`) -- no necesita nada extra, solo JDK

```bash
javac -d out $(find com -name "*.java" ! -path "*/javafx/*")
java -cp out com.formula1.Main
```

### Pista JavaFX (`RaceTrackApp.java`) -- necesita el SDK de JavaFX

JavaFX no viene incluido en el JDK desde Java 11 en adelante, hay que
agregarlo aparte.

**Opcion A - Maven (recomendado).** Agregar a `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.2</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-maven-plugin</artifactId>
            <version>0.0.8</version>
            <configuration>
                <mainClass>com.formula1.javafx.RaceTrackApp</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```

```bash
mvn javafx:run
```

**Opcion B - Linea de comandos.** Descargar el SDK desde
https://gluonhq.com/products/javafx/ y:

```bash
javac --module-path "RUTA/javafx-sdk-21/lib" --add-modules javafx.controls \
      -d out $(find com -name "*.java")
java --module-path "RUTA/javafx-sdk-21/lib" --add-modules javafx.controls \
     -cp out com.formula1.javafx.RaceTrackApp
```

---

## Datos iniciales cargados

Al arrancar `Main.java`, `cargarDatosIniciales()` precarga (todos
tomados del documento de requerimientos del proyecto):

- **7 circuitos**: Monaco, Silverstone, Spa-Francorchamps, Monza,
  Interlagos, Yas Marina, Suzuka -- cada uno con pais, longitud, vueltas
  y record de vuelta historico (tiempo, piloto y ano).
- **20 pilotos**: 2 por cada uno de los 10 equipos (Red Bull, Mercedes,
  Ferrari, McLaren, Aston Martin, Alpine, Alfa Romeo, Haas, AlphaTauri,
  Williams), con rol (Lider/Escudero) y una habilidad estimada (1-100).
- **3 equipos** dados de alta en el `TeamManager`: Red Bull Racing
  (Austria, motor Honda), Mercedes-AMG Petronas (Alemania, motor
  Mercedes) y Ferrari (Italia, motor Ferrari) -- son los unicos 3 que el
  documento original definia con detalle completo.
- **2 vehiculos**: RB20 (Red Bull) y W15 (Mercedes).
- **7 records de vuelta historicos**, uno por circuito, coherentes con
  los datos de circuitos.

El resto de equipos/vehiculos (McLaren, Ferrari como vehiculo, etc.) se
pueden dar de alta a mano desde el menu 3 y 4, o usando las factories
adicionales que ya existen en `vehiclefactories/` para la pista JavaFX.

---

## Estado de verificacion / limitaciones conocidas

Este entorno no tiene `javac` disponible (solo un JRE) y el acceso a
red esta bloqueado, asi que **no se pudo compilar el proyecto de forma
real** para confirmar que compila sin errores.

Lo que si se hizo como verificacion:

- Se extrajeron con un script todas las llamadas a metodos, imports y
  tipos que usa `Main.java`, y se confirmo una por una que cada clase
  creada las implementa con el nombre, tipo y orden de parametros
  exactos (incluyendo el metodo `createtTeam` con el typo original de
  `TeamFactory`).
- Se verifico el balance de llaves y parentesis de los 45 archivos.
- Se releyo `Main.java` completo de punta a punta contrastando cada
  bloque contra las clases correspondientes.

Si al compilar en tu maquina aparece algun error, pega el mensaje
completo (con numero de linea) y se corrige de inmediato.

---

## Posibles siguientes pasos

- Agregar factories de equipo para los 7 equipos restantes (McLaren,
  Aston Martin, Alpine, Alfa Romeo, Haas, AlphaTauri, Williams), hoy
  solo dados de alta a mano o via `vehiclefactories/`.
- Persistir los datos en archivo o base de datos (hoy todo vive en
  memoria mientras el programa esta corriendo).
- Conectar `RaceTrackApp.java` con una `SesionClasificacion` real del
  historial, en vez de generar sus propios pilotos de ejemplo.
- Tests unitarios para `SimuladorClasificacion` (validar que el ranking
  responde de forma consistente a cambios de habilidad/reglaje/clima).
