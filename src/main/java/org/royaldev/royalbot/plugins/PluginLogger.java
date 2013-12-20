package org.royaldev.royalbot.plugins;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Logger that prepends the plugin's name to the message.
 */
class PluginLogger extends Logger {

    private final String prefix;

    PluginLogger(IRCPlugin plugin) {
        super(plugin.getClass().getCanonicalName(), null);
        super.setParent(plugin.getBot().getLogger());
        super.setUseParentHandlers(true);
        prefix = "[" + plugin.getPluginDescription().getName() + "] ";
    }

    @Override
    public void log(LogRecord record) {
        record.setMessage(prefix + record.getMessage());
        super.log(record);
    }
}
