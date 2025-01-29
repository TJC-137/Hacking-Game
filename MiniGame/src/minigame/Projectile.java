//@author TJ

package minigame;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


// Clase Projectile
public class Projectile {
    private static List<Projectile> enemyProjectiles = new ArrayList<>();
    private static List<Projectile> playerProjectiles = new ArrayList<>();
    
    private final Circle circle;
    private final double speed;
    private final double direction;
    
    
    //Constructor de Projectile
    public Projectile(double startX, double startY, double speed, double direction, Color color, double radius) {
        this.circle = new Circle(radius, color);
        this.circle.setTranslateX(startX);
        this.circle.setTranslateY(startY);
        this.speed = speed;
        this.direction = direction;   
    }

    // Moviment
    public void move() {     
        double deltaX = speed * Math.cos(Math.toRadians(direction));
        double deltaY = speed * Math.sin(Math.toRadians(direction));
        circle.setTranslateX(circle.getTranslateX() + deltaX);
        circle.setTranslateY(circle.getTranslateY() + deltaY);     
    }

    // Comprobar si està OutOfBounds
    private boolean isOutOfScreen(Pane root) {
        return circle.getTranslateX() < 0 || circle.getTranslateX() > root.getWidth() 
          ||   circle.getTranslateY() < 0 || circle.getTranslateY() > root.getHeight();
    }
    
    // Elimina el projectile s està OutOfBounds
    public void startAnimation(Pane root) {
        new AnimationTimer() {
            @Override
            public void handle(long now) {               
                move();                
                if (isOutOfScreen(root)) {
                    removeFromPane(root);
                    this.stop();
                }
            }
        }.start();
    }
    

    // Els projectils explotarán quan colisionin
    public void explode(Pane root, List<Projectile> enemyProjectiles) {
        Particle.createCollisionParticles(root, getProjectile().getTranslateX(), getProjectile().getTranslateY());       
        enemyProjectiles.remove(this);    
        root.getChildren().remove(getProjectile());
    }


    //Gestionar l'efecte de les colisions
    public void handleCollisionEffects(Pane root) {  
        Particle.createCollisionParticles(root, circle.getTranslateX(), circle.getTranslateY());
    }
    
    public void update(Pane root) {
        move();
    }

    public Bounds getBoundsInParent() {
        return circle.getBoundsInParent();
    }
    
    public Circle getProjectile() {
        return circle;
    }
    
    public List<Projectile> getEnemyProjectiles() {
        return enemyProjectiles;
    }
    
    public void removeFromPane(Pane root) {
        root.getChildren().remove(circle);
    }
    
}
