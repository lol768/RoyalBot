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

    public Config() {
        try {
            configFile = new File(URLDecoder.decode(RoyalBot.class.getProtectionDomain().getCodeSource().getLocation().toURI().resolve(".").getPath(), "UTF-8"), "config.yml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (configFile == null) yc = new YamlConfiguration();
        else yc = YamlConfiguration.loadConfiguration(configFile);
    }

    private void save() {
        try {
            yc.save(configFile);
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
        final Set<String> admins = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        admins.addAll(yc.getStringList("admins"));
        if (!getSuperAdmin().isEmpty()) admins.add(getSuperAdmin());
        return admins;
    }

    public void setAdmins(Set<String> admins) {
        final List<String> newAdmins = new ArrayList<String>(admins);
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

    public List<String> getChannels() {
        return yc.getStringList("channels");
    }

    public void setChannels(List<String> channels) {
        yc.set("channels", channels);
        save();
    }

    public String getSuperAdmin() {
        return yc.getString("superadmin", "");
    }

}
