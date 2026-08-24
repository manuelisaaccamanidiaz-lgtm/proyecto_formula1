package com.formula1.javafx;

import java.io.InputStream;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Pantalla de bienvenida ("splash screen") que se muestra al abrir la
 * aplicacion, antes del menu principal: el logo de F1 arranca chico y crece
 * de forma lenta y fluida (como si estuviera cargando), y debajo aparece el
 * texto de bienvenida. Al terminar la animacion, se llama automaticamente a
 * {@code onCompletado} para que quien la use pase a la siguiente pantalla
 * (normalmente el menu principal).
 *
 * El logo se carga como recurso del classpath
 * ({@code /com/formula1/images/logo-f1.png}, ver src/main/resources), no
 * desde una ruta de disco, para que funcione igual sin importar desde donde
 * se ejecute la aplicacion.
 */
public class SplashScreenView {

    private static final String RUTA_LOGO = "/com/formula1/images/logo-f1.png";

    /** Construye la vista y arranca la animacion. Llama a onCompletado una sola vez, al terminar. */
    public StackPane construirVista(Runnable onCompletado) {
        StackPane raiz = new StackPane();
        raiz.setStyle("-fx-background-color: #0b0e14;");

        ImageView logo = new ImageView(cargarLogo());
        logo.setPreserveRatio(true);
        logo.setFitWidth(420);
        logo.setOpacity(0.0);
        // Arranca bien chico (10% de su tamano final) para que la animacion
        // de "crecer" se note desde el principio.
        logo.setScaleX(0.1);
        logo.setScaleY(0.1);

        Label bienvenida = new Label("WELCOME TO FORMULA 1");
        bienvenida.setFont(Font.font("System", FontWeight.BOLD, 22));
        bienvenida.setTextFill(Color.web("#f0f6fc"));
        bienvenida.setOpacity(0.0);

        Label listo = new Label("READY TO RACE");
        listo.setFont(Font.font("System", FontWeight.BOLD, 14));
        listo.setTextFill(Color.web("#d32026"));
        listo.setOpacity(0.0);

        VBox textos = new VBox(6, bienvenida, listo);
        textos.setAlignment(Pos.CENTER);

        VBox contenido = new VBox(28, logo, textos);
        contenido.setAlignment(Pos.CENTER);
        raiz.getChildren().add(contenido);
        StackPane.setAlignment(contenido, Pos.CENTER);

        reproducirAnimacion(logo, bienvenida, listo, onCompletado);
        return raiz;
    }

    private void reproducirAnimacion(ImageView logo, Label bienvenida, Label listo, Runnable onCompletado) {
        // 1) El logo aparece (fade in) mientras crece lento y fluido desde 10% a 100%.
        FadeTransition apareceLogo = new FadeTransition(Duration.seconds(1.0), logo);
        apareceLogo.setFromValue(0.0);
        apareceLogo.setToValue(1.0);

        ScaleTransition creceLogo = new ScaleTransition(Duration.seconds(2.6), logo);
        creceLogo.setFromX(0.1);
        creceLogo.setFromY(0.1);
        creceLogo.setToX(1.0);
        creceLogo.setToY(1.0);
        // EASE_OUT: arranca mas rapido y termina desacelerando suavemente,
        // como una carga que se va asentando (no un rebote brusco).
        creceLogo.setInterpolator(Interpolator.EASE_OUT);

        apareceLogo.play();
        creceLogo.play();

        // 2) El texto de bienvenida aparece despues, cuando el logo ya casi termino de crecer.
        FadeTransition apareceBienvenida = new FadeTransition(Duration.seconds(0.9), bienvenida);
        apareceBienvenida.setFromValue(0.0);
        apareceBienvenida.setToValue(1.0);

        FadeTransition apareceListo = new FadeTransition(Duration.seconds(0.9), listo);
        apareceListo.setFromValue(0.0);
        apareceListo.setToValue(1.0);

        PauseTransition esperaAntesDelTexto = new PauseTransition(Duration.seconds(1.9));
        PauseTransition esperaFinal = new PauseTransition(Duration.seconds(0.9));

        SequentialTransition secuenciaTexto = new SequentialTransition(
                esperaAntesDelTexto, apareceBienvenida, apareceListo, esperaFinal);

        if (onCompletado != null) {
            secuenciaTexto.setOnFinished(e -> onCompletado.run());
        }
        secuenciaTexto.play();
    }

    private Image cargarLogo() {
        InputStream flujo = getClass().getResourceAsStream(RUTA_LOGO);
        if (flujo == null) {
            throw new IllegalStateException("No se encontro el recurso " + RUTA_LOGO
                    + " (deberia estar en src/main/resources" + RUTA_LOGO + ")");
        }
        return new Image(flujo);
    }
}
