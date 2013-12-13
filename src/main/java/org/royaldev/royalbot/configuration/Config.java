package org.royaldev.royalbot.configuration;

import org.royaldev.royalbot.RoyalBot;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Config {

    private File configFile = null;
    private final YamlConfiguration yc;

    public Config(String path) {
        try {
            configFile = (path == null) ? new File(URLDecoder.decode(RoyalBot.class.getProtectionDomain().getCodeSource().getLocation().toURI().resolve(".").getPath(), "UTF-8"), "config.yml") : new File(path);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (configFile == null) yc = new YamlConfiguration();
        else yc = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Saves the config to the disk.
     */
    public synchronized void save() {
        try {
            yc.save(configFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads the config from the disk. Discards any unsaved changes.
     */
    public synchronized void load() {
        try {
            yc.load(configFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns case-insensitive map of admins.
     *
     * @return Map
     */
    public Set<String> getAdmins() {
        final Set<String> admins = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        admins.addAll(yc.getStringList("admins"));
        if (!getSuperAdmin().isEmpty()) admins.add(getSuperAdmin());
        return admins;
    }

    public void setAdmins(Set<String> admins) {
        final List<String> newAdmins = new ArrayList<>(admins);
        if (!getSuperAdmin().isEmpty() && newAdmins.contains(getSuperAdmin())) newAdmins.remove(getSuperAdmin());
        yc.set("admins", newAdmins);
        save();
    }

    public String getYouTubeAPIKey() {
        return yc.getString("youtube.api-key", "");
    }

    public boolean getYouTubeEnabled() {
        return yc.getBoolean("youtube.enabled", false);
    }

    public String getWolframAlphaAPIKey() {
        return yc.getString("wolframalpha.api-key", "");
    }

    public boolean getWolframAlphaEnabled() {
        return yc.getBoolean("wolframalpha.enabled", false);
    }

    public boolean getDictionaryAPIEnabled() {
        return yc.getBoolean("dictionaryapi.enabled", false);
    }

    public String getDictionaryAPIKey() {
        return yc.getString("dictionaryapi.api-key", "");
    }

    public List<String> getChannels() {
        return yc.getStringList("channels");
    }

    public void setChannels(List<String> channels) {
        yc.set("channels", channels);
        save();
    }

    public List<String> getBaxFax() {
        return yc.getStringList("baxfax");
    }

    public List<String> getIgnores() {
        return yc.getStringList("ignores");
    }

    public void setIgnores(List<String> ignores) {
        yc.set("ignores", ignores);
        save();
    }

    public String getSuperAdmin() {
        return yc.getString("superadmin", "");
    }

    public ConfigurationSection getChannelCommands() {
        ConfigurationSection cs = yc.getConfigurationSection("channel-commands");
        if (cs == null) cs = yc.createSection("channel-commands");
        return cs;
    }

    public ConfigurationSection getChannelPreferences() {
        ConfigurationSection cs = yc.getConfigurationSection("channel-preferences");
        if (cs == null) cs = yc.createSection("channel-preferences");
        return cs;
    }

}
