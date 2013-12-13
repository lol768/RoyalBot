package org.royaldev.royalbot.commands.impl;

import org.pircbotx.Colors;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.commands.NoticeableCommand;

import java.net.URLEncoder;

public class MCAccountCommand extends NoticeableCommand {

    @Override
    public void onCommand(GenericMessageEvent event, String label, String[] args) {
        if (args.length < 1) {
            notice(event, "Not enough arguments.");
            return;
        }
        boolean status;
        try {
            String content = BotUtils.getContent(String.format("https://minecraft.net/haspaid.jsp?user=%s", URLEncoder.encode(args[0], "UTF-8")));
            status = content.equalsIgnoreCase("true");
        } catch (Exception e) {
            notice(event, BotUtils.formatException(e));
            return;
        }
        event.respond(args[0] + " has " + Colors.BOLD + ((status) ? "" : "not ") + "paid" + Colors.NORMAL + " for Minecraft.");
    }

    @Override
    public String getName() {
        return "mcaccount";
    }

    @Override
    public String getUsage() {
        return "<command> [name]";
    }

    @Override
    public String getDescription() {
        return "Checks to see if that name has purchased Minecraft";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ispremium", "haspaid"};
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
