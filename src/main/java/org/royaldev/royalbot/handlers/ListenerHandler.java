package org.royaldev.royalbot.handlers;

import org.royaldev.royalbot.listeners.IRCListener;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class for registering and retrieving {@link org.royaldev.royalbot.listeners.IRCListener}s.
 */
public class ListenerHandler implements Handler<IRCListener, String> {

    private final Map<String, IRCListener> listeners = new TreeMap<>();

    /**
     * Registers a listener into the ListenerHandler.
     * <br/>
     * <strong>Note:</strong> If a listener with the same name is already registered, this method will <em>not</em>
     * register your listener.
     *
     * @param listener Listener to be registered
     * @return If listener was registered
     */
    @Override
    public boolean register(IRCListener listener) {
        final String name = listener.getName().toLowerCase();
        synchronized (listeners) {
            if (listeners.containsKey(name)) return false;
            listeners.put(name, listener);
        }
        return true;
    }

    @Override
    public boolean unregister(IRCListener listener) {
        return unregister(listener.getName());
    }

    /**
     * Removes a registered listener by its name. Case does not matter.
     * <br/>
     * If no listener is registered under the provided name, this method does nothing.
     *
     * @param name Name to remove
     */
    public boolean unregister(String name) {
        name = name.toLowerCase();
        boolean wasRemoved = false;
        synchronized (listeners) {
            if (listeners.containsKey(name)) {
                listeners.remove(name);
                wasRemoved = true;
            }
        }
        return wasRemoved;
    }

    /**
     * Gets a listener for the listener name. Case does not matter.
     *
     * @param name Name of the listener to get
     * @return IRCListener, or null if none registered
     */
    public IRCListener get(String name) {
        name = name.toLowerCase();
        synchronized (listeners) {
            if (listeners.containsKey(name)) return listeners.get(name);
        }
        return null;
    }

    /**
     * Gets all listeners registered.
     *
     * @return Collection
     */
    public Collection<IRCListener> getAll() {
        synchronized (listeners) {
            return listeners.values();
        }
    }
}
