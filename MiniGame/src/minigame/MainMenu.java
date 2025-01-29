package minigame;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear el contenidor principal
        Pane root = new Pane();

        // Llamar al método para reproducir la música de fondo
        Sound.playBackgroundMusic();

        // Configurar l'imatge de fons
        Image backgroundImage = new Image("resources/Atlas.png");
        ImageView backgroundImageView = new ImageView(backgroundImage);

        // Configuram contenidor VBox
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);

        // Configurar botons
        Button startButton = createTransparentButton("Start Game");
        Button exitButton = createTransparentButton("Exit Game");

        // Ficar botons al contenidor
        container.getChildren().addAll(startButton, exitButton);

        // Ficar la imatge de fons i el contenidor de botons al contenedor principal
        root.getChildren().addAll(backgroundImageView, container);

        // Ajustar posició del contenidor a la part inferior central
        container.layoutXProperty().bind(root.widthProperty().subtract(container.widthProperty()).divide(2));
        container.layoutYProperty().bind(root.heightProperty().subtract(container.heightProperty()));

        // Configurar accions de los botones
        
        // Botó Start Game
        startButton.setOnAction(event -> {
            MiniGame miniGame = new MiniGame();
            miniGame.start(new Stage());
            primaryStage.close();
            
        });

        // Botó Exit
        exitButton.setOnAction(event -> primaryStage.close());

        // Configurar l'escena
        Scene scene = new Scene(root, 800, 600);

        // Ajustar l'imatge a la finestre
        backgroundImageView.fitWidthProperty().bind(scene.widthProperty());
        backgroundImageView.fitHeightProperty().bind(scene.heightProperty());

        // Configurar l'escenari principal
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Configurar l'imatge de la finestra
        primaryStage.getIcons().add(new Image("resources/IconPlayer.png"));
    }

    // Mètode per crear botones transparentes
    private Button createTransparentButton(String buttonText) {
        Button button = new Button(buttonText);
        button.setStyle("-fx-background-color: transparent; "
                      + "-fx-border-color: transparent;");
        button.setTextFill(Color.WHITE);
        return button;
    }

    // Run
    public static void main(String[] args) {
        launch(args);
    }
}
