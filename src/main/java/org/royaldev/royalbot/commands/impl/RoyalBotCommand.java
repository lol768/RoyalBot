package org.royaldev.royalbot.commands.impl;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.commands.IRCCommand;

public class RoyalBotCommand implements IRCCommand {

    private final RoyalBot rb = RoyalBot.getInstance();

    @Override
    public void onCommand(GenericMessageEvent event, String label, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        final String subcommand = args[0];
        if (subcommand.equalsIgnoreCase("reload")) {
            rb.getConfig().load();
            event.respond("Reloaded config.");
        } else if (subcommand.equalsIgnoreCase("save")) {
            rb.getConfig().save();
            event.respond("Saved config.");
        } else {
            event.respond("Unknown subcommand.");
        }
    }

    @Override
    public String getName() {
        return "royalbot";
    }

    @Override
    public String getUsage() {
        return "<command> [subcommand]";
    }

    @Override
    public String getDescription() {
        return "Manages the bot";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rb"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.PRIVATE;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.ADMIN;
    }
}
