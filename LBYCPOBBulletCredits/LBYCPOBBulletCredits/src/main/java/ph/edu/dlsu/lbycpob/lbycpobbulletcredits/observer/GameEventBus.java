package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * GameEventBus.java
 * =================
 * The "Subject" in the OBSERVER DESIGN PATTERN, AND an example of the
 * SINGLETON DESIGN PATTERN at the same time - there should only ever be
 * ONE event bus for the whole game, so every class that needs it goes
 * through GameEventBus.getInstance() rather than creating its own copy.
 *
 * NOTE ON SINGLETONS:
 * The private constructor stops anyone from writing `new GameEventBus()`
 * outside this file. The single shared instance is created once, lazily,
 * the first time getInstance() is called.
 */
public class GameEventBus {

    private static GameEventBus instance;

    private final List<GameEventListener> listeners = new ArrayList<>();

    private GameEventBus() {
        // private on purpose - see class javadoc above
    }

    public static GameEventBus getInstance() {
        if (instance == null) {
            instance = new GameEventBus();
        }
        return instance;
    }

    public void subscribe(GameEventListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(GameEventListener listener) {
        listeners.remove(listener);
    }

    /** Notifies every subscribed listener. Called with no payload. */
    public void publish(GameEventType event) {
        publish(event, null);
    }

    /** Notifies every subscribed listener, passing along extra data. */
    public void publish(GameEventType event, Object payload) {
        // Copy the list before iterating in case a listener subscribes or
        // unsubscribes in response to this very event - iterating over the
        // live list while it changes would throw a ConcurrentModificationException.
        for (GameEventListener listener : new ArrayList<>(listeners)) {
            listener.onGameEvent(event, payload);
        }
    }

    /** Clears all listeners - useful when restarting the game/session. */
    public void reset() {
        listeners.clear();
    }
}
