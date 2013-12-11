package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;

public class JoinCommand implements IRCCommand {

    @Override
    public void onCommand(GenericMessageEvent event, String label, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        final String channel = args[0];
        if (!channel.startsWith("#")) {
            event.respond("Channel did not start with \"#\".");
            return;
        }
        event.getBot().sendIRC().joinChannel(channel);
        event.respond("Joined " + channel + ".");
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getUsage() {
        return "join [channel]";
    }

    @Override
    public String getDescription() {
        return "Makes the bot join a channel";
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
        return AuthLevel.ADMIN;
    }
}
