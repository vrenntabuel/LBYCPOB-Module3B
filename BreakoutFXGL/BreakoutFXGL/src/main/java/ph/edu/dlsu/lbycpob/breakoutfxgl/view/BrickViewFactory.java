package ph.edu.dlsu.lbycpob.breakoutfxgl.view;

import com.almasb.fxgl.entity.Entity;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ph.edu.dlsu.lbycpob.breakoutfxgl.model.Brick;
import ph.edu.dlsu.lbycpob.breakoutfxgl.model.BrickType;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * BrickViewFactory turns a Brick (MODEL) into a BrickView (an on-screen
 * FXGL Entity plus the bookkeeping needed to update its look later).
 * <p>
 * This is where the "two PNG images, robust rectangle fallback"
 * requirement is implemented:
 *   1. Ask BrickTextureLoader to load this brick type's "before" and
 *      "after" PNGs.
 *   2. If the "before" image loaded successfully, build an ImageView
 *      out of it - this is what the player will normally see.
 *   3. If it did NOT load (file missing, unreadable, etc.), fall back
 *      to a plain colored Rectangle instead, using the brick type's
 *      fallback color. Either way the game keeps running.
 */
public class BrickViewFactory {

    private final BrickTextureLoader textureLoader = new BrickTextureLoader();

    public BrickView createBrickView(Brick brick) {
        BrickType type = brick.getType();
        Image beforeImage = textureLoader.load(type.getBeforeImagePath());
        Image afterImage = textureLoader.load(type.getAfterImagePath());

        Node viewNode;
        ImageView imageView = null;
        Rectangle rectangle = null;

        if (beforeImage != null) {
            imageView = new ImageView(beforeImage);
            imageView.setFitWidth(brick.getWidth());
            imageView.setFitHeight(brick.getHeight());
            imageView.setPreserveRatio(false);
            viewNode = imageView;
        } else {
            // FALLBACK: no PNG on disk, so draw a simple colored
            // rectangle instead. The game must never crash or show a
            // blank tile just because art assets have not been added.
            rectangle = new Rectangle(brick.getWidth(), brick.getHeight());
            rectangle.setFill(Color.web(type.getColorHex()));
            rectangle.setStroke(Color.web("#2d3436"));
            rectangle.setStrokeWidth(1);
            rectangle.setArcWidth(6);
            rectangle.setArcHeight(6);
            rectangle.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.25)));
            viewNode = rectangle;
        }

        Entity entity = entityBuilder()
                .at(brick.getX(), brick.getY())
                .view(viewNode)
                .buildAndAttach();

        BrickView brickView = new BrickView(entity, imageView, rectangle, beforeImage, afterImage, type);
        brickView.refresh(brick); // make sure the initial look matches the brick's starting state
        return brickView;
    }
}
