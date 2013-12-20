package org.royaldev.royalbot.plugins;

import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.plugins.exceptions.InvalidPluginException;
import org.royaldev.royalbot.plugins.exceptions.PluginException;
import org.royaldev.royalbot.plugins.exceptions.PluginLoadException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that loads plugins into the bot.
 */
public class PluginLoader {

    private final RoyalBot rb;
    private PluginClassLoader pcl;

    public PluginLoader(RoyalBot instance) {
        rb = instance;
        try {
            pcl = new PluginClassLoader(new URL[]{new File(rb.getPath(), "plugins").toURI().toURL()});
        } catch (Exception ex) {
            pcl = null;
        }
    }

    /**
     * Loads a plugin and registers it in the PluginHandler. The file given must be from within the official plugins
     * folder or an Exception will be thrown.
     *
     * @param file File representing plugin JAR
     * @return Loaded plugin
     * @throws org.royaldev.royalbot.plugins.exceptions.PluginLoadException    If there is an issue that prevents the
     *                                                                         loading of the plugin
     * @throws org.royaldev.royalbot.plugins.exceptions.InvalidPluginException If there is an issue with the plugin
     *                                                                         and its structure or setup
     */
    public Plugin loadPlugin(File file) throws PluginLoadException, InvalidPluginException {
        final PluginDescription pd;
        try {
            pd = pcl.loadAndScanJar(file);
        } catch (Exception ex) {
            throw new PluginLoadException("Could not load plugin! (" + file.getName() + ")", ex);
        }
        rb.getLogger().info("Loading plugin " + pd.getName());
        final Class<?> c;
        try {
            c = Class.forName(pd.getMain(), true, pcl);
        } catch (ClassNotFoundException ex) {
            throw new InvalidPluginException("Couldn't load plugin " + pd.getName() + ": No main class " + pd.getMain(), ex);
        }
        if (!Plugin.class.isAssignableFrom(c))
            throw new InvalidPluginException("Couldn't load plugin " + pd.getName() + ": Doesn't implement Plugin interface");
        final Class<? extends IRCPlugin> pluginClass = c.asSubclass(IRCPlugin.class);
        final IRCPlugin plugin;
        try {
            final Constructor constructor = pluginClass.getDeclaredConstructor();
            plugin = (IRCPlugin) constructor.newInstance();
        } catch (Exception ex) {
            throw new PluginLoadException("Could not load plugin " + pd.getName() + ": Couldn't construct plugin", ex);
        }
        plugin.init(rb, pd);
        rb.getLogger().info("Loaded " + pd.getName());
        rb.getPluginHandler().register(plugin);
        return plugin;
    }

    /**
     * Loads all plugins in the default plugins directory.
     *
     * @return Array of loaded plugins
     */
    public Plugin[] loadPlugins() {
        final File f;
        try {
            f = new File(pcl.getURLs()[0].toURI());
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Plugin[0];
        }
        if (!f.exists() || !f.isDirectory()) return new Plugin[0]; // no plugins to be loaded, obviously
        List<Plugin> plugins = new ArrayList<>();
        for (String name : f.list()) {
            if (!name.endsWith(".jar")) continue;
            Plugin plugin;
            try {
                plugin = loadPlugin(new File(f, name));
            } catch (PluginException ex) {
                rb.getLogger().severe("Exception while loading plugin: " + ex.getMessage());
                ex.printStackTrace();
                continue;
            }
            plugins.add(plugin);
        }
        return plugins.toArray(new Plugin[plugins.size()]);
    }

    public void enablePlugins() {
        for (Plugin plugin : rb.getPluginHandler().getAll()) {
            rb.getLogger().info("Enabling " + plugin.getPluginDescription().getName());
            try {
                plugin.onEnable();
            } catch (Throwable t) {
                rb.getLogger().severe("Exception while enabling plugin " + plugin.getPluginDescription().getName());
                t.printStackTrace();
            }
        }
    }

    public void disablePlugins() {
        for (Plugin plugin : rb.getPluginHandler().getAll()) {
            rb.getLogger().info("Disabling " + plugin.getPluginDescription().getName());
            try {
                plugin.onDisable();
            } catch (Throwable t) {
                rb.getLogger().severe("Exception while disabling plugin " + plugin.getPluginDescription().getName());
                t.printStackTrace();
            }
        }
    }

}
