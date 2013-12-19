package org.royaldev.royalbot.auth;

import org.pircbotx.User;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.NoticeEvent;
import org.royaldev.royalbot.RoyalBot;

/**
 * Class for getting the auth status of users.
 */
public class Auth {

    /**
     * Gets the authentication status of a User.
     *
     * @param user User to check
     * @return AuthResponse - never null
     */
    public static AuthResponse checkAuth(User user) {
        final RoyalBot rb = RoyalBot.getInstance();
        rb.getBot().sendIRC().message("NickServ", String.format("ACC %s *", user.getNick()));
        try (WaitForQueue queue = new WaitForQueue(rb.getBot())) {
            NoticeEvent<?> event = queue.waitFor(NoticeEvent.class);
            return new AuthResponse(rb, event, user);
        } catch (Exception ignored) {
        }
        return new AuthResponse();
    }
}
