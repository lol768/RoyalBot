package org.royaldev.royalbot.commands.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.commands.CallInfo;
import org.royaldev.royalbot.commands.NoticeableCommand;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GoogleCommand extends NoticeableCommand {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo callInfo, String[] args) {
        if (args.length < 1) {
            notice(event, "Not enough arguments.");
            return;
        }
        final String url;
        try {
            url = String.format("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&safe=moderate&q=%s", URLEncoder.encode(StringUtils.join(args, ' '), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            notice(event, "Couldn't encode in UTF-8.");
            return;
        }
        JsonNode jn;
        try {
            jn = om.readTree(BotUtils.getContent(url));
        } catch (Exception ex) {
            notice(event, "Couldn't parse results.");
            return;
        }
        int responseStatus = jn.path("responseStatus").asInt(-1);
        if (responseStatus < 200 || responseStatus >= 300) {
            notice(event, "Error retrieving results: " + responseStatus + ".");
            return;
        }
        if (jn.path("responseData").path("results").size() < 1) {
            notice(event, "No results.");
            return;
        }
        jn = jn.path("responseData").path("results").path(0);
        String title = BotUtils.truncate(jn.path("titleNoFormatting").asText(), 60);
        String resultUrl;
        try {
            resultUrl = BotUtils.shortenURL(jn.path("url").asText());
        } catch (IOException e) {
            notice(event, "Couldn't shorten URL.");
            return;
        }
        String content = StringEscapeUtils.unescapeHtml4(jn.path("content").asText().replaceAll("<.*?>", "")).replace("\n", " ");
        int oldLength = content.length();
        content = BotUtils.truncate(content, 150);
        if (content.length() != oldLength) content += " ...";
        event.respond(String.format("(%s) %s - %s", resultUrl, title, content));
    }

    @Override
    public String getName() {
        return "google";
    }

    @Override
    public String getUsage() {
        return "<command> [query]";
    }

    @Override
    public String getDescription() {
        return "Queries Google for search results";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"goog", "g"};
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
