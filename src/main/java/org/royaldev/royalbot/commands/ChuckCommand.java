package org.royaldev.royalbot.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;

public class ChuckCommand implements IRCCommand {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        final String url = "http://api.icndb.com/jokes/random" + ((args.length > 0) ? "?limitTo=[" + args[0] + "]" : "");
        JsonNode jn;
        try {
            jn = om.readTree(BotUtils.getContent(url));
        } catch (Exception ex) {
            final String link = BotUtils.linkToStackTrace(ex);
            event.respond("Exception!" + ((link == null) ? "" : " " + link));
            return;
        }
        String joke = jn.path("value").path("joke").asText();
        if (joke.isEmpty()) {
            event.respond("Couldn't find a joke!");
            return;
        }
        event.respond(joke);
    }

    @Override
    public String getName() {
        return "chuck";
    }

    @Override
    public String getUsage() {
        return "<command> (category)";
    }

    @Override
    public String getDescription() {
        return "Gets a Chuck Norris joke!";
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
