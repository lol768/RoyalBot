package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;

public class NumberFactCommand implements IRCCommand {

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments");
            return;
        }
        String num = args[0], type = (args.length > 1) ? args[1].toLowerCase() : "", content;
        try {
            content = BotUtils.getContent("http://numbersapi.com/" + num + "/" + type);
        } catch (Exception ex) {
            event.respond("Couldn't get a fact!");
            return;
        }
        if (content.trim().equalsIgnoreCase("Cannot GET /" + num + "/" + type)) {
            event.respond("Invalid type.");
            return;
        }
        if (content.trim().equalsIgnoreCase("Invalid url")) {
            event.respond("Invalid options.");
            return;
        }
        event.respond(content);
    }

    @Override
    public String getName() {
        return "numberfact";
    }

    @Override
    public String getUsage() {
        return "<command> [number string] (type)";
    }

    @Override
    public String getDescription() {
        return "Gets facts about numbers";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"nf", "numfact", "num"};
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
