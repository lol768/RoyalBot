package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.RoyalBot;

import java.util.Random;

public class ChooseCommand implements IRCCommand {

    private final RoyalBot rb = RoyalBot.getInstance();
    private final Random r = rb.getRandom();

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        if (args.length == 1) {
            event.respond("It seems that the choice is already made.");
            return;
        }
        event.respond(args[r.nextInt(args.length)]);
    }

    @Override
    public String getName() {
        return "choose";
    }

    @Override
    public String getUsage() {
        return "<command> [choice] ...";
    }

    @Override
    public String getDescription() {
        return "Chooses one of the given choices randomly";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pick"};
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
