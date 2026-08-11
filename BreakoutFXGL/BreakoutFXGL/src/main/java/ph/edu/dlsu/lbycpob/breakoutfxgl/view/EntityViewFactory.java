package ph.edu.dlsu.lbycpob.breakoutfxgl.view;

import com.almasb.fxgl.entity.Entity;

import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ph.edu.dlsu.lbycpob.breakoutfxgl.model.Ball;
import ph.edu.dlsu.lbycpob.breakoutfxgl.model.Paddle;
import ph.edu.dlsu.lbycpob.breakoutfxgl.model.PowerUp;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * EntityViewFactory builds the on-screen shapes for the paddle, the
 * ball(s), and falling power-ups. Unlike bricks, these do not need the
 * PNG-with-fallback logic (the assignment only asked for image-based
 * bricks), so plain JavaFX shapes are enough here - just styled to
 * match the same light, rounded, drop-shadowed "indie game" look used
 * everywhere else in the interface.
 */
public class EntityViewFactory {

    private static final Color PADDLE_COLOR = Color.web("#2d3436");
    private static final Color BALL_COLOR = Color.web("#e17055");

    /** Also returns the Rectangle directly, since BreakoutApp needs to resize it when the paddle widens/shrinks. */
    public static final class PaddleView {
        public final Entity entity;
        public final Rectangle shape;

        public PaddleView(Entity entity, Rectangle shape) {
            this.entity = entity;
            this.shape = shape;
        }
    }

    public PaddleView createPaddleView(Paddle paddle) {
        Rectangle shape = new Rectangle(paddle.getWidth(), paddle.getHeight());
        shape.setFill(PADDLE_COLOR);
        shape.setArcWidth(10);
        shape.setArcHeight(10);
        shape.setEffect(new DropShadow(6, Color.rgb(45, 52, 54, 0.35)));

        Entity entity = entityBuilder()
                .at(paddle.getX(), paddle.getY())
                .view(shape)
                .buildAndAttach();

        return new PaddleView(entity, shape);
    }

    public Entity createBallView(Ball ball) {
        Circle shape = new Circle(ball.getRadius(), BALL_COLOR);
        // Circle draws from its own center, but our model's x/y is the
        // top-left of its bounding box, so we offset the circle to line up.
        shape.setCenterX(ball.getRadius());
        shape.setCenterY(ball.getRadius());
        shape.setEffect(new DropShadow(5, Color.rgb(45, 52, 54, 0.35)));

        return entityBuilder()
                .at(ball.getX(), ball.getY())
                .view(shape)
                .buildAndAttach();
    }

    public Entity createPowerUpView(PowerUp powerUp) {
        Rectangle background = new Rectangle(powerUp.getWidth(), powerUp.getHeight());
        background.setFill(Color.web(powerUp.getColorHex()));
        background.setArcWidth(8);
        background.setArcHeight(8);
        background.setStroke(Color.WHITE);
        background.setStrokeWidth(1.5);
        background.setEffect(new DropShadow(5, Color.rgb(45, 52, 54, 0.3)));

        Text label = new Text(powerUp.getLabel());
        label.setFill(Color.WHITE);
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 7));
        label.setX(2);
        label.setY(powerUp.getHeight() / 2.0 + 3);

        Group view = new Group(background, label);

        return entityBuilder()
                .at(powerUp.getX(), powerUp.getY())
                .view(view)
                .buildAndAttach();
    }
}
