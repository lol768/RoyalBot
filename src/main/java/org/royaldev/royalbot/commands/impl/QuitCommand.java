package org.royaldev.royalbot.commands.impl;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.commands.CallInfo;
import org.royaldev.royalbot.commands.IRCCommand;

public class QuitCommand implements IRCCommand {

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo callInfo, String[] args) {
        if (args.length < 1) event.getBot().sendIRC().quitServer();
        else event.getBot().sendIRC().quitServer(StringUtils.join(args, ' '));
    }

    @Override
    public String getName() {
        return "quit";
    }

    @Override
    public String getUsage() {
        return "<command> (reason)";
    }

    @Override
    public String getDescription() {
        return "Makes the bot quit. :(";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
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
