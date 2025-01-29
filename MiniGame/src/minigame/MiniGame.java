//@author TJ

package minigame;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;

// Clase principal MiniGame
public class MiniGame extends Application {
    private Pane root;
    private Player player;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Projectile> playerProjectiles = new ArrayList<>();
    private List<Projectile> enemyProjectiles = new ArrayList<>();
    private int currentLevel = 0;
    private CollisionManager collisionManager;
    private Timeline levelTimer;
    private boolean canLevelUp = true;
    private Text healthText;
    private Stage primaryStage;


    // Inici del joc
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeGame(primaryStage);
        setMouseAndKeyEvents();
        startGameLoop();
        Sound.playBattleMusic();
        if (player.isAlive()) {
            showHealthText();
        }
    }

    // Inicialitzam el joc
    private void initializeGame(Stage primaryStage) {
        initializeStage(primaryStage);
        initializePlayer(primaryStage);
        collisionManager = new CollisionManager();
    }

    // Inicialitzam l'escena y l'escenario
    private void initializeStage(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, 800, 600);
        configureStage(primaryStage, scene);
    }

    // Configurar l'escena i l'escenari
    private void configureStage(Stage primaryStage, Scene scene) {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setFullScreen(true);
        configureIcon(primaryStage);
        configureTitle(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Configurar l'imatge del joc
    private void configureIcon(Stage primaryStage) {
        File file = new File("src/resources/IconPlayer.png");
        Image icon = new Image(file.toURI().toString());
        primaryStage.getIcons().add(icon);
    }

    // Configurar el titol del joc
    private void configureTitle(Stage primaryStage) {
        primaryStage.setTitle("Hack MiniGame");
    }

    // Inicialitzar el player
    private void initializePlayer(Stage primaryStage) {
        player = new Player(root, 500, primaryStage.getWidth() / 2, primaryStage.getHeight() / 2 + 200);
    }

    // Configuram ús de teclat i ratolí
    private void setMouseAndKeyEvents() {
        root.setOnMouseMoved(event -> player.rotatePlayer(event));
        root.setOnMouseClicked(event -> handleMouseClick(event));
    }

    // Ús de ratolí
    private void handleMouseClick(javafx.scene.input.MouseEvent event) {
        // Click esquerra
        if (event.getButton() == MouseButton.PRIMARY) {
            player.shootProjectile(root, playerProjectiles);
        // Click dret
        } else if (event.getButton() == MouseButton.SECONDARY) {
            player.teleportToMouse(root, event);
        }
    }

    // Iniciar el bucle de joc
    private void startGameLoop() {
        root.requestFocus();
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGameElements();
                checkGameStatus();
            }
        }.start();
    }

    // Actualitzar elements del joc
    private void updateGameElements() {
        
        player.update(root);

        for (Enemy enemy : new ArrayList<>(enemies)) {
            enemy.update(root);
            for (Projectile enemyProjectile : enemy.getEnemyProjectiles()) {
                enemyProjectile.update(root);
            }
        }

        for (Projectile playerProjectile : playerProjectiles) {
            playerProjectile.update(root);
        }

        // Comprovar colisions
        collisionManager.checkCollisions(player, enemies, playerProjectiles, enemyProjectiles, root);
        
        // Verificar si el player está mort
        if (!player.isAlive()) {
            onPlayerDeath();
        }
        
        // Actualitzar el texte de salud
        updateHealthText();
    }

    // Verificam estat del joc
    private void checkGameStatus() {
        if (enemies.isEmpty()) {
            startNewLevel();
        }
    }

    // Iniciam un nou nivel
    private void startNewLevel() {
        if (canLevelUp && enemies.isEmpty()) {
            currentLevel++;
            int maxEnemies = currentLevel; // Número de enemic = nivell de joc

            // Mostrar informació del nivell durant la pausa
            launchPauseAfterNewLevel("Level: " + currentLevel, 3, maxEnemies);

            // Desactivar la possibilitad de pujar de nivell fins que passin 5 segundos
            canLevelUp = false;

            // Configurar temporizador per reactivar la posibilidad de pujar de nivell despres de 5 segons
            configureLevelTimer(maxEnemies);
        }
    }

    // Configuram temporitzador del nivell
    private void configureLevelTimer(int maxEnemies) {
        if (levelTimer != null) {
            levelTimer.stop();
        }

        levelTimer = new Timeline(new KeyFrame(Duration.seconds(5)));
        levelTimer.setOnFinished(event -> {
            // Reactivar la possibilitad de pujar de nivell
            canLevelUp = true;

        });
        levelTimer.play();
    }


    // Feim pausa despres d'iniciar un nou nivell
    private void launchPauseAfterNewLevel(String message, int durationSeconds, int maxEnemies) {
        Text text = configureLevelMessage(message);
        Duration pauseDuration = Duration.seconds(durationSeconds);
        Timeline pauseTransition = configurePauseTransition(text, pauseDuration, maxEnemies);
        pauseTransition.play();
    }

    // Configurar misatje de nivell
    private Text configureLevelMessage(String message) {
        Text text = new Text(message);
        text.setFont(new Font(30));
        text.setFill(Color.BLACK);
        text.setTranslateX((root.getWidth() - text.getLayoutBounds().getWidth()) / 2);
        text.setTranslateY(root.getHeight() / 2);
        root.getChildren().add(text);
        return text;
    }

    // Configuram transició de pausa
    private Timeline configurePauseTransition(Text text, Duration pauseDuration, int maxEnemies) {
        Timeline pauseTransition = new Timeline(new KeyFrame(pauseDuration));
        pauseTransition.setOnFinished(event -> {
            root.getChildren().remove(text);
            spawnEnemies(maxEnemies);
        });
        return pauseTransition;
    }

    // Spawnear enemics al mateix temps
    private void spawnEnemies(int maxEnemies) {
        for (int i = 0; i < maxEnemies; i++) {
            addEnemy(Math.random() * root.getWidth(), 100);
        }
    }

    // Afegir un enemic
    private void addEnemy(double x, double y) {
        Enemy enemy = new Enemy(root, x, y, enemies, enemyProjectiles, player);
        enemies.add(enemy);
    }
    
    // Mètode per mostrar la pantalla de reinici o salida
    private void showRestartOrExitScreen(Pane root, Stage primaryStage) {
        VBox gameOverBox = new VBox();
        gameOverBox.setAlignment(Pos.CENTER);
        Sound.stopEnemyShot();
        Sound.stopPlayerShot();
        
        // Texte "Game Over"
        Text gameOverText = new Text("Game Over");
        gameOverText.setFill(Color.RED);  
        gameOverText.setFont(new Font(40));  
        
        Button mainMenuButton = createTransparentButton("Return to Main Menu");
        Button exitButton = createTransparentButton("Exit Game");

        gameOverBox.getChildren().addAll(gameOverText, mainMenuButton, exitButton);

        root.getChildren().add(gameOverBox);

        // Possicionar el VBox en el centre de la pantalla
        gameOverBox.layoutXProperty().bind(root.widthProperty().subtract(gameOverBox.widthProperty()).divide(2));
        gameOverBox.layoutYProperty().bind(root.heightProperty().subtract(gameOverBox.heightProperty()).divide(2));

        // Configurar accions dels botons
        mainMenuButton.setOnAction(event -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.start(new Stage());
            primaryStage.close();
        });
        
        // Exit Game
        exitButton.setOnAction(event -> exitGame(primaryStage));
    }

    
    
    // Mètode per sortir del joc
    private void exitGame(Stage primaryStage) {
        
        primaryStage.close();
    }
    
    // Gestió de Game Over
    private void handleGameOver() {

        // Mostrar la pantalla de reset/exit
        showRestartOrExitScreen(root, primaryStage);

        // Eliminar al player de la pantalla
        player.removeFromPane(root);
    }

    // Botons transparents
    private Button createTransparentButton(String buttonText) {
        Button button = new Button(buttonText);
        button.setStyle("-fx-background-color: transparent; "
                      + "-fx-font : 32px \"san-serief\"; "
                      + "-fx-border-color: transparent;");
        button.setTextFill(Color.BLACK);
        return button;
    }
    
    // Mostrar Vida
    private void showHealthText() {
        if (player.isAlive()) {
            healthText = new Text("Health: " + player.getHealth());
            healthText.setFill(Color.BLACK);
            healthText.setX(10);
            healthText.setY(30);
            healthText.setStyle("-fx-font : 26px \"san-serief\"; ");
            root.getChildren().add(healthText);
        }      
    }
    
    private void updateHealthText() {
        healthText.setText("Health: " + player.getHealth());
    }
    
    public void onPlayerDeath() {
        handleGameOver();
    }

    // Run
    public static void main(String[] args) {
        launch(args);
    }
}