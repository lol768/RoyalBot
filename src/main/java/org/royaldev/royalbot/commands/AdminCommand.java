package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.RoyalBot;

import java.util.Set;

public class AdminCommand implements IRCCommand {

    private final RoyalBot rb = RoyalBot.getInstance();

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public String getUsage() {
        return "admin [add/remove] (name)";
    }

    @Override
    public String getDescription() {
        return "Manipulates the admin list.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"admins"};
    }

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 2) {
            event.respond("Not enough arguments.");
            return;
        }
        final String subcommand = args[0];
        final String user = args[1];
        if (rb.getConfig().getSuperAdmin().equalsIgnoreCase(user)) {
            event.respond("Cannot manipulate the superadmin!");
            return;
        }
        if (subcommand.equalsIgnoreCase("add")) {
            final Set<String> admins = rb.getConfig().getAdmins();
            admins.add(user);
            rb.getConfig().setAdmins(admins);
            event.respond("Added " + user + " as admin.");
        } else if (subcommand.equalsIgnoreCase("remove")) {
            if (!rb.getConfig().getAdmins().contains(user)) {
                event.respond("No such admin.");
                return;
            }
            final Set<String> admins = rb.getConfig().getAdmins();
            admins.remove(user);
            rb.getConfig().setAdmins(admins);
            event.respond("Removed " + user + " from admins.");
        } else {
            event.respond("Invalid subcommand.");
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.PRIVATE;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.SUPERADMIN;
    }
}
