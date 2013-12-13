package org.royaldev.royalbot.commands.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.commands.NoticeableCommand;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ChuckCommand extends NoticeableCommand {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void onCommand(GenericMessageEvent event, String label, String[] args) {
        final String url;
        try {
            url = "http://api.icndb.com/jokes/random" + ((args.length > 0) ? "?limitTo=[" + URLEncoder.encode(args[0], "UTF-8") + "]" : "");
        } catch (UnsupportedEncodingException ex) {
            notice(event, "Couldn't encode in UTF-8.");
            return;
        }
        JsonNode jn;
        try {
            jn = om.readTree(BotUtils.getContent(url));
        } catch (Exception ex) {
            notice(event, "Invalid category, probably.");
            return;
        }
        String joke = jn.path("value").path("joke").asText();
        if (joke.isEmpty()) {
            notice(event, "Couldn't find a joke!");
            return;
        }
        event.respond(StringEscapeUtils.unescapeHtml4(joke));
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
