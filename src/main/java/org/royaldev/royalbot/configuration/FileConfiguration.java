package org.royaldev.royalbot.configuration;


import com.google.common.io.Files;
import org.apache.commons.lang3.Validate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * This is a base class for all File based implementations of {@link Configuration}
 */
public abstract class FileConfiguration extends MemoryConfiguration {
    /**
     * Creates an empty {@link FileConfiguration} with no default values.
     */
    public FileConfiguration() {
        super();
    }

    /**
     * Saves this {@link FileConfiguration} to the specified location.
     * <p/>
     * If the file does not exist, it will be created. If already exists, it will
     * be overwritten. If it cannot be overwritten or created, an exception will be thrown.
     *
     * @param file File to save to.
     * @throws IOException              Thrown when the given file cannot be written to for any reason.
     * @throws IllegalArgumentException Thrown when file is null.
     */
    public void save(File file) throws IOException {
        Validate.notNull(file, "File cannot be null");
        Files.createParentDirs(file);
        String data = saveToString();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
        }
    }

    /**
     * Saves this {@link FileConfiguration} to a string, and returns it.
     *
     * @return String containing this configuration.
     */
    public abstract String saveToString();

    /**
     * Loads this {@link FileConfiguration} from the specified location.
     * <p/>
     * All the values contained within this configuration will be removed, leaving
     * only settings and defaults, and the new values will be loaded from the given file.
     * <p/>
     * If the file cannot be loaded for any reason, an exception will be thrown.
     *
     * @param file File to load from.
     * @throws FileNotFoundException    Thrown when the given file cannot be opened.
     * @throws IOException              Thrown when the given file cannot be read.
     * @throws IllegalArgumentException Thrown when file is null.
     */
    public void load(File file) throws IOException {
        Validate.notNull(file, "File cannot be null");
        load(new FileInputStream(file));
    }

    /**
     * Loads this {@link FileConfiguration} from the specified stream.
     * <p/>
     * All the values contained within this configuration will be removed, leaving
     * only settings and defaults, and the new values will be loaded from the given stream.
     *
     * @param stream Stream to load from
     * @throws IOException              Thrown when the given file cannot be read.
     * @throws IllegalArgumentException Thrown when stream is null.
     */
    public void load(InputStream stream) throws IOException {
        Validate.notNull(stream, "Stream cannot be null");
        InputStreamReader reader = new InputStreamReader(stream);
        StringBuilder builder = new StringBuilder();
        try (BufferedReader input = new BufferedReader(reader)) {
            String line;
            while ((line = input.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        }
        loadFromString(builder.toString());
    }

    public void loadFromString(String contents) throws IOException {
        Validate.notNull(contents, "Contents cannot be null");
        Map<?, ?> input;
        try {
            input = (Map<?, ?>) new Yaml().load(contents);
        } catch (YAMLException e) {
            throw new IOException(e);
        } catch (ClassCastException e) {
            throw new IOException("Top level is not a Map.");
        }
        if (input != null) convertMapsToSections(input, this);
    }

    protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {
        for (Map.Entry<?, ?> entry : input.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            if (value instanceof Map) {
                convertMapsToSections((Map<?, ?>) value, section.createSection(key));
            } else {
                section.set(key, value);
            }
        }
    }
}
