package org.royaldev.royalbot.handlers;

import org.royaldev.royalbot.commands.IRCCommand;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class for registering and retrieving {@link org.royaldev.royalbot.commands.IRCCommand}s.
 */
public class CommandHandler implements Handler<IRCCommand> {

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
    @Override
    public boolean register(IRCCommand command) {
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
     * Removes the supplied command from the CommandHandler.
     *
     * @param command Command to be removed
     * @return If command was removed
     */
    @Override
    public boolean unregister(IRCCommand command) {
        return unregister(command.getName());
    }

    /**
     * Removes a registered command by its name. Case does not matter.
     * <br/>
     * If no command is registered under the provided name, this method does nothing.
     *
     * @param name Name to remove
     * @return If command was unregistered
     */
    public boolean unregister(String name) {
        name = name.toLowerCase();
        boolean wasRemoved = false;
        synchronized (commands) {
            if (commands.containsKey(name)) {
                commands.remove(name);
                wasRemoved = true;
            }
        }
        synchronized (aliasCommands) {
            if (aliasCommands.containsKey(name)) aliasCommands.remove(name);
        }
        return wasRemoved;
    }

    /**
     * Gets a command for the command name. Case does not matter.
     *
     * @param name Name of the command to get
     * @return IRCCommand, or null if none registered
     */
    public IRCCommand get(String name) {
        name = name.toLowerCase();
        synchronized (commands) {
            if (commands.containsKey(name)) return commands.get(name);
            synchronized (aliasCommands) {
                if (aliasCommands.containsKey(name)) return get(aliasCommands.get(name));
            }
        }
        return null;
    }

    /**
     * Gets all commands registered.
     *
     * @return Collection
     */
    public Collection<IRCCommand> getAll() {
        synchronized (commands) {
            return commands.values();
        }
    }
}
