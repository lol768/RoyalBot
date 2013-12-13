package org.royaldev.royalbot.commands.impl;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.commands.CallInfo;
import org.royaldev.royalbot.commands.NoticeableCommand;

import java.net.URLEncoder;

public class NumberFactCommand extends NoticeableCommand {

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo callInfo, String[] args) {
        if (args.length < 1) {
            notice(event, "Not enough arguments");
            return;
        }
        String num = args[0], type = (args.length > 1) ? args[1].toLowerCase() : "", content;
        try {
            content = BotUtils.getContent("http://numbersapi.com/" + URLEncoder.encode(num, "UTF-8") + "/" + URLEncoder.encode(type, "UTF-8"));
        } catch (Exception ex) {
            notice(event, "Couldn't get a fact!");
            return;
        }
        if (content.trim().equalsIgnoreCase("Cannot GET /" + num + "/" + type)) {
            notice(event, "Invalid type.");
            return;
        }
        if (content.trim().equalsIgnoreCase("Invalid url")) {
            notice(event, "Invalid options.");
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
