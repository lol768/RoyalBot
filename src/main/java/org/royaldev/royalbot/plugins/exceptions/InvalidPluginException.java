package org.royaldev.royalbot.plugins.exceptions;

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
