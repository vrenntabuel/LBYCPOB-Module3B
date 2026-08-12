package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer;

/**
 * GameEventListener.java
 * =======================
 * Implements the "Observer" side of the OBSERVER DESIGN PATTERN.
 * Any class that wants to react to something happening in the game
 * (an enemy dying, the player taking damage, a wave finishing) implements
 * this interface and registers itself with the GameEventBus.
 *
 * NOTE:
 * Without the Observer pattern, our Player/EnemyCredit code would need to
 * directly call things like "hud.updateHealthBar()" and
 * "audioManager.playExplosion()" every time something happened - tightly
 * coupling gameplay code to UI/audio code. With the Observer pattern,
 * gameplay code just shouts "this event happened!" into the GameEventBus
 * and does not need to know or care who (if anyone) is listening.
 */
public interface GameEventListener {

    /**
     * Called by GameEventBus whenever a subscribed-to event fires.
     *
     * @param event the event type that occurred
     * @param payload optional extra data (e.g. the enemy's display name,
     *                or how much damage was dealt) - may be null
     */
    void onGameEvent(GameEventType event, Object payload);
}
