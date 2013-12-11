package org.royaldev.royalbot.configuration;

import org.royaldev.royalbot.RoyalBot;

import java.util.List;

public class ChannelPreferences {

    private final RoyalBot rb = RoyalBot.getInstance();
    private final String channel;
    private ConfigurationSection cs;

    public ChannelPreferences(String channel) {
        this.channel = channel.toLowerCase();
        cs = rb.getConfig().getChannelPreferences().getConfigurationSection(this.channel);
        if (cs == null) cs = rb.getConfig().getChannelPreferences().createSection(this.channel);
    }

    public String getChannel() {
        return channel;
    }

    public List<String> getDisabledCommands() {
        return cs.getStringList("disabled-commands");
    }

    public void setDisabledCommands(List<String> commands) {
        cs.set("disabled-commands", commands);
        rb.getConfig().save();
    }

    public List<String> getDisabledListeners() {
        return cs.getStringList("disabled-listeners");
    }

    public void setDisabledListeners(List<String> listeners) {
        cs.set("disabled-listeners", listeners);
        rb.getConfig().save();
    }

    public List<String> getIgnores() {
        return cs.getStringList("ignores");
    }

    public void setIgnores(List<String> ignores) {
        cs.set("ignores", ignores);
        rb.getConfig().save();
    }

}
