package ph.edu.dlsu.lbycpob.breakoutfxgl.audio;


import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import ph.edu.dlsu.lbycpob.breakoutfxgl.controller.GameEventListener;
import java.net.URL;

/**
 * AudioManager owns every sound the game makes: the looping background
 * music, the ball-bounce sound effect, and the brick-break sound
 * effect. It is created and used by BreakoutApp (the VIEW layer) and
 * implements GameEventListener so that GameManager (the CONTROLLER)
 * can trigger sounds indirectly, through that interface, without ever
 * importing anything from this class or from JavaFX's media package.
 * <p>
 * ROBUST LOADING, SAME PHILOSOPHY AS BrickTextureLoader:
 * All three audio files are expected under
 * src/main/resources/assets/sounds/ (see README_ASSETS.txt in that
 * folder for exact file names). If a file is missing, unreadable, or
 * not a valid audio file, loading here simply produces null instead
 * of throwing an exception - the corresponding play...() method then
 * just does nothing. The game must always be playable, with or
 * without sound assets, exactly like the brick images.
 * <p> <p>
 * WHY TWO DIFFERENT JAVAFX AUDIO CLASSES:
 *   - javafx.scene.media.MediaPlayer is used for the background music,
 *     because it supports looping (setCycleCount) and represents a
 *     single long-running track.
 *   - javafx.scene.media.AudioClip is used for the two short sound
 *     effects, because it is lightweight and can be triggered many
 *     times in quick succession (for example, several bricks breaking
 *     within the same second) without needing to manage a pool of
 *     MediaPlayer objects ourselves.
 */
public class AudioManager implements GameEventListener {

    private static final String BACKGROUND_MUSIC_PATH = "/assets/sounds/background_music.mp3";
    private static final String BALL_BOUNCE_PATH = "/assets/sounds/ball_bounce.mp3";
    private static final String BRICK_BREAK_PATH = "/assets/sounds/brick_hit.mp3";
    private static final String GAME_OVER_PATH =  "/assets/sounds/game_over.mp3";
    private static final String VICTORY_PATH = "/assets/sounds/game_win.mp3";

    private static final double DEFAULT_MUSIC_VOLUME = 0.35;
    private static final double DEFAULT_SFX_VOLUME = 0.7;


    // Any of these three may legitimately be null if the matching file
    // was not found - every method below checks for that before using them.
    private final MediaPlayer backgroundMusicPlayer;
    private final AudioClip ballBounceClip;
    private final AudioClip brickBreakClip;
    private final AudioClip gameOverClip;
    private final AudioClip victoryClip;

    private boolean muted = false;


    public AudioManager() {
        this.backgroundMusicPlayer = loadMusic(BACKGROUND_MUSIC_PATH, DEFAULT_MUSIC_VOLUME);
        this.ballBounceClip = loadClip(BALL_BOUNCE_PATH);
        this.brickBreakClip = loadClip(BRICK_BREAK_PATH);
        this.gameOverClip = loadClip(GAME_OVER_PATH);
        this.victoryClip = loadClip(VICTORY_PATH);
    }

    // -----------------------------------------------------------------
    // Robust loading helpers - never throw, only ever return null on failure
    // -----------------------------------------------------------------

    private URL resolve(String classpathLocation) {
        // getResource() returns null (does NOT throw) when the file is
        // missing, which is exactly the "safe to check" behavior we want.
        return AudioManager.class.getResource(classpathLocation);
    }

    private AudioClip loadClip(String classpathLocation) {
        URL url = resolve(classpathLocation);
        if (url == null) {
            return null; // file not found - the matching play...() call below will simply do nothing
        }
        try {
            return new AudioClip(url.toExternalForm());
        } catch (Exception e) {
            // File existed but was not valid audio, or the platform could
            // not decode it - treat this the same as "file not found".
            return null;
        }
    }

    private MediaPlayer loadMusic(String classpathLocation, double volume) {
        URL url = resolve(classpathLocation);
        if (url == null) {
            return null;
        }
        try {
            Media media = new Media(url.toExternalForm());
            MediaPlayer player = new MediaPlayer(media);
            player.setCycleCount(MediaPlayer.INDEFINITE); // loop forever
            player.setVolume(volume);
            return player;
        } catch (Exception e) {
            return null;
        }
    }

    // -----------------------------------------------------------------
    // Playback controls - called by BreakoutApp
    // -----------------------------------------------------------------

    /** Starts the looping background music, if it loaded successfully and audio is not muted. */
    public void playBackgroundMusic() {
        if (backgroundMusicPlayer != null && !muted) {
            backgroundMusicPlayer.play();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }
    }

    public void playBallBounceSound() {
        if (ballBounceClip != null && !muted) {
            ballBounceClip.play(DEFAULT_SFX_VOLUME);
        }
    }

    public void playBrickBreakSound() {
        if (brickBreakClip != null && !muted) {
            brickBreakClip.play(DEFAULT_SFX_VOLUME);
        }
    }

    /**
     * Flips mute on/off for both music and sound effects and returns the
     * new muted state, so the caller can update a HUD indicator if it wants to.
     */
    public boolean toggleMute() {
        muted = !muted;
        if (backgroundMusicPlayer != null) {
            if (muted) {
                backgroundMusicPlayer.pause();
            } else {
                backgroundMusicPlayer.play();
            }
        }
        return muted;
    }

    public boolean isMuted() {
        return muted;
    }

    // -----------------------------------------------------------------
    // GameEventListener implementation - this is the ONLY way GameManager
    // (the Controller) is able to trigger sounds; it calls these two
    // methods through the interface and never touches this class directly.
    // -----------------------------------------------------------------

    @Override
    public void onBallBounce() {
        playBallBounceSound();
    }

    @Override
    public void onBrickBroken() {
        playBrickBreakSound();
    }

    @Override
    public void onGameOver() {
        if (gameOverClip != null && !muted) {
            gameOverClip.play(DEFAULT_SFX_VOLUME);
        }
    }

    @Override
    public void onVictory() {
        if (victoryClip != null && !muted) {
            victoryClip.play(DEFAULT_SFX_VOLUME);
        }
    }
}
