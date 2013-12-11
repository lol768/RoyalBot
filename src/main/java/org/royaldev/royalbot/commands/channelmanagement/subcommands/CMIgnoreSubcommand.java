package org.royaldev.royalbot.commands.channelmanagement.subcommands;

import org.pircbotx.Colors;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.commands.IRCCommand;
import org.royaldev.royalbot.configuration.ChannelPreferences;

import java.util.List;

public class CMIgnoreSubcommand implements IRCCommand {

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 2) {
            event.respond("Not enough arguments.");
            return;
        }
        String channel = args[0];
        if (channel.charAt(0) != '#') {
            event.respond("The channel must start with \"#\"");
            return;
        }
        if (!BotUtils.isAuthorized(event.getUser(), channel)) {
            event.respond("You need to be an op in that channel.");
            return;
        }
        String hostmask = args[1];
        final ChannelPreferences cp = new ChannelPreferences(channel);
        final List<String> ignores = cp.getIgnores();
        if (hostmask.equalsIgnoreCase("list")) {
            event.respond(Colors.BOLD + "Ignored hostmasks:");
            for (String ignore : ignores) event.respond("  " + ignore);
            return;
        }
        if (ignores.contains(hostmask)) {
            ignores.remove(hostmask);
            event.respond("Unignored " + hostmask + ".");
        } else {
            ignores.add(hostmask);
            event.respond("Ignored " + hostmask + ".");
        }
        cp.setIgnores(ignores);
    }

    @Override
    public String getName() {
        return "ignore";
    }

    @Override
    public String getUsage() {
        return "<command> [hostmask]";
    }

    @Override
    public String getDescription() {
        return "Toggles channel ignores";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
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
