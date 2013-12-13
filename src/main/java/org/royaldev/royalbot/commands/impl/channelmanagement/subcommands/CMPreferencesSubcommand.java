package org.royaldev.royalbot.commands.impl.channelmanagement.subcommands;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.commands.IRCCommand;
import org.royaldev.royalbot.configuration.ChannelPreferences;
import org.royaldev.royalbot.listeners.IRCListener;

import java.util.List;

public class CMPreferencesSubcommand implements IRCCommand {

    private final RoyalBot rb = RoyalBot.getInstance();

    @Override
    public void onCommand(GenericMessageEvent event, String label, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        if (args[0].equalsIgnoreCase("help")) {
            event.respond(Colors.BOLD + "Channel Preferences Help");
            event.respond("preferences [channel] set [option] [value] (values...)");
            return;
        }
        if (args.length < 2) {
            event.respond("Not enough arguments.");
            return;
        }
        final String channel = args[0], subcommand = args[1];
        if (channel.charAt(0) != '#') {
            event.respond("Channel did not start with \"#\"");
            return;
        }
        String[] subcommandArgs = ArrayUtils.subarray(args, 2, args.length);
        if (subcommand.equalsIgnoreCase("set")) set(event, new ChannelPreferences(channel), subcommandArgs);
        else event.respond("Unknown subcommand!");
    }

    private void set(GenericMessageEvent event, ChannelPreferences cp, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        final String channel = cp.getChannel();
        final String option = args[0];
        if (option.equalsIgnoreCase("help")) {
            event.respond("set command <command> <status> (e.g. \"set command mcping disabled\")");
            event.respond("set listener <listener> <status> (e.g. \"set listener youtube disabled\"");
        } else if (option.equalsIgnoreCase("command")) {
            if (args.length < 3) {
                event.respond("Not enough arguments.");
                return;
            }
            if (!BotUtils.isAuthorized(event.getUser(), channel)) {
                event.respond("You need to be an op in that channel.");
                return;
            }
            String command = args[1], status = args[2];
            IRCCommand ic = rb.getCommandHandler().getCommand(command);
            if (ic == null) {
                event.respond("No such command.");
                return;
            }
            Status s;
            try {
                s = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                event.respond("Invalid status.");
                return;
            }
            List<String> disabledCommands = cp.getDisabledCommands();
            if (s == Status.DISABLED) {
                if (disabledCommands.contains(ic.getName())) {
                    event.respond(Colors.BOLD + ic.getName() + Colors.NORMAL + " is already disabled in " + Colors.BOLD + channel + Colors.NORMAL + ".");
                    return;
                }
                disabledCommands.add(ic.getName());
                event.respond("Disabled " + Colors.BOLD + ic.getName() + Colors.NORMAL + " in " + Colors.BOLD + channel + Colors.NORMAL + ".");
            } else if (s == Status.ENABLED) {
                if (!disabledCommands.contains(ic.getName())) {
                    event.respond(Colors.BOLD + ic.getName() + Colors.NORMAL + " is already enabled in " + Colors.BOLD + channel + Colors.NORMAL + ".");
                    return;
                }
                disabledCommands.remove(ic.getName());
                event.respond("Enabled " + Colors.BOLD + ic.getName() + Colors.NORMAL + " in " + Colors.BOLD + channel + Colors.NORMAL + ".");
            }
            cp.setDisabledCommands(disabledCommands);
        } else if (option.equalsIgnoreCase("listener")) {
            if (args.length < 3) {
                event.respond("Not enough arguments.");
                return;
            }
            if (!BotUtils.isAuthorized(event.getUser(), channel)) {
                event.respond("You need to be an op in that channel.");
                return;
            }
            String listener = args[1], status = args[2];
            IRCListener il = rb.getListenerHandler().getListener(listener);
            if (il == null) {
                event.respond("No such listener.");
                return;
            }
            Status s;
            try {
                s = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                event.respond("Invalid status.");
                return;
            }
            List<String> disabledListeners = cp.getDisabledListeners();
            if (s == Status.DISABLED) {
                if (disabledListeners.contains(il.getName())) {
                    event.respond(Colors.BOLD + il.getName() + Colors.NORMAL + " is already disabled in " + Colors.BOLD + channel + Colors.NORMAL + ".");
                    return;
                }
                disabledListeners.add(il.getName());
                event.respond("Disabled " + Colors.BOLD + il.getName() + Colors.NORMAL + " in " + Colors.BOLD + channel + Colors.NORMAL + ".");
            } else if (s == Status.ENABLED) {
                if (!disabledListeners.contains(il.getName())) {
                    event.respond(Colors.BOLD + il.getName() + Colors.NORMAL + " is already enabled in " + Colors.BOLD + channel + Colors.NORMAL + ".");
                    return;
                }
                disabledListeners.remove(il.getName());
                event.respond("Enabled " + Colors.BOLD + il.getName() + Colors.NORMAL + " in " + Colors.BOLD + channel + Colors.NORMAL + ".");
            }
            cp.setDisabledListeners(disabledListeners);
        }
    }

    private enum Status {
        ENABLED, DISABLED
    }

    @Override
    public String getName() {
        return "preferences";
    }

    @Override
    public String getUsage() {
        return "<command> [subcommands/help]";
    }

    @Override
    public String getDescription() {
        return "Manages preferences for channels";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pref", "pr", "p"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.PRIVATE;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.PUBLIC;
    }
}
