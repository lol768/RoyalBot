package org.royaldev.royalbot;

import org.royaldev.royalbot.commands.IRCCommand;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class CommandHandler {

    private final Map<String, IRCCommand> commands = new TreeMap<>();
    // Alias, Command
    private final Map<String, String> aliasCommands = new TreeMap<>();

    /**
     * Registers a command into the CommandHandler.
     * <br/>
     * <strong>Note:</strong> If a command with the same name is already registered, this method will <em>not</em>
     * register your command.
     *
     * @param command Command to be registered
     * @return If command was registered
     */
    public boolean registerCommand(IRCCommand command) {
        final String name = command.getName().toLowerCase();
        synchronized (commands) {
            if (commands.containsKey(name)) return false;
            commands.put(name, command);
        }
        for (String alias : command.getAliases()) {
            alias = alias.toLowerCase();
            synchronized (aliasCommands) {
                if (aliasCommands.containsKey(alias)) continue;
                aliasCommands.put(alias, name);
            }
        }
        return true;
    }

    /**
     * Removes a registered command by its name. Case does not matter.
     * <br/>
     * If no command is registered under the provided name, this method does nothing.
     *
     * @param name Name to remove
     */
    public void unregisterCommand(String name) {
        name = name.toLowerCase();
        synchronized (commands) {
            if (commands.containsKey(name)) commands.remove(name);
        }
        synchronized (aliasCommands) {
            if (aliasCommands.containsKey(name)) aliasCommands.remove(name);
        }
    }

    /**
     * Gets a command for the command name. Case does not matter.
     *
     * @param name Name of the command to get
     * @return IRCCommand, or null if none registered
     */
    public IRCCommand getCommand(String name) {
        name = name.toLowerCase();
        synchronized (commands) {
            if (commands.containsKey(name)) return commands.get(name);
            synchronized (aliasCommands) {
                if (aliasCommands.containsKey(name)) return getCommand(aliasCommands.get(name));
            }
        }
        return null;
    }

    /**
     * Gets all commands registered.
     *
     * @return Collection
     */
    public Collection<IRCCommand> getAllCommands() {
        synchronized (commands) {
            return commands.values();
        }
    }
}
