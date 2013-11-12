package org.royaldev.royalbot.commands;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;

public class ShortenCommand implements IRCCommand {

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        final String url = StringUtils.join(args, ' ');
        try {
            event.respond(BotUtils.shortenURL(url));
        } catch (Exception e) {
            event.respond("Could not shorten that link.");
        }
    }

    @Override
    public String getName() {
        return "shorten";
    }

    @Override
    public String getUsage() {
        return "<command> [url]";
    }

    @Override
    public String getDescription() {
        return "Shortens a URL.";
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
