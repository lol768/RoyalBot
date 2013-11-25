package org.royaldev.royalbot.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    // 2 = hour, 4 = minute, 6 = second
    private final Pattern time = Pattern.compile("PT((\\d+)H)?((\\d+)M)?((\\d+)S)?");
    private final ObjectMapper om = new ObjectMapper();
    private final DecimalFormat df = new DecimalFormat("00");
    private final RoyalBot rb = RoyalBot.getInstance();

    private int zeroOrNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

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
                return;
            }
            jn = jn.findValue("items");
            if (jn == null) return;
            jn = jn.get(0);
            if (jn == null) return;
            JsonNode snippet = jn.findPath("snippet");
            JsonNode statistics = jn.findPath("statistics");
            JsonNode contentDetails = jn.findPath("contentDetails");
            Matcher timeMatcher = time.matcher(contentDetails.findPath("duration").asText());
            timeMatcher.find();
            e.respond(String.format("%s by %s (%s) - %s views",
                    Colors.BOLD + snippet.findPath("title").asText() + Colors.NORMAL,
                    Colors.BOLD + snippet.findPath("channelTitle").asText() + Colors.NORMAL,
                    df.format(zeroOrNumber(timeMatcher.group(2))) + ":" + df.format(zeroOrNumber(timeMatcher.group(4))) + ":" + df.format(zeroOrNumber(timeMatcher.group(6))),
                    statistics.findPath("viewCount").asLong()
            ));
        }
    }

}
