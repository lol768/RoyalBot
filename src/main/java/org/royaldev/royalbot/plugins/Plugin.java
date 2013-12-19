package org.royaldev.royalbot.plugins;

/**
 * This should not be implemented to create plugins to register with RoyalBot.
 *
 * @see org.royaldev.royalbot.plugins.IRCPlugin
 */
public interface Plugin {

    /**
     * Method to be run when the plugin is enabled.
     */
    public abstract void onEnable();

    /**
     * Method to be run when the plugin is disabled.
     */
    public abstract void onDisable();

    /**
     * Gets the plugin's description.
     *
     * @return PluginDescription
     */
    public abstract PluginDescription getPluginDescription();

}
