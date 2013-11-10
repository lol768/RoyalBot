package org.royaldev.royalbot;

import org.royaldev.royalbot.commands.IRCCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    private final Map<String, IRCCommand> commands = new HashMap<String, IRCCommand>();

    public void registerCommand(IRCCommand command) {
        final String name = command.getName().toLowerCase();
        synchronized (commands) {
            if (commands.containsKey(name)) return;
            commands.put(name, command);
        }
    }

    public void unregisterCommand(String name) {
        name = name.toLowerCase();
        synchronized (commands) {
            if (!commands.containsKey(name)) return;
            commands.remove(name);
        }
    }

    public IRCCommand getCommand(String name) {
        name = name.toLowerCase();
        synchronized (commands) {
            if (!commands.containsKey(name)) return null;
            return commands.get(name);
        }
    }

    public Collection<IRCCommand> getAllCommands() {
        return commands.values();
    }
}
