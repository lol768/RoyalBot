package org.royaldev.royalbot.auth;

import org.pircbotx.User;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.NoticeEvent;
import org.royaldev.royalbot.RoyalBot;

public class Auth {

    public static AuthResponse checkAuth(User user) {
        final RoyalBot rb = RoyalBot.getInstance();
        rb.getBot().sendIRC().message("NickServ", String.format("ACC %s *", user.getNick()));
        WaitForQueue queue = new WaitForQueue(rb.getBot());
        try {
            NoticeEvent<?> event = queue.waitFor(NoticeEvent.class);
            return new AuthResponse(rb, event, user);
        } catch (Exception ignored) {
        } finally {
            queue.close();
        }
        return new AuthResponse();
    }
}
