package org.royaldev.royalbot.listeners;

public interface IRCListener {

    /**
     * Get the name of the listener. These should all be unique, like commands.
     *
     * @return Reference name
     */
    public String getName();

}
