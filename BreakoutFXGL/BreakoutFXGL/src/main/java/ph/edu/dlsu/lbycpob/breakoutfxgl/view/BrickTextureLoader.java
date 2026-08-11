package ph.edu.dlsu.lbycpob.breakoutfxgl.view;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * BrickTextureLoader is responsible for ONE thing: trying to load a
 * PNG image from the classpath (src/main/resources/...) and returning
 * null instead of throwing an exception if that image cannot be
 * found. This is the "robustness" piece the assignment asks for -
 * the game must keep working with plain colored rectangles if the art
 * assets have not been added yet.
 *
 * WHY A SEPARATE CLASS: putting this logic in its own small class
 * (instead of inline inside BreakoutApp) means:
 *   - It can be reused for any future image (ball, paddle, power-up
 *     sprites) without copy-pasting the try/catch logic.
 *   - It is easy to unit test on its own.
 *   - Each image is only ever read from disk once and then cached in
 *     memory (see the "cache" map below), which matters because
 *     several bricks of the same type all share the same picture.
 */
public class BrickTextureLoader {

    // Caches both successful loads AND failed lookups (as null), so
    // we never try to re-read a missing file from disk more than once.
    private final Map<String, Image> cache = new HashMap<>();

    /**
     * @param resourcePath a classpath location such as
     *                     "/assets/textures/brick_normal.png", or null
     * @return the loaded Image, or null if resourcePath was null, the
     *         file does not exist, or the file could not be decoded as
     *         an image for any reason
     */
    public Image load(String resourcePath) {
        if (resourcePath == null) {
            return null;
        }

        if (cache.containsKey(resourcePath)) {
            return cache.get(resourcePath); // may legitimately be a cached null
        }

        Image image = tryLoad(resourcePath);
        cache.put(resourcePath, image);
        return image;
    }

    private Image tryLoad(String resourcePath) {
        // getResourceAsStream returns null (does NOT throw) when the
        // file is missing, which is exactly the "safe to check" outcome
        // we want before attempting to decode anything as an image.
        try (InputStream stream = BrickTextureLoader.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                return null; // file not found - caller falls back to a rectangle
            }
            Image image = new Image(stream);
            if (image.isError()) {
                return null; // file existed but was not a valid image
            }
            return image;
        } catch (Exception e) {
            // Any unexpected I/O problem is also treated as "no image
            // available" rather than crashing the game.
            return null;
        }
    }
}
