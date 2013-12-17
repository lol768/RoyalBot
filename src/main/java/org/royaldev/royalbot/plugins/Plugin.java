package org.royaldev.royalbot.plugins;

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
