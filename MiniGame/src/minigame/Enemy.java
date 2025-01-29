//@author TJ

package minigame;

import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Clase Enemy
public class Enemy {

    private Player player;
    private List<Enemy> enemies;

    private Circle circle;
    private Circle shooter;

    private Random random = new Random();
    private double health = 100;

    static final double ENEMY_RADIUS = 30.0;
    private static final double SHOOTING_RADIUS = 20.0;
    private static final int SHOOTING_PATTERN_PROJECTILES = 10;
    static final double MOVEMENT_SPEED = 0.5;
    static final double MAX_MOVEMENT_SPEED = 1.0;

    private List<Projectile> enemyProjectiles = new ArrayList<>();
    private List<Projectile> playerProjectiles = new ArrayList<>();

    private boolean isAlive = true;
    private boolean isMoving = false;
    private boolean isShooting = false;
    
    
    private ShootingPattern shootingPattern;
    private MovementPattern movementPattern;
    private CollisionManager collisionManager;

    // Variables per la rotació
    private long lastShotTime = 0;
    private double rotationAngle = 5.0;

    

    // Constructor de Enemy
    public Enemy(Pane root, double x, double y, List<Enemy> enemies, List<Projectile> enemyProjectiles, Player player) {
        this.enemies = enemies;
        this.player = player;

        this.movementPattern = new MovementPattern();
        this.shootingPattern = new ShootingPattern();

        createEnemy(root, x, y);
        rotateEnemy();
        applyRandomPatterns(root);
        
    }


    // Creació de Enemy
    private void createEnemy(Pane root, double x, double y) {
        
        circle = new Circle(ENEMY_RADIUS, Color.BLACK);
        circle.setTranslateX(x);
        circle.setTranslateY(y);

        shooter = new Circle(ENEMY_RADIUS / 4.0, Color.PURPLE);
        shooter.centerXProperty().bind(circle.translateXProperty());
        shooter.centerYProperty().bind(circle.translateYProperty());

        root.getChildren().addAll(circle, shooter);
        
    }

    // Actualitzar posició de Enemy
    void updatePosition(double deltaX, double deltaY, Pane root) {
        circle.setTranslateX(circle.getTranslateX() + deltaX);
        circle.setTranslateY(circle.getTranslateY() + deltaY);

        double maxX = root.getWidth() - ENEMY_RADIUS;
        double maxY = root.getHeight() - ENEMY_RADIUS;

        circle.setTranslateX(Math.min(maxX, Math.max(circle.getTranslateX(), ENEMY_RADIUS)));
        circle.setTranslateY(Math.min(maxY, Math.max(circle.getTranslateY(), ENEMY_RADIUS)));
    }
    

    
    // Mètode per el movimient i tir intercalats
    public void applyRandomPatterns(Pane root) {
        new AnimationTimer() {
            private long lastActionTime = 0;

            @Override
            public void handle(long now) {
                if (now - lastActionTime >= 1_000_000_000) { // Cambiar acció cada segon
                    lastActionTime = now;

                    if (isAlive) {
                        if (isMoving) {
                            movementPattern.executeRandomMovementPattern(root, Enemy.this);
                        } else {
                            
                            shootingPattern.executeRandomAttackPattern(root, Enemy.this); Sound.enemyShotSound();
                            
                        }

                        isMoving = !isMoving; // Alternar entre movimiento y disparo
                    }
                }

                // Verificar col·lisions
                collisionManager.checkEnemyProjectileCollisionsWithBounds(enemyProjectiles, root);
                collisionManager.checkCollisionEnemyProjectileVsPlayer(enemies, player, root);

                // Detener el bucle después de 10 segundos o si el enemigo muere
                if (now - lastActionTime >= 10_000_000_000L || !isAlive) {
                    stop(); // Stop AnimationTimer

                    // Eliminar proyectiles al morir
                    removeProjectiles(root);
                }
            }
        }.start();
    }

    // Mètode per eliminar projectiles al morir del enemy
    private void removeProjectiles(Pane root) {
        for (Projectile projectile : enemyProjectiles) {
            Particle.createCollisionParticles(root, projectile.getProjectile().getTranslateX(), projectile.getProjectile().getTranslateY());
            root.getChildren().remove(projectile.getProjectile());
        }
        enemyProjectiles.clear();
    }


    // Mètode per rotar l'Enemy
    private void rotateEnemy() {
        circle.setRotate(circle.getRotate() + rotationAngle);
    }

    public void update(Pane root) {

    }

    // Getters y Setters
    public Player getPlayer() {
        return player;
    }

    
    public double getShootingRadius() {
        return SHOOTING_RADIUS;
    }


    public double getTranslateX() {
        return circle.getTranslateX();
    }

    public double getTranslateY() {
        return circle.getTranslateY();
    }

    public void removeFromPane(Pane root) {
        root.getChildren().removeAll(circle, shooter);
    }

    public List<Projectile> getEnemyProjectiles() {
        return enemyProjectiles;
    }

    public Circle getEnemy() {
        return circle;
    }

    public Circle getShooter() {
        return shooter;
    }

    public double getHealth() {
        return health;
    }

    public boolean isAlive() {
        return health > 0;
    }

    // Gestió de col·lisió
    public void handleCollision(Pane root) {
        decreaseHealth(50, root);

        if (!isAlive()) {
            handleDeath(root);
        }
    }
    
    

    // Gestió del efecte de col·lisió
    public void handleCollisionEffects(Pane root) {
        Particle.createCollisionParticles(root, circle.getTranslateX(), circle.getTranslateY());
    }

    // Reduïr vida
    public void decreaseHealth(double amount, Pane root) {
        health -= amount;
        if (health <= 0) {
            handleDeath(root);
            
        }
    }

    // Gestió de la mort del enemy
    private void handleDeath(Pane root) {

        // Efecte de partícules
        for (int i = 0; i < 50; i++) {
            Particle.createCollisionParticles(root, getTranslateX(), getTranslateY());
        }

        // Animació de mort
        Particle.createDeathAnimation(root, getTranslateX(), getTranslateY(), 150);

        // Eliminar projectiles quan mor
        for (Projectile projectile : enemyProjectiles) {
            Particle.createCollisionParticles(root, projectile.getProjectile().getTranslateX(), projectile.getProjectile().getTranslateY());
            root.getChildren().remove(projectile.getProjectile());
        }

        // Netejam la llista de projectiles del enemy
        enemyProjectiles.clear();

        // Eliminar l'enemy de la pantalla
        removeFromPane(root);
        enemies.remove(this);
        
        
    }


    public Bounds getBoundsInParent() {
        return circle.getBoundsInParent();
    }
}
