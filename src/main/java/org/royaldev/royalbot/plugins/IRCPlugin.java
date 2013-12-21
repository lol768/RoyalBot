package org.royaldev.royalbot.plugins;

import com.google.common.io.Files;
import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.configuration.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This should be extended in order to create plugins.
 */
public abstract class IRCPlugin implements Plugin {

    private YamlConfiguration yc = null;
    private PluginDescription pd = null;
    private PluginClassLoader pcl = null;
    private File dataFolder = null;
    private File configPath = null;
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
        if (yc == null) reloadConfig();
        return yc;
    }

    /**
     * Saves the configuration for this plugin.
     */
    public void saveConfig() {
        try {
            yc.save(configPath);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + configPath, ex);
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * Reloads the configuration for this plugin.
     */
    public void reloadConfig() {
        if (!configPath.exists()) {
            try {
                Files.createParentDirs(configPath);
                if (!configPath.createNewFile()) yc = new YamlConfiguration();
            } catch (IOException ex) {
                yc = new YamlConfiguration();
            }
        }
        yc = YamlConfiguration.loadConfiguration(configPath);
        final InputStream internal = getResource("config.yml");
        if (internal == null) return;
        yc.setDefaults(YamlConfiguration.loadConfiguration(internal));
    }

    /**
     * Saves the default internal configuration of the plugin if one doesn't already exist.
     */
    public void saveDefaultConfig() {
        if (configPath == null) return;
        if (!configPath.exists()) saveResource("config.yml", false);
    }

    /**
     * Gets a resource from the plugin JAR.
     *
     * @param path Path in JAR
     * @return InputStream of resource or null if not found
     */
    public InputStream getResource(String path) {
        final URL resource = pcl.findResource(path);
        if (resource == null) return null;
        try {
            URLConnection urlConnection = resource.openConnection();
            urlConnection.setUseCaches(false);
            return urlConnection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Saves a resource from the plugin JAR to the disk.
     *
     * @param path    Path in JAR
     * @param replace Replace existing files?
     */
    public void saveResource(String path, boolean replace) {
        final InputStream is = getResource(path);
        if (is == null) throw new IllegalArgumentException("No such resource: " + path);
        File f = new File(dataFolder, path);
        if (!f.exists() && !f.getParentFile().mkdirs())
            throw new RuntimeException("Could not make parent directories for " + path + ".");
        if (f.exists() && !replace) throw new RuntimeException("Could not save " + path + " because file exists.");
        try {
            final FileOutputStream fos = new FileOutputStream(f);
            int read;
            while ((read = is.read()) > -1) fos.write(read);
            fos.flush();
            fos.close();
            is.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets the folder in which this plugin may store data. May not be created.
     *
     * @return File object containing folder
     */
    public File getDataFolder() {
        return this.dataFolder;
    }

    /**
     * Gets a PluginDescription containing the official data in the plugin.yml of this plugin.
     *
     * @return PluginDescription
     */
    public PluginDescription getPluginDescription() {
        return pd;
    }

    /**
     * Gets the bot that this plugin is registered with.
     *
     * @return RoyalBot
     */
    public RoyalBot getBot() {
        return rb;
    }

    /**
     * Gets a Logger for this plugin. This logger will prepend the name of the plugin to all logged data, allowing for
     * easier identification.
     *
     * @return Logger specifically for this plugin
     */
    public Logger getLogger() {
        return logger;
    }

    void init(RoyalBot rb, PluginDescription pd, PluginClassLoader pcl, File dataFolder) {
        this.pd = pd;
        this.rb = rb;
        this.pcl = pcl;
        this.dataFolder = dataFolder;
        this.configPath = new File(dataFolder, "config.yml");
        this.logger = new PluginLogger(this);
    }
}
