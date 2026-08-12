package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager;

import com.almasb.fxgl.entity.Entity;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.data.CreditEntry;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.data.Wave;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.data.WaveDataLoader;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.EnemyCreditFactory;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventBus;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer.GameEventType;


import java.util.ArrayList;
import java.util.List;

/**
 * WaveManager.java
 * ================
 * Responsible for: loading the list of OOP-concept "credit" waves,
 * spawning their entries with a short delay between each, tracking when a
 * wave's enemies are all destroyed, and progressing to the next wave with
 * increasing difficulty - eventually spawning all four OOP pillars at
 * once for the finale, which is what makes the ending "intentionally
 * nearly impossible" per the design brief.
 *
 * CONTENT NOTE:
 * The actual wave/enemy/bullet-pattern data used to live here as an
 * 80-line hardcoded buildWaves() method. It has been moved out into
 * src/main/resources/waves/waves.json, loaded via WaveDataLoader - see
 * that class's javadoc for the full JSON schema and the reasoning behind
 * separating this content from the Java code that drives it. To change
 * what enemies exist, how tough they are, or how their bullets behave,
 * edit waves.json; nothing in this class needs to change.
 */
public class WaveManager {

    /** Classpath location of the wave data - see WaveDataLoader's javadoc for the JSON schema. */
    private static final String WAVE_DATA_RESOURCE = "/waves/waves.json";

    private final EnemyCreditFactory enemyCreditFactory;
    private final List<Wave> waves;

    private int currentWaveIndex = -1;
    private final List<Entity> aliveEnemiesThisWave = new ArrayList<>();

    private double spawnTimer = 0;
    private static final double SPAWN_INTERVAL_SECONDS = 1.2;
    private int nextEntryToSpawn = 0;

    private boolean waveFullySpawned = false;
    private boolean allWavesComplete = false;

    public WaveManager(EnemyCreditFactory enemyCreditFactory) {
        this.enemyCreditFactory = enemyCreditFactory;
        this.waves = WaveDataLoader.loadFromResource(WAVE_DATA_RESOURCE);
    }

    public void update(double tpf) {
        if (allWavesComplete) {
            return;
        }

        Wave currentWave = waves.get(currentWaveIndex);

        // Spawn remaining entries for this wave, spaced out over time so
        // they don't all appear on top of each other at once.
        if (!waveFullySpawned) {
            spawnTimer += tpf;
            if (spawnTimer >= SPAWN_INTERVAL_SECONDS && nextEntryToSpawn < currentWave.getEntries().size()) {
                spawnTimer = 0;
                CreditEntry entry = currentWave.getEntries().get(nextEntryToSpawn);

                double xFraction = currentWave.getEntries().size() > 1
                        ? (double) nextEntryToSpawn / (currentWave.getEntries().size() - 1)
                        : 0.5;

                Entity enemy = enemyCreditFactory.spawnEnemyCredit(entry, xFraction, currentWave.getDifficultyMultiplier());
                aliveEnemiesThisWave.add(enemy);

                nextEntryToSpawn++;
                if (nextEntryToSpawn >= currentWave.getEntries().size()) {
                    waveFullySpawned = true;
                }
            }
        }

        // Once everything for this wave has spawned AND every spawned
        // enemy has been destroyed/removed, move on to the next wave.
        if (waveFullySpawned) {
            aliveEnemiesThisWave.removeIf(e -> !e.isActive());
            if (aliveEnemiesThisWave.isEmpty()) {
                GameEventBus.getInstance().publish(GameEventType.WAVE_COMPLETE, currentWave.getTitle());
                advanceToNextWave();
            }
        }
    }

    private void advanceToNextWave() {
        currentWaveIndex++;
        if (currentWaveIndex >= waves.size()) {
            allWavesComplete = true;
            GameEventBus.getInstance().publish(GameEventType.GAME_VICTORY);
            return;
        }

        nextEntryToSpawn = 0;
        spawnTimer = 0;
        waveFullySpawned = false;
        aliveEnemiesThisWave.clear();

        Wave wave = waves.get(currentWaveIndex);
        GameEventBus.getInstance().publish(GameEventType.WAVE_STARTED, wave.getTitle());
    }

    public boolean isAllWavesComplete() {
        return allWavesComplete;
    }

    /** Starts (or restarts, after a retry) progression from the very first wave. */
    public void resetToFirstWave() {
        currentWaveIndex = -1;
        allWavesComplete = false;
        aliveEnemiesThisWave.clear();
        advanceToNextWave();
    }
}
