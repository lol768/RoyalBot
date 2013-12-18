package org.royaldev.royalbot.commands.impl.channelmanagement;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.commands.CallInfo;
import org.royaldev.royalbot.commands.IRCCommand;
import org.royaldev.royalbot.commands.impl.channelmanagement.subcommands.CMCommandsSubcommand;
import org.royaldev.royalbot.commands.impl.channelmanagement.subcommands.CMIgnoreSubcommand;
import org.royaldev.royalbot.commands.impl.channelmanagement.subcommands.CMPreferencesSubcommand;
import org.royaldev.royalbot.handlers.CommandHandler;

public class ChannelManagementCommand implements IRCCommand {

    private final CommandHandler subcommands = new CommandHandler();

    public ChannelManagementCommand() {
        subcommands.register(new CMCommandsSubcommand());
        subcommands.register(new CMIgnoreSubcommand());
        subcommands.register(new CMPreferencesSubcommand());
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo callInfo, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        final String subcommandName = args[0];
        if (subcommandName.equalsIgnoreCase("help")) {
            for (IRCCommand subcommand : subcommands.getAll())
                event.respond(BotUtils.getHelpString(subcommand));
            return;
        }
        final IRCCommand ic = subcommands.get(subcommandName);
        if (ic == null) {
            event.respond("No such subcommand!");
            return;
        }
        try {
            ic.onCommand(event, new CallInfo(subcommandName, CallInfo.UsageType.PRIVATE), ArrayUtils.subarray(args, 1, args.length));
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
