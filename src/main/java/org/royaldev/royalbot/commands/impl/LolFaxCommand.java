package org.royaldev.royalbot.commands.impl;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.commands.CallInfo;
import org.royaldev.royalbot.commands.NoticeableCommand;

import java.util.List;
import java.util.Random;

public class LolFaxCommand extends NoticeableCommand {

    private final RoyalBot rb = RoyalBot.getInstance();
    private final Random r = rb.getRandom();

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo callInfo, String[] args) {
        final List<String> baxfax = rb.getConfig().getLolFax();
        if (baxfax.size() < 1) {
            notice(event, "No lolfax registered!");
            return;
        }
        boolean noPing = args.length > 0 && args[0].equalsIgnoreCase("noping");
        String response = "[lolfax] " + baxfax.get(r.nextInt(baxfax.size()));
        if (noPing && event instanceof MessageEvent) {
            MessageEvent me = (MessageEvent) event;
            for (User u : me.getChannel().getUsers()) {
                int length = u.getNick().length();
                for (int index : BotUtils.indicesOf(response, u.getNick()))
                    response = response.substring(0, index) + BotUtils.flip(response.substring(index, index + length)) + response.substring(index + length, response.length());
            }
        }
        final boolean xafxab = callInfo.getLabel().equalsIgnoreCase("xaflol");
        if (xafxab) {
            int index;
            while ((index = StringUtils.indexOfIgnoreCase(response, "lolfax")) != -1)
                response = response.substring(0, index) + StringUtils.reverse(response.substring(index, index + 6)) + response.substring(index + 6, response.length());
        }
        event.respond(response);
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
        return "Gets a random lol768 fact!";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"xaflol"};
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
