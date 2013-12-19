package org.royaldev.royalbot.plugins.exceptions;

/**
 * Generic exception related to a plugin.
 */
public class PluginException extends Exception {

    public PluginException(String reason) {
        super(reason);
    }

    public PluginException(Throwable cause) {
        super(cause);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

}
