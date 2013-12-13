package org.royaldev.royalbot.commands.impl;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.commands.IRCCommand;

public class PingCommand implements IRCCommand {

    @Override
    public void onCommand(GenericMessageEvent event, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("me"))
            event.respond("Hello there, " + event.getUser().getNick() + "!");
        else event.respond("Pong!");
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getUsage() {
        return "<command> (\"me\")";
    }

    @Override
    public String getDescription() {
        return "Pings the bot! :D";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.PUBLIC;
    }
}
