package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.EntityType;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.PlayerComponent;


import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * PlayerFactory.java
 * ==================
 * FACTORY DESIGN PATTERN implementation for the player's ship. Kept as
 * its own small factory (rather than folding this into Main.java) so the
 * "how do I build a player entity" knowledge lives in exactly one place,
 * matching the same pattern used for bullets, enemies, and allies.
 */
public class PlayerFactory {

    private final BulletFactory bulletFactory;

    public PlayerFactory(BulletFactory bulletFactory) {
        this.bulletFactory = bulletFactory;
    }

    public Entity spawnPlayer(double x, double y) {
        // A small triangular spacecraft, as specified in the design brief.
        // Points are defined nose-up since "straight up" is the direction
        // the player fires.
        Polygon triangleShape = new Polygon(
                15, 0,    // nose (tip), pointing up
                0, 30,    // rear-left
                30, 30    // rear-right
        );
        triangleShape.setFill(Color.DEEPSKYBLUE);
        triangleShape.setStroke(Color.WHITE);
        triangleShape.setStrokeWidth(1.5);

        return entityBuilder()
                .type(EntityType.PLAYER)
                .at(x, y)
                .bbox(new HitBox(BoundingShape.box(30, 30)))
                .collidable()
                .view(triangleShape)
                .with(new PlayerComponent(bulletFactory))
                .buildAndAttach();
    }
}
