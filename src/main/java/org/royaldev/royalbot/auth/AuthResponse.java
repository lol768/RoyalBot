package org.royaldev.royalbot.auth;

import org.pircbotx.User;
import org.pircbotx.hooks.events.NoticeEvent;
import org.royaldev.royalbot.RoyalBot;

public class AuthResponse {

    private boolean isValid = false;
    private boolean isLoggedIn = false;
    private boolean isAdmin = false;
    private String accountName = "";

    protected AuthResponse(RoyalBot rb, NoticeEvent<?> event, User user) {
        if (!event.getUser().getNick().equals("NickServ")) return;
        String[] args = event.getNotice().split(" ");
        if (args.length < 5 || !args[0].equals(user.getNick())) return;
        this.isValid = true;
        this.accountName = args[2];
        if (Integer.parseInt(args[4]) == 3) this.isLoggedIn = true;
        if (this.isLoggedIn && rb.getConfig().getAdmins().contains(accountName)) this.isAdmin = true;
    }

    protected AuthResponse() {
    }

    /**
     * Gets if the account checked is a valid NickServ account.
     *
     * @return true or false
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Gets if the account is currently logged in.
     *
     * @return true or false
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    /**
     * Gets if the account is an admin (must be logged in and in the admins list).
     *
     * @return true or false
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Gets if the account is authed (must be a valid account, logged in, and in the admins list).
     *
     * @return true or false
     */
    public boolean isAuthed() {
        return isValid() && isLoggedIn() && isAdmin();
    }

    /**
     * Gets the name of the account handled by this AuthResponse.
     *
     * @return Account name - never null
     */
    public String getAccountName() {
        return accountName;
    }
}
