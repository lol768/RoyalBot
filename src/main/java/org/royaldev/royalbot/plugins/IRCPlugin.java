package org.royaldev.royalbot.plugins;

import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.configuration.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public abstract class IRCPlugin implements Plugin {

    private YamlConfiguration yc = null;
    private PluginDescription pd = null;
    private Logger logger = null;
    private RoyalBot rb = null;

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    /**
     * Gets the configuration for this plugin. It will be in &lt;botPath&gt;/plugins/&lt;pluginName&gt;/config.yml
     * <br/>
     * If the config does not exist, it will be created.
     *
     * @return Configuration
     * @throws java.lang.RuntimeException If any errors occur while creating config
     */
    public YamlConfiguration getConfig() {
        if (yc == null) {
            final String name = getPluginDescription().getName();
            if (name == null) throw new RuntimeException("Plugin name was null");
            final String botPath = RoyalBot.getInstance().getPath();
            if (botPath == null) throw new RuntimeException("The path to the bot was null!");
            final File configDirectory = new File(botPath, name);
            if (!configDirectory.exists() && !configDirectory.mkdirs())
                throw new RuntimeException("Couldn't create plugin config for " + name);
            if (configDirectory.isFile())
                throw new RuntimeException("Config directory for " + name + " is a file!");
            final File configPath = new File(configDirectory, "config.yml");
            try {
                if (!configPath.exists() && !configPath.createNewFile())
                    throw new RuntimeException("Couldn't create config.yml for " + name + "!");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            if (!configPath.isFile()) throw new RuntimeException("config.yml for " + name + " is not a file!");
            yc = YamlConfiguration.loadConfiguration(configPath);
        }
        return yc;
    }

    public PluginDescription getPluginDescription() {
        return pd;
    }

    public RoyalBot getBot() {
        return rb;
    }

    public Logger getLogger() {
        return logger;
    }

    void init(RoyalBot rb, PluginDescription pd) {
        this.pd = pd;
        this.rb = rb;
        this.logger = new PluginLogger(this);
    }
}
