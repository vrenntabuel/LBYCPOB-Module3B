package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.manager;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.bullet.BulletComponent;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.AllyShipComponent;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.EnemyCreditComponent;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.EntityType;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.entity.PlayerComponent;
import ph.edu.dlsu.lbycpob.lbycpobbulletcredits.factory.BulletFactory;


import static com.almasb.fxgl.dsl.FXGL.getPhysicsWorld;

/**
 * CollisionManager.java
 * =====================
 * Registers every required collision check with FXGL's physics world:
 *   - Player  vs Enemy bullets
 *   - Player  vs Enemy credits (ramming into a name deals contact damage)
 *   - Ally    vs Enemy bullets  (bullet interception)
 *   - Player bullets vs Enemy credits
 *   - Player bullets vs Enemy bullets (shooting down destructible bullets)
 *
 * NOTE:
 * FXGL automatically checks every pair of entity types you register a
 * CollisionHandler for, every frame, using each entity's bounding box
 * (set up via .bbox(...) in the factories). We only need to describe WHAT
 * should happen when a collision is detected - FXGL handles the actual
 * geometry math for us.
 */
public class CollisionManager {

    private final BulletFactory bulletFactory;

    public CollisionManager(BulletFactory bulletFactory) {
        this.bulletFactory = bulletFactory;
    }

    /** Call once during game setup to wire up all collision rules. */
    public void registerAllHandlers() {
        registerPlayerVsEnemyBullet();
        registerAllyVsEnemyBullet();
        registerPlayerVsEnemyCredit();
        registerPlayerBulletVsEnemyCredit();
        registerPlayerBulletVsEnemyBullet();
    }

    private void registerPlayerVsEnemyBullet() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ENEMY_BULLET) {
            @Override
            protected void onCollisionBegin(Entity player, Entity bullet) {
                damage(player, 1);
                bulletFactory.recycle(bullet, false);
            }
        });
    }

    private void registerAllyVsEnemyBullet() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ALLY_SHIP, EntityType.ENEMY_BULLET) {
            @Override
            protected void onCollisionBegin(Entity ally, Entity bullet) {
                damage(ally, 1);
                bulletFactory.recycle(bullet, false);
            }
        });
    }

    private void registerPlayerVsEnemyCredit() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ENEMY_CREDIT) {
            @Override
            protected void onCollisionBegin(Entity player, Entity enemy) {
                // Ramming into a name deals contact damage to the player,
                // but does not hurt the enemy - encourages shooting instead
                // of colliding.
                damage(player, 1);
            }
        });
    }

    private void registerPlayerBulletVsEnemyCredit() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_BULLET, EntityType.ENEMY_CREDIT) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                EnemyCreditComponent enemyComponent = enemy.getComponent(EnemyCreditComponent.class);
                enemyComponent.takeDamage(1);
                bulletFactory.recycle(bullet, true);
            }
        });
    }

    /**
     * Lets the player shoot down enemy bullets - unless they are
     * INDESTRUCTIBLE, in which case the player bullet simply passes
     * through untouched and nothing happens (matching the visual language
     * set up in BulletFactory: orange bullets ignore player fire,
     * green/purple bullets react to it). See
     * BulletComponent.registerHitFromPlayerBullet().
     *
     * BEGINNER NOTE:
     * FXGL's CollisionHandler(TypeA, TypeB) is documented to always invoke
     * onCollisionBegin(entityOfTypeA, entityOfTypeB) in that order, but we
     * resolve each entity explicitly by its actual EntityType below anyway
     * rather than trusting parameter position blindly. It costs almost
     * nothing and makes it impossible for this handler to ever accidentally
     * read the WRONG entity's BulletComponent - which matters a lot here,
     * since a player bullet's own "durability" field is an unused
     * placeholder (see BulletFactory.spawnPlayerBullet) that would give a
     * nonsense answer if it were ever read by mistake.
     */
    private void registerPlayerBulletVsEnemyBullet() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_BULLET, EntityType.ENEMY_BULLET) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                Entity playerBullet = a.isType(EntityType.PLAYER_BULLET) ? a : b;
                Entity enemyBullet = a.isType(EntityType.ENEMY_BULLET) ? a : b;

                BulletComponent enemyBulletComponent = enemyBullet.getComponent(BulletComponent.class);

                if (enemyBulletComponent.isIndestructible()) {
                    // Indestructible bullets ignore player fire entirely -
                    // the player bullet is NOT consumed here, so it keeps
                    // flying and can still go on to hit something else.
                    return;
                }

                boolean destroyed = enemyBulletComponent.registerHitFromPlayerBullet();

                // The player bullet IS consumed on impact here, whether or
                // not it finished the job - otherwise "shooting down
                // bullets" would be a free action rather than a tradeoff
                // against also shooting the enemy credits themselves.
                bulletFactory.recycle(playerBullet, true);

                if (destroyed) {
                    bulletFactory.recycle(enemyBullet, false);
                }
            }
        });
    }

    /** Small helper so each handler above stays a one-liner. */
    private void damage(Entity target, int amount) {
        if (target.hasComponent(PlayerComponent.class)) {
            target.getComponent(PlayerComponent.class).takeDamage(amount);
        } else if (target.hasComponent(AllyShipComponent.class)) {
            target.getComponent(AllyShipComponent.class).takeDamage(amount);
        }
    }
}
