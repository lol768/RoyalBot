package org.royaldev.royalbot.commands.impl;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.commands.CallInfo;
import org.royaldev.royalbot.commands.IRCCommand;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class BaxFaxCommand implements IRCCommand {

    private final RoyalBot rb = RoyalBot.getInstance();
    private final Random r = rb.getRandom();

    private final Pattern baxfaxPattern = Pattern.compile("(m?bax(ter|fax)?)", Pattern.CASE_INSENSITIVE);

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo callInfo, String[] args) {
        final List<String> baxfax = rb.getConfig().getBaxFax();
        boolean noPing = args.length > 0 && args[0].equalsIgnoreCase("noping");
        String response = baxfax.get(r.nextInt(baxfax.size()));
        if (noPing && event instanceof MessageEvent) {
            MessageEvent me = (MessageEvent) event;
            for (User u : me.getChannel().getUsers()) {
                int length = u.getNick().length();
                int index;
                while ((index = response.toLowerCase().indexOf(u.getNick().toLowerCase())) != -1)
                    response = response.substring(0, index) + StringUtils.reverse(response.substring(index, index + length)) + response.substring(index + length, response.length());
            }
        }
        event.respond(((callInfo.getLabel().equalsIgnoreCase("xafxab")) ? "[xafxab] " : "[baxfax] ") + response);
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
        return new String[]{"xafxab"};
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
