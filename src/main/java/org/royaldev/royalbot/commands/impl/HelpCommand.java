package org.royaldev.royalbot.commands.impl;


import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.auth.Auth;
import org.royaldev.royalbot.commands.CallInfo;
import org.royaldev.royalbot.commands.ChannelCommand;
import org.royaldev.royalbot.commands.IRCCommand;
import org.royaldev.royalbot.handlers.CommandHandler;

public class HelpCommand implements IRCCommand {

    private final RoyalBot rb = RoyalBot.getInstance();

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo callInfo, String[] args) {
        final CommandHandler ch = rb.getCommandHandler();
        final User u = event.getUser();
        final boolean userIsAdmin = Auth.checkAuth(u).isAuthed();
        final boolean isSuperAdmin = userIsAdmin && rb.getConfig().getSuperAdmin().equalsIgnoreCase(u.getNick());
        u.send().message("Channel command prefix: \"" + rb.getCommandPrefix() + "\"");
        if (args.length < 1) {
            for (IRCCommand ic : ch.getAll()) {
                if (ic instanceof ChannelCommand) continue;
                if (ic.getAuthLevel() == AuthLevel.ADMIN && !userIsAdmin) continue;
                if (ic.getAuthLevel() == AuthLevel.SUPERADMIN && !isSuperAdmin) continue;
                u.send().message(BotUtils.getHelpString(ic));
            }
        } else {
            final IRCCommand ic = ch.get(args[0]);
            if (ic == null) {
                event.respond("No such command!");
                return;
            }
            u.send().message(BotUtils.getHelpString(ic));
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "<command> (command)";
    }

    @Override
    public String getDescription() {
        return "Displays all commands!";
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
