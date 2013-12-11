package org.royaldev.royalbot.commands;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class MessageCommand implements IRCCommand {

    @Override
    public void onCommand(GenericMessageEvent event, String label, String[] args) {
        if (args.length < 2) {
            event.respond("Not enough arguments.");
            return;
        }
        String target = args[0];
        String message = StringUtils.join(args, ' ', 1, args.length);
        event.getBot().sendIRC().message(target, message);
        event.respond("Sent message to " + target + ".");
    }

    @Override
    public String getName() {
        return "message";
    }

    @Override
    public String getUsage() {
        return "<command> [target] [message]";
    }

    @Override
    public String getDescription() {
        return "Sends a message to the target";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"msg"};
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
