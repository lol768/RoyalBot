package org.royaldev.royalbot;

import org.royaldev.royalbot.handlers.Handler;
import org.royaldev.royalbot.plugins.Plugin;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

// Must stay in main package and be package-local
public class PluginHandler implements Handler<Plugin> {

    PluginHandler() {}

    private final Map<String, Plugin> plugins = new TreeMap<>();

    @Override
    public boolean register(Plugin plugin) {
        final String name = plugin.getPluginDescription().getName();
        synchronized (plugins) {
            if (plugins.containsKey(name)) return false;
            plugins.put(name, plugin);
        }
        return true;
    }

    @Override
    public boolean unregister(Plugin plugin) {
        return unregister(plugin.getPluginDescription().getName());
    }

    public boolean unregister(String name) {
        synchronized (plugins) {
            if (!plugins.containsKey(name)) return false;
            plugins.remove(name);
        }
        return true;
    }

    @Override
    public Plugin get(String name) {
        if (name == null) return null;
        synchronized (plugins) {
            return plugins.get(name);
        }
    }

    @Override
    public Collection<Plugin> getAll() {
        synchronized (plugins) {
            return plugins.values();
        }
    }
}
