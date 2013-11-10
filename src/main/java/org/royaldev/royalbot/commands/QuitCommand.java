package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;

public class QuitCommand implements IRCCommand {
    public String getName() {
        return "quit";
    }

    public String getUsage() {
        return "<command> (reason)";
    }

    public String getDescription() {
        return "Makes the bot quit. :(";
    }

    public void onCommand(GenericMessageEvent event, String message) {
        if (message.isEmpty()) event.getBot().sendIRC().quitServer();
        else event.getBot().sendIRC().quitServer(message);
    }

    public CommandType getCommandType() {
        return CommandType.PRIVATE;
    }

    public AuthLevel getAuthLevel() {
        return AuthLevel.ADMIN;
    }
}
