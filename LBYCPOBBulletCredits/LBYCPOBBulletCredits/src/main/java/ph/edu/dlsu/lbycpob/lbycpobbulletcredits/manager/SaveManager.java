package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * SaveManager.java
 * ================
 * Tracks whether the player has completed the ending, persisted to a small
 * local properties file so it survives between runs of the game.
 *
 * NOTE:
 * We deliberately keep this simple (a single boolean flag in a
 * java.util.Properties file) rather than reaching for a database or a
 * complex serialization framework. Properties files are plain text,
 * human-readable, and part of the standard library - a good fit for a
 * small arcade game's save data.
 */
public class SaveManager {

    private static final String SAVE_FILE_NAME = "oop-bullet-hell-save.properties";
    private static final String KEY_ENDING_COMPLETE = "ending.complete";

    private final Path saveFilePath;

    public SaveManager() {
        // Stored next to the user's home directory so it works whether the
        // game is launched from an IDE or a packaged jar.
        this.saveFilePath = Path.of(System.getProperty("user.home"), SAVE_FILE_NAME);
    }

    public void markEndingComplete() {
        Properties properties = loadProperties();
        properties.setProperty(KEY_ENDING_COMPLETE, "true");
        saveProperties(properties);
    }

    public boolean isEndingComplete() {
        Properties properties = loadProperties();
        return Boolean.parseBoolean(properties.getProperty(KEY_ENDING_COMPLETE, "false"));
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        if (Files.exists(saveFilePath)) {
            try (var reader = Files.newBufferedReader(saveFilePath)) {
                properties.load(reader);
            } catch (IOException e) {
                // Non-fatal: a missing/corrupt save file just means we
                // treat progress as "not yet completed".
                System.err.println("Could not read save file: " + e.getMessage());
            }
        }
        return properties;
    }

    private void saveProperties(Properties properties) {
        try (var writer = Files.newBufferedWriter(saveFilePath)) {
            properties.store(writer, "OOP Bullet Credits save data");
        } catch (IOException e) {
            System.err.println("Could not write save file: " + e.getMessage());
        }
    }
}
