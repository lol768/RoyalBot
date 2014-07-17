package org.royaldev.royalbot.configuration;

import org.royaldev.royalbot.RoyalBot;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The bot's main configuration.
 */
public class Config {

    private final YamlConfiguration yc;
    private File configFile = null;

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

    /**
     * Sets the admins on the bot.
     *
     * @param admins Admins to set
     */
    public void setAdmins(Set<String> admins) {
        final List<String> newAdmins = new ArrayList<>(admins);
        if (!getSuperAdmin().isEmpty() && newAdmins.contains(getSuperAdmin())) newAdmins.remove(getSuperAdmin());
        yc.set("admins", newAdmins);
        save();
    }

    /**
     * Gets the specified YouTube API key or an empty string if not set.
     *
     * @return API key or empty string
     */
    public String getYouTubeAPIKey() {
        return yc.getString("youtube.api-key", "");
    }

    /**
     * Returns if YouTube link parsing is enabled.
     *
     * @return true or false
     */
    public boolean isYouTubeEnabled() {
        return yc.getBoolean("youtube.enabled", false);
    }

    /**
     * Gets the API key for WolframAlpha or an empty string if not set.
     *
     * @return API key or empty string
     */
    public String getWolframAlphaAPIKey() {
        return yc.getString("wolframalpha.api-key", "");
    }

    /**
     * Returns if WolframAlpha is enabled.
     *
     * @return true or false
     */
    public boolean isWolframAlphaEnabled() {
        return yc.getBoolean("wolframalpha.enabled", false);
    }

    /**
     * Returns if the dictionary API is enabled.
     *
     * @return true or false
     */
    public boolean isDictionaryAPIEnabled() {
        return yc.getBoolean("dictionaryapi.enabled", false);
    }

    /**
     * Gets the API key for the dictionary API or empty string if not set.
     *
     * @return API key or empty string
     */
    public String getDictionaryAPIKey() {
        return yc.getString("dictionaryapi.api-key", "");
    }

    /**
     * Returns if Wunderground is enabled.
     *
     * @return true of false
     */
    public boolean isWundergroundEnabled() {
        return yc.getBoolean("weather.wunderground.enabled", false);
    }

    /**
     * Gets the API key for Wunderground or an empty string if not set.
     *
     * @return API key or empty string
     */
    public String getWundergroundAPIKey() {
        return yc.getString("weather.wunderground.api-key", "");
    }

    /**
     * Gets the list of channels that the bot is set to automatically join on startup.
     *
     * @return List of channels - never null
     */
    public List<String> getChannels() {
        return yc.getStringList("channels");
    }

    /**
     * Sets the list of channels that the bot is set to automatically join on startup.
     *
     * @param channels List of channels to join
     */
    public void setChannels(List<String> channels) {
        yc.set("channels", channels);
        save();
    }

    /**
     * Gets the registered facts for the "lolfax" command.
     *
     * @return List of lolfax - never null
     */
    public List<String> getLolFax() {
        return yc.getStringList("lolfax");
    }

    /**
     * Gets the list of ignored hostmasks.
     *
     * @return List of hostmasks - never null
     */
    public List<String> getIgnores() {
        return yc.getStringList("ignores");
    }

    /**
     * Sets the list of ignored hostmasks.
     *
     * @param ignores List of hostmasks to ignore
     */
    public void setIgnores(List<String> ignores) {
        yc.set("ignores", ignores);
        save();
    }

    /**
     * Gets the superadmin of the bot. For security purposes, this has no setter.
     *
     * @return Nick of superadmin
     */
    public String getSuperAdmin() {
        return yc.getString("superadmin", "");
    }

    /**
     * Gets a ConfigurationSection of {@link org.royaldev.royalbot.commands.ChannelCommand}s.
     *
     * @return ConfigurationSection
     */
    public ConfigurationSection getChannelCommands() {
        ConfigurationSection cs = yc.getConfigurationSection("channel-commands");
        if (cs == null) cs = yc.createSection("channel-commands");
        return cs;
    }

    /**
     * Gets a ConfigurationSection of channel-specific preferences.
     *
     * @return ConfigurationSection
     */
    public ConfigurationSection getChannelPreferences() {
        ConfigurationSection cs = yc.getConfigurationSection("channel-preferences");
        if (cs == null) cs = yc.createSection("channel-preferences");
        return cs;
    }

    /**
     * Gets a ConfigurationSection of letters for flipping.
     *
     * @return ConfigurationSection
     */
    public ConfigurationSection getFlipTable() {
        ConfigurationSection cs = yc.getConfigurationSection("flip");
        if (cs == null) cs = yc.createSection("flip");
        return cs;
    }

}
