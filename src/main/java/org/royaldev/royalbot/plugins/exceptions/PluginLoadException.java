package org.royaldev.royalbot.plugins.exceptions;

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
