package org.royaldev.royalbot.plugins.exceptions;

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
