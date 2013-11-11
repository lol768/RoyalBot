package org.royaldev.royalbot.commands;


import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.CommandHandler;
import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.auth.Auth;

public class HelpCommand implements IRCCommand {

    final RoyalBot rb = RoyalBot.getInstance();

    public String getName() {
        return "help";
    }

    public String getUsage() {
        return "<command> (command)";
    }

    public String getDescription() {
        return "Displays all commands!";
    }

    private String getHelpString(IRCCommand ic) {
        return ic.getName() + " / Description: " + ic.getDescription() + " / Usage: " + ic.getUsage().replaceAll("(?i)<command>", ic.getName()) + " / Type: " + ic.getCommandType().getDescription();
    }

    public void onCommand(GenericMessageEvent event, String[] args) {
        final CommandHandler ch = rb.getCommandHandler();
        final User u = event.getUser();
        final boolean userIsAdmin = Auth.checkAuth(u).isAuthed();
        final boolean isSuperAdmin = userIsAdmin && rb.getConfig().getSuperAdmin().equalsIgnoreCase(u.getNick());
        u.send().message("Channel command prefix: \"" + rb.getCommandPrefix() + "\"");
        if (args.length < 1) {
            for (IRCCommand ic : ch.getAllCommands()) {
                if (ic.getAuthLevel() == AuthLevel.ADMIN && !userIsAdmin) continue;
                if (ic.getAuthLevel() == AuthLevel.SUPERADMIN && !isSuperAdmin) continue;
                u.send().message(getHelpString(ic));
            }
        } else {
            final IRCCommand ic = ch.getCommand(args[0]);
            if (ic == null) {
                event.respond("No such command!");
                return;
            }
            u.send().message(getHelpString(ic));
        }
    }

    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    public AuthLevel getAuthLevel() {
        return AuthLevel.PUBLIC;
    }
}
