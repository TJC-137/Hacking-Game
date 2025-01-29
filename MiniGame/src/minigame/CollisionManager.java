//@author TJ

package minigame;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

// Classe CollisionManager
public class CollisionManager {

    // Mètode per comprovar les col·lisions
    public static void checkCollisions (Player player, List<Enemy> enemies, 
                                        List<Projectile> playerProjectiles,
                                        List<Projectile> enemyProjectiles, Pane root) {
        
        if (player.isAlive()) { // Si el jugador està viu
            checkCollisionPlayerProjectileVsEnemies(playerProjectiles, enemies, root);
            checkCollisionEnemyProjectileVsPlayer(enemies, player, root);
            
            checkEnemyPlayerCollision(player, enemies, root);
            checkCollisionPlayerProjectileVsEnemyProjectile( playerProjectiles, enemies, root);
            
            checkPlayerProjectileCollisionsWithBounds( playerProjectiles,  root);
            checkEnemyProjectileCollisionsWithBounds(enemyProjectiles, root);
        }
    }

    // Col·lisió: Player Projectiles Vs Enemy
    private static void checkCollisionPlayerProjectileVsEnemies(List<Projectile> playerProjectiles, List<Enemy> targets, Pane root) {
        List<Projectile> projectilesToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (Projectile playerProjectile : playerProjectiles) {
            for (Enemy enemy : targets) {
                if (playerProjectile.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                    handleCollisionEffects(root, playerProjectile.getProjectile());
                    projectilesToRemove.add(playerProjectile);

                    enemy.decreaseHealth(10, root);

                    if (!enemy.isAlive()) {
                        enemiesToRemove.add(enemy);
                    }
                }
            }
        }

        removeProjectilesFromPane(projectilesToRemove, root);
        playerProjectiles.removeAll(projectilesToRemove);

        removeEnemiesFromPane(enemiesToRemove, root);
        targets.removeAll(enemiesToRemove);
        
        
    }


    // Col·lisió: Enemy Projectile Vs Player
    static void checkCollisionEnemyProjectileVsPlayer(List<Enemy> enemies, Player player, Pane root) {
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (Enemy enemy : enemies) {
            List<Projectile> enemyProjectiles = enemy.getEnemyProjectiles();
            List<Projectile> projectilesToRemove = new ArrayList<>();

            for (Projectile enemyProjectile : enemyProjectiles) {
                if (enemyProjectile.getBoundsInParent().intersects(player.getBoundsInParent())) {
                    handleCollisionEffects(root, enemyProjectile.getProjectile());
                    projectilesToRemove.add(enemyProjectile);
                  
                    
                    if (player.isAlive()) {
                        player.decreaseHealth(100, root);
                    }

                    if (!player.isAlive()) {
                        player.handleDeath(root);
                    }
                }
            }

            removeProjectilesFromPane(projectilesToRemove, root);
            enemyProjectiles.removeAll(projectilesToRemove);

            if (!enemy.isAlive()) {
                enemiesToRemove.add(enemy);
            }
        }

        removeEnemiesFromPane(enemiesToRemove, root);
        enemies.removeAll(enemiesToRemove);
    }

    // Col·lisió: Player Projectiles vs Enemy Projectiles
    static void checkCollisionPlayerProjectileVsEnemyProjectile(List<Projectile> playerProjectiles, List<Enemy> enemies, Pane root) {
        List<Projectile> playerProjectilesToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (Projectile playerProjectile : playerProjectiles) {
            for (Enemy enemy : enemies) {
                List<Projectile> enemyProjectiles = enemy.getEnemyProjectiles();
                List<Projectile> enemyProjectilesToRemove = new ArrayList<>();

                for (Projectile enemyProjectile : enemyProjectiles) {
                    if (playerProjectile.getBoundsInParent().intersects(enemyProjectile.getBoundsInParent())) {
                        handleCollisionEffects(root, playerProjectile.getProjectile());
                        handleCollisionEffects(root, enemyProjectile.getProjectile());

                        playerProjectilesToRemove.add(playerProjectile);
                        enemyProjectilesToRemove.add(enemyProjectile);
                        
                        Sound.particleExplosion();
                    }
                }
                removeProjectilesFromPane(enemyProjectilesToRemove, root);
                enemyProjectiles.removeAll(enemyProjectilesToRemove);

                if (!enemy.isAlive()) {
                    enemiesToRemove.add(enemy);
                }
            }
        }
        removeProjectilesFromPane(playerProjectilesToRemove, root);
        playerProjectiles.removeAll(playerProjectilesToRemove);

        removeEnemiesFromPane(enemiesToRemove, root);
        enemies.removeAll(enemiesToRemove);
        
        
    }

    // Col·lisió: Player vs Enemy (Body)
    private static void checkEnemyPlayerCollision(Player player, List<Enemy> enemies, Pane root) {
        for (Enemy enemy : enemies) {
            if (enemy.getBoundsInParent().intersects(player.getBoundsInParent())) {
                player.handleCollision(root, player);
            }
        }
    }

    // Col·lisió: Player Projectiles vs OutOfBounds
    private static void checkPlayerProjectileCollisionsWithBounds(List<Projectile> playerProjectiles, Pane root) {
        List<Projectile> projectilesToRemove = new ArrayList<>();

        for (Projectile playerProjectile : playerProjectiles) {
            if (isOutOfBounds(playerProjectile, root)) {
                handleCollisionEffects(root, playerProjectile.getProjectile());
                projectilesToRemove.add(playerProjectile);
            }
        }

        removeProjectilesFromPane(projectilesToRemove, root);
        playerProjectiles.removeAll(projectilesToRemove);
    }
    
    // Col·lisió: Enemy Projectiles vs OutOfBounds
    static void checkEnemyProjectileCollisionsWithBounds(List<Projectile> enemyProjectiles, Pane root) {
        List<Projectile> projectilesToRemove = new ArrayList<>();

        for (Projectile enemyProjectile : enemyProjectiles) {
            if (isOutOfBounds(enemyProjectile, root)) {
                handleCollisionEffects(root, enemyProjectile.getProjectile());
                projectilesToRemove.add(enemyProjectile);
            }
        }

        removeProjectilesFromPane(projectilesToRemove, root);
        enemyProjectiles.removeAll(projectilesToRemove);
    }
    
    // Mètode per comprovar si els projectils estàn fora dels límits
    private static boolean isOutOfBounds(Projectile projectile, Pane root) {
        Circle projectileCircle = projectile.getProjectile();
        double projectileX = projectileCircle.getTranslateX();
        double projectileY = projectileCircle.getTranslateY();
        double projectileRadius = projectileCircle.getRadius();

        return projectileX - projectileRadius < 0 || projectileX + projectileRadius > root.getWidth()
                || projectileY - projectileRadius < 0 || projectileY + projectileRadius > root.getHeight();
    }

    // Eliminar els projectils enemics de pantalla
    static void removeProjectilesFromPane(List<Projectile> projectilesToRemove, Pane root) {
        projectilesToRemove.forEach(projectile -> root.getChildren().remove(projectile.getProjectile()));
    }

    // Eliminar els enemics de la pantalla
    static void removeEnemiesFromPane(List<Enemy> enemiesToRemove, Pane root) {
        enemiesToRemove.forEach(enemy -> root.getChildren().removeAll(enemy.getEnemy(), enemy.getShooter()));
    }

    // Gestió d'efectes de la col·lisió
    private static void handleCollisionEffects(Pane root, Circle circle) {
        Particle.createCollisionParticles(root, circle.getTranslateX(), circle.getTranslateY());
    }
    

}
