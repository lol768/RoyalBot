package org.royaldev.royalbot.plugins.exceptions;

/**
 * An exception to be used when a plugin has an error while loading.
 */
public class PluginLoadException extends PluginException {

    public PluginLoadException(String reason) {
        super(reason);
    }

    public PluginLoadException(Throwable cause) {
        super(cause);
    }

    public PluginLoadException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
