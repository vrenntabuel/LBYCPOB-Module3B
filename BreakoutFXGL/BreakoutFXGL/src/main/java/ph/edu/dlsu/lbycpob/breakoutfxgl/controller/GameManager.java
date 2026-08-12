package ph.edu.dlsu.lbycpob.breakoutfxgl.controller;



import ph.edu.dlsu.lbycpob.breakoutfxgl.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * GameManager is the CONTROLLER in our MVC design.
 * <p>
 * MVC (Model-View-Controller) split used in this project:
 *   MODEL      -> Ball, Paddle, Brick, PowerUp and their subclasses.
 *                 Plain Java objects. They know nothing about FXGL,
 *                 JavaFX, drawing, or the screen.
 *   VIEW       -> everything in the view/ and app/ packages. It uses
 *                 FXGL to draw shapes/images on screen and turns
 *                 keyboard presses into calls on the Controller.
 *   CONTROLLER -> this class, GameManager. It owns the model objects,
 *                 runs the game rules (movement, collisions, scoring,
 *                 spawning), and exposes read-only access to the
 *                 current state so the View can draw it. GameManager
 *                 never touches JavaFX/FXGL classes directly - that
 *                 keeps the game logic reusable and easy to unit test.
 * <p>
 * COLLECTIONS: bricks, balls, and powerUps are all stored in
 * ArrayLists. ArrayList<Brick> in particular is required by the
 * assignment brief; the same List<X> pattern is reused for balls and
 * power-ups to keep the code consistent.
 */
public class GameManager {

    // --- Playfield boundaries, used for wall bounces and out-of-bounds checks ---
    private final double fieldWidth;
    private final double fieldHeight;

    // --- Model collections ---
    private final Paddle paddle;
    private final List<Ball> balls = new ArrayList<>();
    private final List<Brick> bricks = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();

    // --- Game state ---
    private int score;
    private int lives;
    private boolean gameOver;
    private boolean levelCleared;

    private final Random random = new Random();
    private static final double POWER_UP_DROP_CHANCE = 0.20; // 20% per destroyed brick
    private static final int STARTING_LIVES = 3;

    // Defaults to the "do nothing" listener (Null Object pattern - see
    // GameEventListener) so every onBallBounce()/onBrickBroken() call
    // below is always safe to make, with no null checks required.
    // BreakoutApp swaps in a real AudioManager via setGameEventListener().
    private GameEventListener eventListener = GameEventListener.NO_OP;

    public GameManager(double fieldWidth, double fieldHeight) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.lives = STARTING_LIVES;
        this.score = 0;

        this.paddle = new Paddle(fieldWidth / 2 - 60, fieldHeight - 60);

        Ball firstBall = new Ball(fieldWidth / 2, fieldHeight - 80, 10);
        balls.add(firstBall);

        buildLevel();
    }

    public static int getStartingLives() {
        return STARTING_LIVES;
    }

    /**
     * Lets the View layer plug in something that reacts to game events -
     * in this project, an AudioManager that plays sounds. Passing null
     * resets back to the silent Null Object listener instead of leaving
     * GameManager holding an actual null reference.
     */
    public void setGameEventListener(GameEventListener listener) {
        this.eventListener = (listener != null) ? listener : GameEventListener.NO_OP;
    }

    /**
     * Fills the ArrayList<Brick> with a grid of bricks. Called once at
     * the start of the game (and could be called again for a "next
     * level" feature).
     */
    private void buildLevel() {
        double brickWidth = 80;
        double brickHeight = 24;
        double gap = 6;

        double startY = 90;

        // 1 = brick, 0 = empty space
        int[][] pattern = {
                {0, 0, 1, 1, 0, 0},
                {0, 1, 1, 1, 1, 0},
                {1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 0},
                {0, 0, 1, 1, 0, 0}
        };

        int rows = pattern.length;
        int cols = pattern[0].length;

        double totalWidth =
                cols * (brickWidth + gap) - gap;

        double startX =
                (fieldWidth - totalWidth) / 2.0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                if (pattern[row][col] == 1) {

                    double x =
                            startX + col * (brickWidth + gap);

                    double y =
                            startY + row * (brickHeight + gap);

                    BrickType type;

                    if (row == 0) {
                        type = BrickType.UNBREAKABLE;
                    } else if (row == 1) {
                        type = BrickType.STRONG;
                    } else {
                        type = BrickType.NORMAL;
                    }

                    bricks.add(
                            new Brick(
                                    x,
                                    y,
                                    brickWidth,
                                    brickHeight,
                                    type
                            )
                    );
                }
            }
        }
    }

    /**
     * The main per-frame update. BreakoutApp calls this once per
     * animation frame (this is our ANIMATION LOOP hook). Everything
     * that needs to happen each frame - moving objects and checking
     * collisions - is coordinated from right here.
     */
    public void update(double deltaSeconds) {
        if (gameOver || levelCleared) {
            return;
        }

        // Move every ball and every falling power-up.
        // Because Ball and PowerUp both extend GameObject and both
        // override update(), this loop works for any GameObject list
        // without an if/else chain - POLYMORPHISM in the animation loop.
        for (Ball ball : balls) {
            ball.update(deltaSeconds);
        }
        for (PowerUp powerUp : powerUps) {
            powerUp.update(deltaSeconds);
        }

        handleWallCollisions();
        handlePaddleCollisions();
        handleBrickCollisions();
        handlePowerUpCollisions();
        removeBallsThatFellOffscreen();
        removeExpiredPowerUps();
        checkWinCondition();
    }

    // -----------------------------------------------------------------
    // COLLISION DETECTION
    // -----------------------------------------------------------------
    private void handleWallCollisions() {
        for (Ball ball : balls) {
            if (ball.getX() <= 0 || ball.getRight() >= fieldWidth) {
                ball.bounceHorizontal();
                // Clamp so the ball does not get stuck slightly past the wall.
                ball.setX(Math.max(0, Math.min(fieldWidth - ball.getWidth(), ball.getX())));
                eventListener.onBallBounce();
            }
            if (ball.getY() <= 0) {
                ball.bounceVertical();
                ball.setY(Math.max(0, ball.getY()));
                eventListener.onBallBounce();
            }
        }
    }

    private void handlePaddleCollisions() {
        for (Ball ball : balls) {
            // Only bounce if the ball is moving downward and actually
            // overlaps the paddle - this avoids a "double bounce" bug
            // where the ball could get stuck rapidly flipping direction.
            if (ball.getDy() > 0 && ball.collidesWith(paddle)) {
                double hitOffset = paddle.relativeHitPosition(ball.getX() + ball.getWidth() / 2.0);
                ball.bounceOffPaddle(hitOffset);
                // Push the ball just above the paddle so it does not sink into it.
                ball.setY(paddle.getY() - ball.getHeight());
                eventListener.onBallBounce();
            }
        }
    }

    private void handleBrickCollisions() {
        for (Ball ball : balls) {
            // We use an Iterator here (instead of a normal for-each)
            // because destroyed bricks are removed from the ArrayList
            // WHILE we are looping over it. Removing directly with
            // bricks.remove(brick) inside a for-each loop would throw
            // a ConcurrentModificationException.
            Iterator<Brick> iterator = bricks.iterator();
            while (iterator.hasNext()) {
                Brick brick = iterator.next();
                if (!ball.collidesWith(brick)) {
                    continue;
                }

                boolean destroyed = brick.hit();
                bounceBallOffBrick(ball, brick);
                eventListener.onBallBounce();

                if (destroyed) {
                    score += 100;
                    maybeSpawnPowerUp(brick);
                    iterator.remove();
                    eventListener.onBrickBroken();
                }

                // Only resolve one brick collision per ball per frame.
                break;
            }
        }
    }

    /**
     * Figures out whether the ball hit the brick from the top/bottom
     * or the left/right, bounces it accordingly, and then pushes the
     * ball fully outside the brick's rectangle along that same axis.
     * That last step is the fix for the "ball gets stuck inside a
     * brick" bug described above: reversing velocity alone is not
     * enough if the ball's position is still overlapping the brick.
     */
    private void bounceBallOffBrick(Ball ball, Brick brick) {
        double ballCenterX = ball.getX() + ball.getWidth() / 2.0;
        double ballCenterY = ball.getY() + ball.getHeight() / 2.0;
        double brickCenterX = brick.getX() + brick.getWidth() / 2.0;
        double brickCenterY = brick.getY() + brick.getHeight() / 2.0;

        double overlapX = (ball.getWidth() / 2.0 + brick.getWidth() / 2.0) - Math.abs(ballCenterX - brickCenterX);
        double overlapY = (ball.getHeight() / 2.0 + brick.getHeight() / 2.0) - Math.abs(ballCenterY - brickCenterY);

        if (overlapX < overlapY) {
            ball.bounceHorizontal();
            if (ballCenterX < brickCenterX) {
                ball.setX(brick.getX() - ball.getWidth()); // push out to the left of the brick
            } else {
                ball.setX(brick.getRight()); // push out to the right of the brick
            }
        } else {
            ball.bounceVertical();
            if (ballCenterY < brickCenterY) {
                ball.setY(brick.getY() - ball.getHeight()); // push out above the brick
            } else {
                ball.setY(brick.getBottom()); // push out below the brick
            }
        }
    }

    private void handlePowerUpCollisions() {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            if (powerUp.collidesWith(paddle)) {
                // POLYMORPHISM: we do not check "if this is a
                // WidenPaddlePowerUp, else if this is a ..." - we just
                // call applyEffect() and let the real subclass decide.
                powerUp.applyEffect(this);
                iterator.remove();
            }
        }
    }

    private void removeBallsThatFellOffscreen() {
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            if (ball.getY() > fieldHeight) {
                iterator.remove();
            }
        }

        if (balls.isEmpty()) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
                eventListener.onGameOver();
            } else {
                // Respawn a single fresh ball for the next attempt.
                balls.add(new Ball(fieldWidth / 2, fieldHeight - 80, 10));
            }
        }
    }

    private void removeExpiredPowerUps() {
        powerUps.removeIf(powerUp -> powerUp.getY() > fieldHeight);
    }

    private void checkWinCondition() {
        boolean anyBreakableBrickLeft = bricks.stream()
                .anyMatch(brick -> brick.getType() != BrickType.UNBREAKABLE);
        if (!anyBreakableBrickLeft) {
            levelCleared = true;
            eventListener.onVictory();
        }
    }

    /**
     * Randomly decides whether a destroyed brick drops a power-up, and
     * if so, which kind. Demonstrates picking among several subclasses
     * of the same parent type (PowerUp) at runtime.
     */
    private void maybeSpawnPowerUp(Brick brick) {
        if (random.nextDouble() > POWER_UP_DROP_CHANCE) {
            return;
        }

        double x = brick.getX() + brick.getWidth() / 2.0 - 11;
        double y = brick.getY();

        int choice = random.nextInt(3);
        PowerUp powerUp = switch (choice) {
            case 0 -> new WidenPaddlePowerUp(x, y);
            case 1 -> new ShrinkPaddlePowerUp(x, y);
            default -> new MultiBallPowerUp(x, y);
        };
        powerUps.add(powerUp);
    }

    /** Called by MultiBallPowerUp.applyEffect(). Adds a clone of an existing ball. */
    public void spawnExtraBall() {
        if (!balls.isEmpty()) {
            balls.add(balls.getFirst().copy());
        }
    }

    // -----------------------------------------------------------------
    // EVENT HANDLING ENTRY POINTS (called by the View in response to
    // keyboard events)
    // -----------------------------------------------------------------

    public void movePaddleLeft(double deltaSeconds) {
        paddle.moveLeft(deltaSeconds, 0);
    }

    public void movePaddleRight(double deltaSeconds) {
        paddle.moveRight(deltaSeconds, fieldWidth);
    }

    // -----------------------------------------------------------------
    // Read-only access for the View layer
    // -----------------------------------------------------------------

    public Paddle getPaddle() {
        return paddle;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isLevelCleared() {
        return levelCleared;
    }
}
