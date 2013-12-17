package org.royaldev.royalbot.handlers;

import java.util.Collection;

/**
 * The interface for all Handlers. A Handler takes objects of the type parameter and registers them for access at later
 * points in time.
 *
 * @param <T> Type of object to be handled by this Handler
 */
public interface Handler<T> {

    /**
     * Registers the object in this Handler.
     *
     * @param obj Object to register
     * @return If object was registered
     */
    public boolean register(T obj);

    /**
     * Unregisters the object from this Handler.
     *
     * @param obj Object to unregister
     * @return If object was unregistered
     */
    public boolean unregister(T obj);

    /**
     * Gets a registered object in this Handler by its identifier.
     *
     * @param identifier Identifier attached to a registered object
     * @return Object if an object was registered with the supplied identifier, null if otherwise
     */
    public T get(String identifier);

    /**
     * Gets all registered objects in this Handler.
     *
     * @return Collection of all registered objects in this Handler. Never null
     */
    public Collection<T> getAll();

}
