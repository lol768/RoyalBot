package org.royaldev.royalbot;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.auth.Auth;
import org.royaldev.royalbot.commands.IRCCommand;

public class BaseListeners extends ListenerAdapter<PircBotX> {

    private final RoyalBot rb;

    protected BaseListeners(RoyalBot instance) {
        rb = instance;
    }

    public void onConnect(ConnectEvent e) {
        rb.getLogger().info("Connected!");
    }

    public void onInvite(InviteEvent e) {
        e.getBot().sendIRC().joinChannel(e.getChannel());
    }

    public void onGenericMessage(GenericMessageEvent e) {
        if (!(e instanceof MessageEvent) && !(e instanceof PrivateMessageEvent)) return;
        final boolean isPrivateMessage = e instanceof PrivateMessageEvent;
        if (e.getMessage().isEmpty()) return;
        if (e.getMessage().charAt(0) != rb.getCommandPrefix() && !isPrivateMessage) return;
        final String[] split = e.getMessage().split(" ");
        final String commandString = (!isPrivateMessage) ? split[0].substring(1, split[0].length()) : split[0];
        final IRCCommand command = rb.getCommandHandler().getCommand(commandString);
        if (command == null) return;
        final IRCCommand.CommandType commandType = command.getCommandType();
        if (!isPrivateMessage && commandType != IRCCommand.CommandType.MESSAGE && commandType != IRCCommand.CommandType.BOTH)
            return;
        else if (isPrivateMessage && commandType != IRCCommand.CommandType.PRIVATE && commandType != IRCCommand.CommandType.BOTH)
            return;
        final IRCCommand.AuthLevel authLevel = command.getAuthLevel();
        if (authLevel == IRCCommand.AuthLevel.ADMIN && !Auth.checkAuth(e.getUser()).isAuthed()) {
            e.respond("You are not an admin!");
            return;
        }
        command.onCommand(e, ((split.length > 1) ? StringUtils.join(split, ' ', 1, split.length).trim() : ""));
    }

}
