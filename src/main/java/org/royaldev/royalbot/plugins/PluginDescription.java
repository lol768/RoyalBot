package org.royaldev.royalbot.plugins;

import org.royaldev.royalbot.configuration.YamlConfiguration;

/**
 * Class containing data contained in a plugin's plugin.yml.
 */
public class PluginDescription {

    private final YamlConfiguration yc;

    protected PluginDescription(YamlConfiguration yc) {
        this.yc = yc;
    }

    /**
     * Gets the name of the plugin. If it is not set, this will return an empty string; however, this should always be
     * set.
     *
     * @return Name
     */
    public String getName() {
        return yc.getString("name", "");
    }

    /**
     * Gets the main class of the plugin (e.g. "com.example.Plugin"). If it is not set, this will return an empty
     * string; however, this should always be set.
     *
     * @return Main class
     */
    public String getMain() {
        return yc.getString("main", "");
    }

    /**
     * Gets the version of this plugin. If it is not set, this will return an empty string.
     *
     * @return Version
     */
    public String getVersion() {
        return yc.getString("version", "");
    }

}
