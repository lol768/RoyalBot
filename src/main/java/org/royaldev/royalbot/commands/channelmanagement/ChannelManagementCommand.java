package org.royaldev.royalbot.commands.channelmanagement;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.CommandHandler;
import org.royaldev.royalbot.commands.IRCCommand;
import org.royaldev.royalbot.commands.channelmanagement.subcommands.CMCommandsSubcommand;
import org.royaldev.royalbot.commands.channelmanagement.subcommands.CMPreferencesSubcommand;

public class ChannelManagementCommand implements IRCCommand {

    private final CommandHandler subcommands = new CommandHandler();

    public ChannelManagementCommand() {
        subcommands.registerCommand(new CMCommandsSubcommand());
        subcommands.registerCommand(new CMPreferencesSubcommand());
    }

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        final String subcommandName = args[0];
        if (subcommandName.equalsIgnoreCase("help")) {
            for (IRCCommand subcommand : subcommands.getAllCommands())
                event.respond(BotUtils.getHelpString(subcommand));
            return;
        }
        final IRCCommand ic = subcommands.getCommand(subcommandName);
        if (ic == null) {
            event.respond("No such subcommand!");
            return;
        }
        try {
            ic.onCommand(event, ArrayUtils.subarray(args, 1, args.length));
        } catch (Throwable t) {
            final StringBuilder sb = new StringBuilder("Unhandled subcommand exception! ");
            sb.append(t.getClass().getSimpleName()).append(": ").append(t.getMessage());
            String url = BotUtils.linkToStackTrace(t);
            if (url != null) sb.append(" (").append(url).append(")");
            event.respond(sb.toString());
        }
    }

    @Override
    public String getName() {
        return "channelmanagement";
    }

    @Override
    public String getUsage() {
        return "<command> [subcommand] (args...)";
    }

    @Override
    public String getDescription() {
        return "Manages channels";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cm", "chanman"};
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
