package org.royaldev.royalbot.commands.impl;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.commands.IRCCommand;

public class RepositoryCommand implements IRCCommand {

    @Override
    public void onCommand(GenericMessageEvent event, String label, String[] args) {
        event.respond("Contribute to " + event.getBot().getNick() + "! https://github.com/RoyalDev/RoyalBot");
    }

    @Override
    public String getName() {
        return "repository";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public String getDescription() {
        return "Returns the repository for this bot.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"repo"};
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
