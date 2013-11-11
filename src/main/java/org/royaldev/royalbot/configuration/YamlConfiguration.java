package org.royaldev.royalbot.configuration;

import org.apache.commons.lang3.Validate;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * An implementation of {@link Configuration} which saves all files in Yaml.
 * Note that this implementation is not synchronized.
 */
public class YamlConfiguration extends FileConfiguration {
    protected static final String BLANK_CONFIG = "{}\n";
    private final DumperOptions yamlOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter();
    private final Yaml yaml = new Yaml(new SafeConstructor(), yamlRepresenter, yamlOptions);

    @Override
    public String saveToString() {
        yamlOptions.setIndent(2);
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        String dump = yaml.dump(getValues(false));
        if (dump.equals(BLANK_CONFIG)) dump = "";
        return  dump;
    }

    @Override
    public void loadFromString(String contents) throws IOException {
        Validate.notNull(contents, "Contents cannot be null");
        Map<?, ?> input;
        try {
            input = (Map<?, ?>) yaml.load(contents);
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
            if (value instanceof Map) convertMapsToSections((Map<?, ?>) value, section.createSection(key));
            else section.set(key, value);
        }
    }

    /**
     * Creates a new {@link YamlConfiguration}, loading from the given file.
     * <p/>
     * Any errors loading the Configuration will be logged and then ignored.
     * If the specified input is not a valid config, a blank config will be returned.
     *
     * @param file Input file
     * @return Resulting configuration
     * @throws IllegalArgumentException Thrown if file is null
     */
    public static YamlConfiguration loadConfiguration(File file) {
        Validate.notNull(file, "File cannot be null");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return config;
    }

    /**
     * Creates a new {@link YamlConfiguration}, loading from the given stream.
     * <p/>
     * Any errors loading the Configuration will be logged and then ignored.
     * If the specified input is not a valid config, a blank config will be returned.
     *
     * @param stream Input stream
     * @return Resulting configuration
     * @throws IllegalArgumentException Thrown if stream is null
     */
    public static YamlConfiguration loadConfiguration(InputStream stream) {
        Validate.notNull(stream, "Stream cannot be null");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(stream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return config;
    }

    public class YamlRepresenter extends Representer {

        public YamlRepresenter() {
            this.multiRepresenters.put(ConfigurationSection.class, new RepresentConfigurationSection());
        }

        private class RepresentConfigurationSection extends RepresentMap {
            @Override
            public Node representData(Object data) {
                return super.representData(((ConfigurationSection) data).getValues(false));
            }
        }
    }
}
