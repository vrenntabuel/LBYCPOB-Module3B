package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.audio;

import com.almasb.fxgl.audio.Music;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventBus;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventListener;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventType;


import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * AudioManager.java
 * =================
 * Implements the SINGLETON DESIGN PATTERN for game-wide audio, and also
 * implements GameEventListener so it can react to gameplay via the
 * OBSERVER pattern (see observer/GameEventBus.java) instead of gameplay
 * code calling into audio code directly.
 *
 * ASSET NOTE:
 * The design brief says to assume audio files already exist in the
 * resources folder. This class expects them at:
 *   src/main/resources/assets/music/battle-theme.mp3
 *   src/main/resources/assets/sounds/shoot_loop.mp3
 *   src/main/resources/assets/sounds/explosion.mp3
 *   src/main/resources/assets/sounds/victory.mp3
 *   src/main/resources/assets/sounds/damage.mp3
 *   src/main/resources/assets/sounds/word_hit.mp3
 *   src/main/resources/assets/sounds/boss_hit.mp3
 * FXGL automatically looks under src/main/resources/assets/... for audio,
 * so only the file names below need to match your actual files.
 */
public class AudioManager implements GameEventListener {

    private static AudioManager instance;

    private static final String MUSIC_FILE = "battle-theme.mp3";
    private static final String SFX_SHOOT_LOOP = "shoot_loop.mp3";
    private static final String SFX_EXPLOSION = "explosion.mp3";
    private static final String SFX_VICTORY = "victory.mp3";
    private static final String SFX_DAMAGE = "damage.mp3";
    private static final String SFX_WORD_HIT = "word_hit.mp3";
    private static final String SFX_BOSS_HIT = "boss_hit.mp3";

    private boolean musicStarted = false;

    /**
     * The shoot sound is a genuinely LOOPING audio track (not a one-shot
     * effect re-triggered per bullet), started once when gameplay begins
     * and stopped when it ends/pauses - see startShootLoop()/
     * stopShootLoop(), controlled from PlayingState's onEnter()/onExit().
     *
     * It needs its own Music instance (loaded once, then looped/stopped
     * via FXGL's AudioPlayer) rather than being folded into the main
     * background-music track, because it starts and stops independently
     * of the background music - e.g. it stops while paused, but the
     * background music does not.
     */
    private Music shootLoopMusic;
    private boolean shootLoopActive = false;

    private AudioManager() {
        // private constructor - see class javadoc on the Singleton pattern
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /** Registers this manager with the event bus - call once during setup. */
    public void startListening() {
        GameEventBus.getInstance().subscribe(this);
    }

    public void playBackgroundMusic() {
        if (!musicStarted) {
            try {
                loopBGM(MUSIC_FILE);
                musicStarted = true;
            } catch (Exception e) {
                // If the audio asset is missing, log it and keep playing
                // silently rather than crashing the whole game - useful
                // while assembling/replacing placeholder audio files.
                System.err.println("Could not load music '" + MUSIC_FILE + "': " + e.getMessage());
            }
        }
    }

    public void stopBackgroundMusic() {
        if (musicStarted) {
            getAudioPlayer().stopAllMusic();
            musicStarted = false;
        }
    }

    /** Starts the looping shoot sound - safe to call repeatedly, only starts once. */
    public void startShootLoop() {
        if (shootLoopActive) {
            return;
        }
        try {
            if (shootLoopMusic == null) {
                shootLoopMusic = getAssetLoader().loadMusic(SFX_SHOOT_LOOP);
            }
            // Use FXGL's built-in loop method
            getAudioPlayer().loopMusic(shootLoopMusic);
            shootLoopActive = true;
        } catch (Exception e) {
            System.err.println("Could not start shoot loop '" + SFX_SHOOT_LOOP + "': " + e.getMessage());
        }
    }

    /** Stops the looping shoot sound - safe to call even if it was never started. */
    public void stopShootLoop() {
        if (!shootLoopActive) {
            return;
        }
        try {
            if (shootLoopMusic != null) {
                getAudioPlayer().stopMusic(shootLoopMusic);
            }
        } catch (Exception e) {
            System.err.println("Could not stop shoot loop: " + e.getMessage());
        } finally {
            shootLoopActive = false;
        }
    }

    @Override
    public void onGameEvent(GameEventType event, Object payload) {
        switch (event) {
            case ENEMY_DESTROYED:
            case PILLAR_BOSS_DESTROYED:
            case ALLY_SACRIFICED:
                safePlay(SFX_EXPLOSION);
                break;
            case ENEMY_DAMAGED:
                // A light "hit" sound for regular OOP sub-concept words -
                // distinct from the bigger, more dramatic SFX_BOSS_HIT
                // used for the four pillar bosses (see PILLAR_BOSS_DAMAGED
                // below). EnemyCreditComponent only ever publishes ONE of
                // these two events per hit, never both, so they never
                // overlap on the same hit.
                safePlay(SFX_WORD_HIT);
                break;
            case PILLAR_BOSS_DAMAGED:
                safePlay(SFX_BOSS_HIT);
                break;
            case PLAYER_DAMAGED:
                safePlay(SFX_DAMAGE);
                break;
            case GAME_VICTORY:
                stopBackgroundMusic();
                stopShootLoop();
                safePlay(SFX_VICTORY);
                break;
            default:
                // Other events (WAVE_STARTED, NARRATIVE_LINE, etc.) currently
                // have no associated sound effect - intentionally ignored.
                break;
        }
    }

    /** Plays a sound effect, swallowing (and logging) a missing-asset error instead of crashing. */
    private void safePlay(String soundFile) {
        try {
            play(soundFile);
        } catch (Exception e) {
            System.err.println("Could not play sound '" + soundFile + "': " + e.getMessage());
        }
    }
}
