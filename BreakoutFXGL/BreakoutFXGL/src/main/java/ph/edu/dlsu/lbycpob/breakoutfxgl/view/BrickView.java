package ph.edu.dlsu.lbycpob.breakoutfxgl.view;

import com.almasb.fxgl.entity.Entity;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ph.edu.dlsu.lbycpob.breakoutfxgl.model.Brick;
import ph.edu.dlsu.lbycpob.breakoutfxgl.model.BrickType;

/**
 * BrickView bundles together everything the VIEW layer needs to
 * remember about one particular brick's on-screen appearance:
 *   - the FXGL Entity that was actually attached to the game world
 *   - EITHER an ImageView (if a PNG was found) OR a Rectangle (the
 *     robust fallback), never both
 *   - the cached "before" and "after" images, if any, so refresh()
 *     can swap between them without touching the disk again
 *
 * This class deliberately knows nothing about collisions, scoring, or
 * any other game RULE - it only draws what the Brick model tells it
 * to draw. That is the View's whole job in our MVC split.
 */
public class BrickView {

    private final Entity entity;
    private final ImageView imageView; // null if this brick fell back to a rectangle
    private final Rectangle rectangle; // null if this brick is using real images
    private final javafx.scene.image.Image beforeImage;
    private final javafx.scene.image.Image afterImage;
    private final BrickType type;

    public BrickView(Entity entity, ImageView imageView, Rectangle rectangle,
                      javafx.scene.image.Image beforeImage, javafx.scene.image.Image afterImage,
                      BrickType type) {
        this.entity = entity;
        this.imageView = imageView;
        this.rectangle = rectangle;
        this.beforeImage = beforeImage;
        this.afterImage = afterImage;
        this.type = type;
    }

    public Entity getEntity() {
        return entity;
    }

    /**
     * Updates this brick's on-screen look to match its current model
     * state (undamaged vs. damaged). Called once when the brick is
     * first created and again every frame afterward - the check
     * itself is cheap, and only actually changes the image/color when
     * the damage state has actually flipped.
     */
    public void refresh(Brick brick) {
        boolean damaged = brick.isDamaged();

        if (imageView != null) {
            javafx.scene.image.Image desired = (damaged && afterImage != null) ? afterImage : beforeImage;
            if (imageView.getImage() != desired) {
                imageView.setImage(desired);
            }
        } else if (rectangle != null) {
            String hex = damaged ? type.getDamagedColorHex() : type.getColorHex();
            rectangle.setFill(Color.web(hex));
        }
    }
}
