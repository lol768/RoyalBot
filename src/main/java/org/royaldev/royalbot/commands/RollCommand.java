package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.RoyalBot;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollCommand implements IRCCommand {

    private final RoyalBot rb = RoyalBot.getInstance();
    private final Random r = rb.getRandom();
    private final Pattern p = Pattern.compile("(\\d+)?d(\\d+)", Pattern.CASE_INSENSITIVE);

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        Matcher m = p.matcher(args[0]);
        if (!m.find()) {
            event.respond("Please specify correct type of die.");
            return;
        }
        final StringBuilder rolls = new StringBuilder();
        int numRolls, dieValue;
        try {
            numRolls = Integer.parseInt(m.group(1) == null ? "1" : m.group(1));
            dieValue = Integer.parseInt(m.group(2) == null ? "6" : m.group(2));
        } catch (NumberFormatException ex) {
            event.respond("Rolls or die value was not a number.");
            return;
        }
        if (numRolls < 1 || dieValue < 1) {
            event.respond("Can't roll zero times or roll a die that has no faces.");
            return;
        }
        if (numRolls > 25) {
            event.respond(numRolls + " rolls is a little excessive. Max number of rolls is 25.");
            return;
        }
        for (int i = 0; i < numRolls; i++) rolls.append(r.nextInt(dieValue) + 1).append(" ");
        event.respond("Rolling " + numRolls + "d" + dieValue + ": " + rolls.substring(0, rolls.length() - 1));
    }

    @Override
    public String getName() {
        return "roll";
    }

    @Override
    public String getUsage() {
        return "<command> [die]";
    }

    @Override
    public String getDescription() {
        return "Rolls a die";
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
