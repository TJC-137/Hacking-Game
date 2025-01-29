package minigame;

import java.util.Random;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ShootingPattern {
    
    
    // Mètode per executar un patró de tir random
    public static void executeRandomAttackPattern(Pane root, Enemy enemy) {
        if (enemy.isAlive()) {  // Asegurar que el enemigo está vivo antes de disparar
            int randomAttack = new Random().nextInt(3); // 0, 1 o 2
            
            switch (randomAttack) {
                case 0: shootFanPattern(root, enemy, 3); break;
                case 1: shootSinglePattern(root, enemy); break;
                case 2: shootCircularPattern(root, enemy, 10); break;
            }
            
        }
    }


    // Patró de 3 projectiles en forma de abanico
    public static void shootFanPattern(Pane root, Enemy enemy, int numProjectiles) {
        double centerX = enemy.getTranslateX();
        double centerY = enemy.getTranslateY();

        double angleToPlayer = Math.toDegrees(Math.atan2(enemy.getPlayer().getTranslateY() - centerY, enemy.getPlayer().getTranslateX() - centerX));

        for (int i = -1; i <= 1; i++) {
            double angle = angleToPlayer + i * 20.0;
            shootProjectile(root, enemy, angle, Color.ORANGE);
        }
    }

    // Patró de un sol projectil
    public static void shootSinglePattern(Pane root, Enemy enemy) {
        double centerX = enemy.getTranslateX();
        double centerY = enemy.getTranslateY();

        // Enemy apuntant al Player
        double angleToPlayer = Math.toDegrees(Math.atan2(enemy.getPlayer().getTranslateY() - centerY, enemy.getPlayer().getTranslateX() - centerX));

        // Cream un objete Projectile
        Projectile enemyProjectile = new Projectile(centerX, centerY, 2.0, angleToPlayer, Color.RED, 10.0);

        
        // Agregam el projectil a la llista de projectiles del enemy
        enemy.getEnemyProjectiles().add(enemyProjectile);

        // Afegim el projectile al root
        root.getChildren().add(enemyProjectile.getProjectile());

        // Iniciam la animació del projectil
        enemyProjectile.startAnimation(root);
        
        
    }


    // Patró de disparar en cercles
    public static void shootCircularPattern(Pane root, Enemy enemy, int numProjectiles) {
        // Obtenim les coordenades del centre de l'enemic
        double centerX = enemy.getTranslateX();
        double centerY = enemy.getTranslateY();

        // Radi, color i velocitat dels projectils
        double radius = enemy.getShootingRadius();
        Color projectileColor = Color.PURPLE;
        double projectileSpeed = 2.0;
        double projectileRadius = 10.0;

        
        // Cream els projectils en un patró circular
        for (int i = 0; i < numProjectiles; i++) {
            double angle = 360.0 * i / numProjectiles;
            double projectileX = centerX + radius * Math.cos(Math.toRadians(angle));
            double projectileY = centerY + radius * Math.sin(Math.toRadians(angle));

            // Cream un objecte Projectile
            Projectile enemyProjectile = new Projectile(projectileX, projectileY, projectileSpeed, angle, projectileColor, projectileRadius);
            enemy.getEnemyProjectiles().add(enemyProjectile);
            root.getChildren().add(enemyProjectile.getProjectile());
            enemyProjectile.startAnimation(root);

            // Manejam col·lisió amb el Player
            if (enemyProjectile.getBoundsInParent().intersects(enemy.getPlayer().getBoundsInParent())) {
                handleCollisionEffects(root, enemy.getPlayer());
                enemy.getPlayer().decreaseHealth(20, root);

                // Verificam si el jugador està viu
                if (!enemy.getPlayer().isAlive()) {
                    enemy.getPlayer().handleDeath(root);
                }
            }
        }
    }

    // Mecànica per disparar
    private static void shootProjectile(Pane root, Enemy enemy, double angle, Color color) {
        // Coordenades del centre de l'enemic
        double centerX = enemy.getTranslateX();
        double centerY = enemy.getTranslateY();

        // Coordenades del projectil segons l'angle
        double projectileX = centerX + enemy.getShootingRadius() * Math.cos(Math.toRadians(angle));
        double projectileY = centerY + enemy.getShootingRadius() * Math.sin(Math.toRadians(angle));

        // Creació del projectil
        Projectile enemyProjectile = new Projectile(projectileX, projectileY, 2.0, angle, color, 10.0);

        
        // Afegim el projectil a la llista de projectils de l'enemic
        enemy.getEnemyProjectiles().add(enemyProjectile);

        // Afegim el projectil al root
        root.getChildren().add(enemyProjectile.getProjectile());

        
        // Iniciar l'animació del projectil
        enemyProjectile.startAnimation(root);
    }


    // Gestió de colisions
    private static void handleCollisionEffects(Pane root, Player player) {
        double centerX = (player.getBoundsInParent().getMinX() + player.getBoundsInParent().getMaxX()) / 2;
        double centerY = (player.getBoundsInParent().getMinY() + player.getBoundsInParent().getMaxY()) / 2;
        Particle.createCollisionParticles(root, centerX, centerY);
    }
}
