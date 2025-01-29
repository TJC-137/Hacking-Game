//@author TJ

package minigame;

import javafx.animation.AnimationTimer;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

// Clase Particle
public class Particle {

    private Circle circle;
    private double speedX;
    private double speedY;
    private double lifetime;

    // Constructor de Particle
    public Particle(double x, double y) {
        circle = new Circle(2, Color.GRAY);
        circle.setTranslateX(x);
        circle.setTranslateY(y);

        Random random = new Random();
        speedX = (random.nextDouble() - 0.5) * 4;
        speedY = (random.nextDouble() - 0.5) * 4;
        lifetime = 1.5 + random.nextDouble() * 1.5;
    }

    // Constructor de particle nomes per deathAnimation
    public Particle(double x, double y, double angle) {
        circle = new Circle(2, Color.GRAY);
        circle.setTranslateX(x);
        circle.setTranslateY(y);

        Random random = new Random();
        speedX = 4 * Math.cos(Math.toRadians(angle));
        speedY = 4 * Math.sin(Math.toRadians(angle));
        lifetime = 1.5 + random.nextDouble() * 1.5;
    }
    
    //Efecte de col·lisió de particules
    public static void createCollisionParticles(Pane root, double x, double y) {
        List<Particle> particles = new ArrayList<>();

        int numParticles = 10;
        for (int i = 0; i < numParticles; i++) {
            Particle particle = new Particle(x, y);
            particles.add(particle);
            root.getChildren().add(particle.getParticle());
        }

        AnimationTimer particlesAnimation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Iterator<Particle> iterator = particles.iterator();
                while (iterator.hasNext()) {
                    Particle particle = iterator.next();
                    particle.update();

                    if (particle.getRemainingLifetime() <= 0) {
                        root.getChildren().remove(particle.getParticle());
                        iterator.remove();
                    }
                }

                if (particles.isEmpty()) {
                    this.stop();
                }
            }
        };

        particlesAnimation.start();
    }

    //Efecte de núbol de particules
    public static void createDashEffect(Pane root, double startX, double startY) {
        List<Particle> particles = new ArrayList<>();

        int numParticles = 20;
        for (int i = 0; i < numParticles; i++) {
            Particle particle = new Particle(startX, startY);
            particles.add(particle);
            root.getChildren().add(particle.getParticle());
        }

        AnimationTimer particlesAnimation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Iterator<Particle> iterator = particles.iterator();
                while (iterator.hasNext()) {
                    Particle particle = iterator.next();
                    particle.update();

                    if (particle.getRemainingLifetime() <= 0) {
                        root.getChildren().remove(particle.getParticle());
                        iterator.remove();
                    }
                }

                if (particles.isEmpty()) {
                    this.stop();
                }
            }
        };

        particlesAnimation.start();
    }
    
    
    
    //Animació de morir
    public static void createDeathAnimation(Pane root, double x, double y, int numParticles) {
        List<Particle> particles = new ArrayList<>();

        for (int i = 0; i < numParticles; i++) {
            double angle = 360.0 * i / numParticles;
            Particle particle = new Particle(x, y, angle);
            particles.add(particle);
            root.getChildren().add(particle.getParticle());
        }

        AnimationTimer particlesAnimation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Iterator<Particle> iterator = particles.iterator();
                while (iterator.hasNext()) {
                    Particle particle = iterator.next();
                    particle.update();

                    if (particle.getRemainingLifetime() <= 0) {
                        root.getChildren().remove(particle.getParticle());
                        iterator.remove();
                    }
                }

                if (particles.isEmpty()) {
                    this.stop();
                }
            }
        };

        particlesAnimation.start();
    }
    
    // Update
    public void update() {
        circle.setTranslateX(circle.getTranslateX() + speedX);
        circle.setTranslateY(circle.getTranslateY() + speedY);
        lifetime -= 0.02;
        circle.setOpacity(lifetime / 1.5);
    }
    
    //Getters
    public Circle getParticle() {
        return circle;
    }
    
    public double getRemainingLifetime() {
        return lifetime;
    }

    

    // Col·lisions 
    public boolean collidesWithPlayer(Player player) {
        return circle.getBoundsInParent().intersects(player.getPlayer().getBoundsInParent());
    }

    public boolean collidesWithEnemy(Enemy enemy) {
        return circle.getBoundsInParent().intersects(enemy.getEnemy().getBoundsInParent());
    }

    public boolean collidesWithProjectile(Projectile projectile) {
        return circle.getBoundsInParent().intersects(projectile.getProjectile().getBoundsInParent());
    }
    
    
}
