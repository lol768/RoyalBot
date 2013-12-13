package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;

public abstract class NoticeableCommand implements IRCCommand {

    protected void notice(GenericMessageEvent event, String notice) {
        event.getUser().send().notice(notice);
    }

}
