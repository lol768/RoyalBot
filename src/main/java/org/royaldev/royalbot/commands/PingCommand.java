package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;

public class PingCommand implements IRCCommand {

    public String getName() {
        return "ping";
    }

    public String getUsage() {
        return "<command> (\"me\")";
    }

    public String getDescription() {
        return "Pings the bot! :D";
    }

    public void onCommand(GenericMessageEvent event, String message) {
        if (message.equalsIgnoreCase("me")) event.respond("Hello there, " + event.getUser().getNick() + "!");
        else event.respond("Pong!");
    }

    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    public AuthLevel getAuthLevel() {
        return AuthLevel.PUBLIC;
    }
}
