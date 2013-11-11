package org.royaldev.royalbot.configuration;

import java.util.Map;

/**
 * Represents a source of configurable options and settings
 */
public interface Configuration extends ConfigurationSection {
    /**
     * Sets the default value of the given path as provided.
     * <p>
     * If no source {@link Configuration} was provided as a default collection,
     * then a new {@link MemoryConfiguration} will be created to hold the new default
     * value.
     * <p>
     * If value is null, the value will be removed from the default Configuration source.
     *
     * @param path Path of the value to set.
     * @param value Value to set the default to.
     * @throws IllegalArgumentException Thrown if path is null.
     */
    public void addDefault(String path, Object value);

    /**
     * Sets the default values of the given paths as provided.
     * <p>
     * If no source {@link Configuration} was provided as a default collection,
     * then a new {@link MemoryConfiguration} will be created to hold the new default
     * values.
     *
     * @param defaults A map of Path->Values to add to defaults.
     * @throws IllegalArgumentException Thrown if defaults is null.
     */
    public void addDefaults(Map<String, Object> defaults);

    /**
     * Gets the source {@link Configuration} for this configuration.
     * <p>
     * If no configuration source was set, but default values were added, then a
     * {@link MemoryConfiguration} will be returned. If no source was set and no
     * defaults were set, then this method will return null.
     *
     * @return Configuration source for default values, or null if none exist.
     */
    public Configuration getDefaults();
}
