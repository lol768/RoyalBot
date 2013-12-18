package org.royaldev.royalbot.plugins;

import org.royaldev.royalbot.configuration.YamlConfiguration;

public class PluginDescription {

    private final YamlConfiguration yc;

    protected PluginDescription(YamlConfiguration yc) {
        this.yc = yc;
    }

    public String getName() {
        return yc.getString("name", "");
    }

    public String getMain() {
        return yc.getString("main", "");
    }

    public String getVersion() {
        return yc.getString("version", "");
    }

}
