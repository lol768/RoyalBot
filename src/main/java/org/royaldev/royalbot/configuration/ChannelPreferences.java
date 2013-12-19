package org.royaldev.royalbot.configuration;

import org.royaldev.royalbot.RoyalBot;

import java.util.List;

/**
 * Contains channel-specific preferences.
 */
public class ChannelPreferences {

    private final RoyalBot rb = RoyalBot.getInstance();
    private final String channel;
    private ConfigurationSection cs;

    public ChannelPreferences(String channel) {
        this.channel = channel.toLowerCase();
        cs = rb.getConfig().getChannelPreferences().getConfigurationSection(this.channel);
        if (cs == null) cs = rb.getConfig().getChannelPreferences().createSection(this.channel);
    }

    /**
     * Gets the name of the channel that these preferences correspond to.
     *
     * @return Channel name
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Gets the list of disabled command names for the channel.
     *
     * @return List of command names
     */
    public List<String> getDisabledCommands() {
        return cs.getStringList("disabled-commands");
    }

    /**
     * Sets the list of disabled command names for the channel.
     *
     * @param commands List of command names to disable
     */
    public void setDisabledCommands(List<String> commands) {
        cs.set("disabled-commands", commands);
        rb.getConfig().save();
    }

    /**
     * Gets the list of disabled listener names for the channel.
     *
     * @return List of listener names
     */
    public List<String> getDisabledListeners() {
        return cs.getStringList("disabled-listeners");
    }

    /**
     * Sets the list of disabled listener names for the channel.
     *
     * @param listeners List of listener names to disable
     */
    public void setDisabledListeners(List<String> listeners) {
        cs.set("disabled-listeners", listeners);
        rb.getConfig().save();
    }

    /**
     * Gets the list of hostmasks that the bot ignores in the channel.
     *
     * @return List of hostmasks
     */
    public List<String> getIgnores() {
        return cs.getStringList("ignores");
    }

    /**
     * Sets the list of ignored hostmasks for the channel.
     *
     * @param ignores List of hostmasks to ignore
     */
    public void setIgnores(List<String> ignores) {
        cs.set("ignores", ignores);
        rb.getConfig().save();
    }

}
