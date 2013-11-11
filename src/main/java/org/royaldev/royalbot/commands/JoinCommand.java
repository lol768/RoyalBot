package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;

public class JoinCommand implements IRCCommand {
    public String getName() {
        return "join";
    }

    public String getUsage() {
        return "join [channel]";
    }

    public String getDescription() {
        return "Makes the bot join a channel";
    }

    public void onCommand(GenericMessageEvent event, String[] args) {
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

    public CommandType getCommandType() {
        return CommandType.PRIVATE;
    }

    public AuthLevel getAuthLevel() {
        return AuthLevel.ADMIN;
    }
}
