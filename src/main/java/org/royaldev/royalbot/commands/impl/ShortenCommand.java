package org.royaldev.royalbot.commands.impl;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.commands.NoticeableCommand;

public class ShortenCommand extends NoticeableCommand {

    @Override
    public void onCommand(GenericMessageEvent event, String label, String[] args) {
        if (args.length < 1) {
            notice(event, "Not enough arguments.");
            return;
        }
        final String url = StringUtils.join(args, ' ');
        try {
            event.respond(BotUtils.shortenURL(url));
        } catch (Exception e) {
            notice(event, "Could not shorten that link.");
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
