package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.data;

import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet.*;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.util.MiniJson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * WaveDataLoader.java
 * ===================
 * Reads the list of Waves (and every enemy "OOP concept" CreditEntry
 * within them) from a JSON resource file instead of hardcoding them in
 * Java, so tuning the game's content - adding a new enemy, tweaking a
 * bullet pattern's speed, reordering waves - is a plain-text edit that
 * does not require recompiling any Java code.
 *
 * See src/main/resources/waves/waves.json for the actual data, and the
 * "JSON SCHEMA" section below for what each field means.
 *
 * NOTE - separating DATA from CODE:
 * WaveManager used to have an 80-line buildWaves() method that was pure
 * hardcoded game content mixed into Java source. Moving that content out
 * into a data file (and having this one class be the only place that
 * knows how to translate that data into real objects) is a small example
 * of a broader, very common software design idea: keep your CONTENT
 * (numbers, names, tuning values) separate from your LOGIC (classes,
 * algorithms) wherever reasonably possible. A game designer - or a
 * curious student - can now change how the game plays just by editing
 * waves.json, without touching a single .java file.
 *
 * JSON SCHEMA:
 * <pre>
 * {
 *   "waves": [
 *     {
 *       "title": "Realm of Abstraction",
 *       "difficultyMultiplier": 1.0,
 *       "entries": [
 *         {
 *           "name": "Class",
 *           "health": 3,
 *           "isPillarBoss": false,
 *           "firePattern": {
 *             "type": "STRAIGHT",
 *             "durability": "FRAGILE",
 *             "baseInterval": 0.8,
 *             "bulletSpeed": 170
 *           }
 *         }
 *       ]
 *     }
 *   ]
 * }
 * </pre>
 *
 * "firePattern.type" selects which FirePattern implementation (see the
 * bullet/ package) to build, and which extra fields are required:
 *
 *   STRAIGHT      - baseInterval, bulletSpeed
 *   RADIAL_BURST  - baseInterval, bulletSpeed, bulletsPerBurst
 *   SPIRAL        - shotInterval, bulletSpeed, angleStepDegrees
 *   SPREAD        - baseInterval, bulletSpeed, spreadCount, totalSpreadDegrees
 *   WALL          - baseInterval, bulletSpeed, wallSegments, wallWidth
 *                   (wallWidth is the width the volley fans out to by the
 *                   time it reaches the bottom of the screen, NOT its
 *                   spawn width - see bullet/WallPattern.java's javadoc)
 *   RANDOM        - minInterval, maxInterval, bulletSpeed, coneDegrees
 *
 * Every pattern also requires "durability", one of: INDESTRUCTIBLE,
 * FRAGILE, TOUGH (see bullet/BulletDurability.java). Angles in the JSON
 * are given in DEGREES for readability - this loader converts them to
 * radians internally, since that is what the FirePattern constructors
 * expect.
 */
public final class WaveDataLoader {

    private WaveDataLoader() {
        // static-only utility class - never instantiated
    }

    /**
     * Loads every Wave from a JSON file on the classpath (i.e. somewhere
     * under src/main/resources).
     *
     * @param classpathResource an absolute classpath path, e.g.
     *                          "/waves/waves.json"
     * @throws IllegalStateException if the resource is missing or malformed -
     *         this is core game content, not an optional asset like audio,
     *         so failing loudly and immediately is more useful than limping
     *         along with an empty wave list.
     */
    public static List<Wave> loadFromResource(String classpathResource) {
        String json = readResource(classpathResource);
        Object root;
        try {
            root = MiniJson.parse(json);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Could not parse wave data JSON at '" + classpathResource + "': " + e.getMessage(), e);
        }
        return parseWaves(asMap(root, "root"));
    }

    private static String readResource(String classpathResource) {
        try (InputStream in = WaveDataLoader.class.getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IllegalStateException("Wave data resource not found on classpath: " + classpathResource);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read wave data resource: " + classpathResource, e);
        }
    }

    private static List<Wave> parseWaves(Map<String, Object> root) {
        List<Object> wavesArray = asList(root.get("waves"), "waves");
        List<Wave> waves = new ArrayList<>();
        for (Object waveObj : wavesArray) {
            waves.add(parseWave(asMap(waveObj, "wave")));
        }
        if (waves.isEmpty()) {
            throw new IllegalStateException("Wave data JSON contained no waves - the game has nothing to play.");
        }
        return waves;
    }

    private static Wave parseWave(Map<String, Object> waveMap) {
        String title = asString(waveMap.get("title"), "wave.title");
        double difficultyMultiplier = asDouble(waveMap.get("difficultyMultiplier"), "wave.difficultyMultiplier");

        List<Object> entriesArray = asList(waveMap.get("entries"), "wave.entries");
        List<CreditEntry> entries = new ArrayList<>();
        for (Object entryObj : entriesArray) {
            entries.add(parseCreditEntry(asMap(entryObj, "entry")));
        }

        return new Wave(title, entries, difficultyMultiplier);
    }

    private static CreditEntry parseCreditEntry(Map<String, Object> entryMap) {
        String name = asString(entryMap.get("name"), "entry.name");
        int health = (int) asDouble(entryMap.get("health"), "entry.health");
        boolean isPillarBoss = Boolean.TRUE.equals(entryMap.get("isPillarBoss"));

        FirePattern firePattern = parseFirePattern(asMap(entryMap.get("firePattern"), "entry.firePattern"), name);

        return new CreditEntry(name, health, firePattern, isPillarBoss);
    }

    private static FirePattern parseFirePattern(Map<String, Object> patternMap, String ownerName) {
        String type = asString(patternMap.get("type"), "firePattern.type").toUpperCase(Locale.ROOT);
        BulletDurability durability = parseDurability(asString(patternMap.get("durability"), "firePattern.durability"), ownerName);

        return switch (type) {
            case "STRAIGHT" -> new StraightPattern(
                    asDouble(patternMap.get("baseInterval"), "firePattern.baseInterval"),
                    asDouble(patternMap.get("bulletSpeed"), "firePattern.bulletSpeed"),
                    durability);

            case "RADIAL_BURST" -> new RadialBurstPattern(
                    asDouble(patternMap.get("baseInterval"), "firePattern.baseInterval"),
                    asDouble(patternMap.get("bulletSpeed"), "firePattern.bulletSpeed"),
                    (int) asDouble(patternMap.get("bulletsPerBurst"), "firePattern.bulletsPerBurst"),
                    durability);

            case "SPIRAL" -> new SpiralPattern(
                    asDouble(patternMap.get("shotInterval"), "firePattern.shotInterval"),
                    asDouble(patternMap.get("bulletSpeed"), "firePattern.bulletSpeed"),
                    Math.toRadians(asDouble(patternMap.get("angleStepDegrees"), "firePattern.angleStepDegrees")),
                    durability);

            case "SPREAD" -> new SpreadPattern(
                    asDouble(patternMap.get("baseInterval"), "firePattern.baseInterval"),
                    asDouble(patternMap.get("bulletSpeed"), "firePattern.bulletSpeed"),
                    (int) asDouble(patternMap.get("spreadCount"), "firePattern.spreadCount"),
                    Math.toRadians(asDouble(patternMap.get("totalSpreadDegrees"), "firePattern.totalSpreadDegrees")),
                    durability);

            case "WALL" -> new WallPattern(
                    asDouble(patternMap.get("baseInterval"), "firePattern.baseInterval"),
                    asDouble(patternMap.get("bulletSpeed"), "firePattern.bulletSpeed"),
                    (int) asDouble(patternMap.get("wallSegments"), "firePattern.wallSegments"),
                    asDouble(patternMap.get("wallWidth"), "firePattern.wallWidth"),
                    durability);

            case "RANDOM" -> new RandomPattern(
                    asDouble(patternMap.get("minInterval"), "firePattern.minInterval"),
                    asDouble(patternMap.get("maxInterval"), "firePattern.maxInterval"),
                    asDouble(patternMap.get("bulletSpeed"), "firePattern.bulletSpeed"),
                    Math.toRadians(asDouble(patternMap.get("coneDegrees"), "firePattern.coneDegrees")),
                    durability);

            default -> throw new IllegalStateException(
                    "Unknown firePattern.type '" + type + "' for entry '" + ownerName + "' - expected one of "
                            + "STRAIGHT, RADIAL_BURST, SPIRAL, SPREAD, WALL, RANDOM");
        };
    }

    private static BulletDurability parseDurability(String raw, String ownerName) {
        try {
            return BulletDurability.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Unknown durability '" + raw + "' for entry '" + ownerName + "' - expected one of "
                            + "INDESTRUCTIBLE, FRAGILE, TOUGH");
        }
    }

    // --- Small typed-access helpers, so every "wrong/missing field" error
    //     names exactly which field was the problem instead of throwing a
    //     generic ClassCastException/NullPointerException somewhere deep
    //     in a switch expression. ---

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value, String fieldName) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        throw new IllegalStateException("Expected a JSON object for '" + fieldName + "' but found: " + value);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> asList(Object value, String fieldName) {
        if (value instanceof List) {
            return (List<Object>) value;
        }
        throw new IllegalStateException("Expected a JSON array for '" + fieldName + "' but found: " + value);
    }

    private static String asString(Object value, String fieldName) {
        if (value instanceof String s) {
            return s;
        }
        throw new IllegalStateException("Expected a JSON string for '" + fieldName + "' but found: " + value);
    }

    private static double asDouble(Object value, String fieldName) {
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        throw new IllegalStateException("Expected a JSON number for '" + fieldName + "' but found: " + value);
    }
}
