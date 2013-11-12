package org.royaldev.royalbot.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.RoyalBot;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeListener extends ListenerAdapter<PircBotX> {

    private final Pattern p = Pattern.compile("https?://(www\\.)?youtube\\.com/watch\\?v=([\\w\\-]+)");
    private final ObjectMapper om = new ObjectMapper();
    private final DecimalFormat df = new DecimalFormat("00");
    private final RoyalBot rb = RoyalBot.getInstance();

    @Override
    public void onMessage(MessageEvent e) {
        if (!rb.getConfig().getYouTubeEnabled()) return;
        final Matcher m = p.matcher(e.getMessage());
        while (m.find()) {
            if (m.group(2) == null) continue;
            JsonNode jn;
            try {
                String url = "https://www.googleapis.com/youtube/v3/videos?id=%s&key=%s&part=snippet,statistics,contentDetails";
                jn = om.readTree(BotUtils.getContent(String.format(url, m.group(2), rb.getConfig().getYouTubeAPIKey())));
            } catch (Exception ex) {
                final String link = BotUtils.linkToStackTrace(ex);
                e.respond("Exception!" + ((link == null) ? "" : " " + link));
                return;
            }
            jn = jn.findValue("items");
            if (jn == null) return;
            jn = jn.get(0);
            if (jn == null) return;
            JsonNode snippet = jn.findPath("snippet");
            JsonNode statistics = jn.findPath("statistics");
            JsonNode contentDetails = jn.findPath("contentDetails");
            Period p = ISOPeriodFormat.standard().parsePeriod(contentDetails.findPath("duration").asText());
            e.respond(String.format("%s by %s (%s) - %s views",
                    Colors.BOLD + snippet.findPath("title").asText() + Colors.NORMAL,
                    Colors.BOLD + snippet.findPath("channelTitle").asText() + Colors.NORMAL,
                    df.format(p.getHours()) + ":" + df.format(p.getMinutes()) + ":" + df.format(p.getSeconds()),
                    statistics.findPath("viewCount").asLong()
            ));
        }
    }

}
