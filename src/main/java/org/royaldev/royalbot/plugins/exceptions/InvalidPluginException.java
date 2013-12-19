package org.royaldev.royalbot.plugins.exceptions;

/**
 * Exception to be used when a supplied plugin is invalid and cannot be loaded.
 */
public class InvalidPluginException extends PluginException {

    public InvalidPluginException(String reason) {
        super(reason);
    }

    public InvalidPluginException(Throwable cause) {
        super(cause);
    }

    public InvalidPluginException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
