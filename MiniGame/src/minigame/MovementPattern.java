package minigame;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;

import java.util.Random;

public class MovementPattern {

    private Random random = new Random();

    // Mètode per aplicar un patrón de moviment aleatori
    void executeRandomMovementPattern(Pane root, Enemy enemy) {
        int randomMovement = random.nextInt(3); // 0, 1 o 2

        switch (randomMovement) {
            case 0: idleAnimation(root, enemy); break;
            
            case 1: moveRandomly(root, enemy); break;
            
            case 2: moveInZigzag(root, enemy); break;
        }
    }

    //Idle
    private void idleAnimation(Pane root, Enemy enemy) {
        new AnimationTimer() {
            private long startTime = 0;
            private boolean isForward = true;

            @Override
            public void handle(long now) {
                if (startTime == 0) {
                    startTime = now;
                }

                double elapsedTime = (now - startTime) / 1_000_000_000.0; // 1 segon

                double vibrationRange = 4.0; 
                double vibrationSpeed = 1.0; 

                double deltaX = isForward ? vibrationRange : -vibrationRange;
                double deltaY = isForward ? vibrationSpeed : -vibrationSpeed;

                if (enemy.getTranslateX() <= Enemy.ENEMY_RADIUS || enemy.getTranslateX() >= root.getWidth() - Enemy.ENEMY_RADIUS) {
                    isForward = !isForward;
                }

                enemy.updatePosition(deltaX, deltaY, root);

                if (elapsedTime >= 2.0) {
                    isForward = !isForward;
                    startTime = now;
                }
            }
        }.start();
    }

    //Moviment random
    private void moveRandomly(Pane root, Enemy enemy) {
        double currentX = enemy.getTranslateX();
        double currentY = enemy.getTranslateY();

        double targetX = random.nextDouble() * root.getWidth();
        double targetY = random.nextDouble() * root.getHeight();

        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;

        double speed = Math.hypot(deltaX, deltaY);
        double maxMovementSpeed = Enemy.MAX_MOVEMENT_SPEED;

        if (speed > maxMovementSpeed) {
            double scale = maxMovementSpeed / speed;
            deltaX *= scale;
            deltaY *= scale;
        }

        enemy.updatePosition(deltaX, deltaY, root);
    }

    //Moviment en zig zag
    private void moveInZigzag(Pane root, Enemy enemy) {
        new AnimationTimer() {
            private double deltaX = Enemy.MOVEMENT_SPEED;
            private double deltaY = Enemy.MOVEMENT_SPEED;

            @Override
            public void handle(long now) {
                enemy.updatePosition(deltaX, deltaY, root);

                if (enemy.getTranslateX() <= Enemy.ENEMY_RADIUS || enemy.getTranslateX() >= root.getWidth() - Enemy.ENEMY_RADIUS) {
                    deltaX = -deltaX;
                }

                if (enemy.getTranslateY() <= Enemy.ENEMY_RADIUS || enemy.getTranslateY() >= root.getHeight() - Enemy.ENEMY_RADIUS) {
                    deltaY = -deltaY;
                }
            }
        }.start();
    }
}
