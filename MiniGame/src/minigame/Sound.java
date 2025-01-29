// @author TJ
package minigame;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Sound {
    
    private static MediaPlayer mediaPlayerBGM;
    private static MediaPlayer mediaPlayerBTL;
    private static MediaPlayer mediaPlayerPS;
    private static MediaPlayer mediaPlayerES;

    // Main Menu Play Music
    public static void playBackgroundMusic() {
        stopMusic();
        mediaPlayerBGM = createMediaPlayer("audio/MainMenu.mp3", 0.5, true);
    }
    
    // Battle Music
    public static void playBattleMusic() {
        stopMusic();
        mediaPlayerBTL = createMediaPlayer("audio/Nier-8-bit.mp3", 0.5, true);
    }
    
    // Player Shot
    public static void playerShotSound(){
        mediaPlayerPS = createMediaPlayer("audio/player-fire.mp3", 0.5, false);
    }
    
    // Enemy Shot
    public static void enemyShotSound(){   
        mediaPlayerES = createMediaPlayer("audio/enemy-fire.mp3", 0.5, false);
    }
    
    
    // Projectiles collision
    public static void particleExplosion(){   
        mediaPlayerES = createMediaPlayer("audio/explosion-2.mp3", 0.5, false);
    }
    
    // Stop Music
    public static void stopMusic() {
        if (mediaPlayerBGM != null) {
            mediaPlayerBGM.stop();
        }
        if (mediaPlayerBTL != null) {
            mediaPlayerBTL.stop();
        }
    }
    
    public static void stopEnemyShot(){
        if (mediaPlayerES  != null) {
            mediaPlayerES .stop();
        }
    }
    
    public static void stopPlayerShot(){
        if (mediaPlayerPS  != null) {
            mediaPlayerPS .stop();
        }
    }
    
    
    // Media Player
    private static MediaPlayer createMediaPlayer(String filePath, double volume, boolean isLooping) {
        
        Media media = new Media(new File(filePath).toURI().toString());
        MediaPlayer newMediaPlayer = new MediaPlayer(media);
        newMediaPlayer.setVolume(volume);

        if (isLooping) {
            newMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        }
               
        

        newMediaPlayer.play();
        return newMediaPlayer;
    }   
}
