package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.RoyalBot;

import java.util.List;
import java.util.Random;

public class BaxFaxCommand implements IRCCommand {

    private final RoyalBot rb = RoyalBot.getInstance();
    private final Random r = rb.getRandom();

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        final List<String> baxfax = rb.getConfig().getBaxFax();
        event.respond("[baxfax] " + baxfax.get(r.nextInt(baxfax.size())));
    }

    @Override
    public String getName() {
        return "baxfax";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public String getDescription() {
        return "Gets a random mbaxter fact!";
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
