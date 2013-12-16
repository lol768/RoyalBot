package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;

public abstract class NoticeableCommand implements IRCCommand {

    /**
     * Sends a notice to the User linked to the given event.
     *
     * @param event  Event to get User from
     * @param notice Notice to send to User
     */
    protected void notice(GenericMessageEvent event, String notice) {
        event.getUser().send().notice(notice);
    }

}
