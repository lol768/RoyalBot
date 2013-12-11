package org.royaldev.royalbot.commands;

import org.pircbotx.Colors;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.RoyalBot;

import java.util.List;

public class IgnoreCommand implements IRCCommand {

    private final RoyalBot rb = RoyalBot.getInstance();

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        String hostmask = args[0];
        final List<String> ignores = rb.getConfig().getIgnores();
        if (hostmask.equalsIgnoreCase("list")) {
            event.respond(Colors.BOLD + "Ignored hostmasks:");
            for (String ignore : ignores) event.respond("  " + ignore);
            return;
        }
        if (ignores.contains(hostmask)) {
            ignores.remove(hostmask);
            event.respond("Unignored " + hostmask + ".");
        } else {
            ignores.add(hostmask);
            event.respond("Ignored " + hostmask + ".");
        }
        rb.getConfig().setIgnores(ignores);
    }

    @Override
    public String getName() {
        return "ignore";
    }

    @Override
    public String getUsage() {
        return "<command> [hostmask]";
    }

    @Override
    public String getDescription() {
        return "Toggles bot ignores (regex)";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"unignore"};
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
