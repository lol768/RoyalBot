package org.royaldev.royalbot.plugins;

import org.royaldev.royalbot.configuration.YamlConfiguration;
import org.royaldev.royalbot.plugins.exceptions.InvalidPluginException;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Package-local {@link java.net.URLClassLoader} that loads plugins.
 */
class PluginClassLoader extends URLClassLoader {

    public PluginClassLoader(URL[] urls) {
        super(urls);
    }

    public PluginDescription loadAndScanJar(File jarFile) throws Exception {
        super.addURL(jarFile.toURI().toURL());
        JarFile jar = new JarFile(jarFile);
        ZipEntry pluginYml = jar.getEntry("plugin.yml");
        if (pluginYml == null)
            throw new InvalidPluginException("Plugin has no plugin.yml! (" + jarFile.getName() + ")");
        final YamlConfiguration yc = YamlConfiguration.loadConfiguration(jar.getInputStream(pluginYml));
        if (!yc.contains("name"))
            throw new InvalidPluginException("plugin.yml has no name! (" + jarFile.getName() + ")");
        if (!yc.contains("main")) throw new InvalidPluginException(yc.getString("name") + " has no main class.");
        Enumeration<? extends JarEntry> enumeration = jar.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = enumeration.nextElement();
            if (zipEntry.getName().endsWith(".class")) {
                String className = zipEntry.getName();
                className = className.replace(".class", "").replace("/", ".");
                if (className.startsWith("org.royaldev.royalbot")) continue; // no hijacks, please
                loadClass(className);
            }
        }
        return new PluginDescription(yc);
    }

}
