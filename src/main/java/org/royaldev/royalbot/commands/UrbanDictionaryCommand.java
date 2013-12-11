package org.royaldev.royalbot.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrbanDictionaryCommand implements IRCCommand {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void onCommand(GenericMessageEvent event, String label, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        final String url;
        try {
            url = String.format("http://api.urbandictionary.com/v0/define?term=%s", URLEncoder.encode(StringUtils.join(args, ' '), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            event.respond("Couldn't encode in UTF-8.");
            return;
        }
        JsonNode jn;
        try {
            jn = om.readTree(BotUtils.getContent(url));
        } catch (Exception ex) {
            String stackURL = BotUtils.linkToStackTrace(ex);
            event.respond(BotUtils.formatException(ex) + ((stackURL != null) ? " (" + stackURL + ")" : ""));
            return;
        }
        if (jn.path("result_type").asText().equalsIgnoreCase("no_results")) {
            event.respond("No results.");
            return;
        }
        jn = jn.path("list").path(0);
        String permalink;
        try {
            permalink = BotUtils.shortenURL(jn.path("permalink").asText());
        } catch (Exception e) {
            permalink = "no url";
        }
        String definition = jn.path("definition").asText().replaceAll("(\r)?\n", " ");
        if (definition.length() > 200) definition = definition.substring(0, 200) + " ...";
        event.respond(String.format("(%s) %s", permalink, definition));
    }

    @Override
    public String getName() {
        return "urbandictionary";
    }

    @Override
    public String getUsage() {
        return "<command> [query]";
    }

    @Override
    public String getDescription() {
        return "Queries UrbanDictionary for definitions";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ud", "urban", "urbandict"};
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
