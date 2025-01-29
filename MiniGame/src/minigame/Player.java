//@author TJ

package minigame;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Clase Player
public class Player {
    private Pane root;
    private Polygon player;
    private Circle shooter;

    private final double PLAYERSPEED = 7;
    private int health;

    private List<Projectile> playerProjectiles = new ArrayList<>();
    private static final double PROJECTILE_SPEED = 5.0;
    private static final Duration TELEPORT_DURATION = Duration.seconds(0.3);
 
    private boolean isDeadMessageShown = false;

    //Constructor de Player
    public Player(Pane root, int initialHealth, double initialX, double initialY) {
        this.root = root;
        this.health = initialHealth;
        createPlayer(root, initialX, initialY);
        setKeyEvents(root.getScene());
        setMouseEvents(root);
        
       
    }

    //Creació del Player
    private void createPlayer(Pane root, double initialX, double initialY) {

        // Coordenades de cada vertex per crear un Rombo
        double[] playerCoords = {-20, 0, 0, -15, 30, 0, 0, 15};
        
        player = new Polygon(playerCoords);
        player.setFill(Color.BLACK);
        player.setStroke(Color.WHITE);

        player.setTranslateX(initialX);
        player.setTranslateY(initialY);

        //Cercle blanc dins el poligon
        shooter = new Circle(3, Color.WHITE);
        double centerX = (player.getLayoutBounds().getMinX() + player.getLayoutBounds().getMaxX()) / 2;
        double centerY = (player.getLayoutBounds().getMinY() + player.getLayoutBounds().getMaxY()) / 2;
        double adjustmentX = 0;
        double adjustmentY = -2;

        shooter.centerXProperty().bind(player.translateXProperty().add(centerX + adjustmentX));
        shooter.centerYProperty().bind(player.translateYProperty().add(centerY + adjustmentY));
        shooter.rotateProperty().bind(player.rotateProperty());

        root.getChildren().addAll(player, shooter);
    }

    // Ús de teclat
    private void setKeyEvents(Scene scene) {
        Map<KeyCode, Boolean> keyStates = new HashMap<>();

        scene.setOnKeyPressed(event -> keyStates.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keyStates.put(event.getCode(), false));

        new AnimationTimer() {
            
            @Override
            public void handle(long now) {
                double deltaX = 0;
                double deltaY = 0;

                //Moviment cardinal
                if (keyStates.getOrDefault(KeyCode.W, false)) {
                    deltaY -= PLAYERSPEED;
                }
                if (keyStates.getOrDefault(KeyCode.S, false)) {
                    deltaY += PLAYERSPEED;
                }
                if (keyStates.getOrDefault(KeyCode.A, false)) {
                    deltaX -= PLAYERSPEED;
                }
                if (keyStates.getOrDefault(KeyCode.D, false)) {
                    deltaX += PLAYERSPEED;
                }

                // Moviment diagonal
                if ((keyStates.getOrDefault(KeyCode.W, false) && keyStates.getOrDefault(KeyCode.A, false)) ||
                        (keyStates.getOrDefault(KeyCode.W, false) && keyStates.getOrDefault(KeyCode.D, false)) ||
                        (keyStates.getOrDefault(KeyCode.S, false) && keyStates.getOrDefault(KeyCode.A, false)) ||
                        (keyStates.getOrDefault(KeyCode.S, false) && keyStates.getOrDefault(KeyCode.D, false))) {
                    deltaX /= Math.sqrt(2);
                    deltaY /= Math.sqrt(2);
                }

                player.setTranslateX(player.getTranslateX() + deltaX);
                player.setTranslateY(player.getTranslateY() + deltaY);
            }
        }.start();
    }

    // Ús de ratolí
    private void setMouseEvents(Pane root) {
        
        root.setOnMouseMoved(this::rotatePlayer);

        root.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) { //Click esquerra dispara
                shootProjectile(root, getPlayerProjectiles());
            } else if (event.getButton() == MouseButton.SECONDARY) { //Click dret teleport o propulsió
                teleportToMouse(root, event);
            }
        });
    }

    // Mecànica de teleportarse o propulsió
    public void teleportToMouse(Pane root, MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        TranslateTransition transition = new TranslateTransition(TELEPORT_DURATION, getPlayer());
        transition.setToX(mouseX);
        transition.setToY(mouseY);
        Particle.createDashEffect(root, getTranslateX(), getTranslateY());
        transition.play();
    }

    // Rotar el player en relació al cursor del ratolí
    void rotatePlayer(MouseEvent event) {
        double angle = Math.toDegrees(Math.atan2(event.getY() - player.getTranslateY(), event.getX() - player.getTranslateX()));
        player.setRotate(angle);
    }

    //Disparar projectiles
    public void shootProjectile(Pane root, List<Projectile> playerProjectiles) {
        
        //Posició inicial del projectile
        double centerX = (player.getLayoutBounds().getMinX() + player.getLayoutBounds().getMaxX()) / 2;
        double centerY = (player.getLayoutBounds().getMinY() + player.getLayoutBounds().getMaxY()) / 2;

        //Direcció del projectile
        double playerFrontX = player.getTranslateX() + centerX * Math.cos(Math.toRadians(player.getRotate()));
        double playerFrontY = player.getTranslateY() + centerY * Math.sin(Math.toRadians(player.getRotate()));

        Color projectileColor = Color.BLUE;
        double projectileRadius = 2.0;

        //Cream un objecte projectile
        Projectile playerProjectile = new Projectile(
                playerFrontX,
                playerFrontY,
                PROJECTILE_SPEED,
             player.getRotate(),
                projectileColor,
                projectileRadius
        );

        playerProjectiles.add(playerProjectile);
        root.getChildren().add(playerProjectile.getProjectile());
        Sound.playerShotSound();
        playerProjectile.startAnimation(root);
    }

    //Getters i setters
    public double getPlayerSpeed() {
        return PLAYERSPEED;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public List<Projectile> getPlayerProjectiles() {
        return playerProjectiles;
    }

    public double getTranslateX() {
        return player.getTranslateX();
    }

    public double getTranslateY() {
        return player.getTranslateY();
    }

    public Polygon getPlayer() {
        return player;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void removeFromPane(Pane root) {
        root.getChildren().remove(player);
    }

    public Bounds getBoundsInParent() {
        return player.getBoundsInParent();
    }

    //Gestió de l'efecte de colisió
    public void handleCollisionEffects(Pane root, Player player) {
        Particle.createCollisionParticles(root, getTranslateX(), getTranslateY());
    }
    
    
    
    //Gestió col·lisió
    public void handleCollision(Pane root, Player player) {
        if (isAlive()) {
            decreaseHealth(100, root);

            if (!isAlive()) {
                handleDeath(root);
            }
        }
    }

    //Reduir vida
    public void decreaseHealth(double amount, Pane root) {
        health -= amount;

        if (health <= 0) {
            handleDeath(root);
            
        }
    }

    //Gestió de mort del player
    public void handleDeath(Pane root) {
        if (!isDeadMessageShown) {
            System.out.println("Player Dead");

            //Efecte de particules
            for (int i = 0; i < 25; i++) {
                Particle.createDashEffect(root, getTranslateX(), getTranslateY());
                
                
            }
            
            // Mostram el misatge de Player Dead
            isDeadMessageShown = true;
        }
        
        // Aplicam la animació de mort
        Particle.createDeathAnimation(root, getTranslateX(), getTranslateY(), 150);

        // Eliminam al jugador de la pantalla
        removeFromPane(root);
        
        
        
    }


    public void update(Pane root) {
        
    }
}
