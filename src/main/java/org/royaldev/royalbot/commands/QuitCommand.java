package org.royaldev.royalbot.commands;

import org.apache.commons.lang3.StringUtils;
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

    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 1) event.getBot().sendIRC().quitServer();
        else event.getBot().sendIRC().quitServer(StringUtils.join(args, ' '));
    }

    public CommandType getCommandType() {
        return CommandType.PRIVATE;
    }

    public AuthLevel getAuthLevel() {
        return AuthLevel.ADMIN;
    }
}
