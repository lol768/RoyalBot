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

    public boolean isValid() {
        return isValid;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isAuthed() {
        return isValid() && isLoggedIn() && isAdmin();
    }

    public String getAccountName() {
        return accountName;
    }
}
