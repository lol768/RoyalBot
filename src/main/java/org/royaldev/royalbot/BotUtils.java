package org.royaldev.royalbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.royaldev.royalbot.commands.IRCCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

public class BotUtils {

    /**
     * Gets the appropriate string to send to a user if an exception is encountered.
     *
     * @param e Exception to format
     * @return Message to send user; never null
     */
    public static String formatException(Exception e) {
        return "Exception! " + e.getClass().getSimpleName() + ": " + e.getMessage();
    }

    public static String getStackTrace(Exception e) {
        final StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static String pastebin(String paste) {
        final CloseableHttpClient hc = HttpClients.createDefault();
        final HttpPost hp = new HttpPost("http://hastebin.com/documents");
        try {
            hp.setEntity(new StringEntity(paste, "UTF-8"));
        } catch (UnsupportedEncodingException ignored) {
            return null;
        }
        HttpResponse hr;
        try {
            hr = hc.execute(hp);
        } catch (IOException ex) {
            return null;
        }
        HttpEntity he = hr.getEntity();
        if (he == null) return null;
        String json;
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(he.getContent()));
            try {
                json = br.readLine();
            } finally {
                br.close();
                hc.close();
            }
        } catch (IOException ex) {
            return null;
        }
        final ObjectMapper om = new ObjectMapper();
        JsonNode jn;
        try {
            jn = om.readTree(json);
        } catch (Exception e) {
            return null;
        }
        json = jn.path("key").asText();
        return json.isEmpty() ? null : "http://hastebin.com/" + json;
    }

    /**
     * Convenience method to get a stack trace from an Exception, send it to Pastebin, and then shorten the link with
     * is.gd. If Pastebin is disabled in the config, this will return null.
     * <br/>
     * <strong>Note:</strong> If <em>any</em> errors occur, this will simply return null, and you will get no feedback
     * of the error.
     *
     * @param ex Exception to do this with
     * @return Shortened link to the stack trace or null
     */
    public static String linkToStackTrace(Exception ex) {
        String pastebin = BotUtils.pastebin(BotUtils.getStackTrace(ex));
        if (pastebin != null) {
            pastebin += ".txt";
            String url = null;
            try {
                url = BotUtils.shortenURL(pastebin);
            } catch (Exception ignored) {
            }
            if (url != null) return url;
        }
        return null;
    }

    /**
     * Gets the contents of an external URL.
     *
     * @param url URL to get contents of
     * @return Contents
     * @throws IOException
     */
    public static String getContent(String url) throws IOException, URISyntaxException {
        final URL u = new URL(url);
        final BufferedReader br = new BufferedReader(new InputStreamReader(u.openConnection().getInputStream()));
        final StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append("\n");
        return sb.toString();
    }

    public static String shortenURL(String url) throws IOException, URISyntaxException {
        final URL shorten = new URL("http://is.gd/create.php?format=simple&url=" + URLEncoder.encode(url, "UTF-8"));
        return getContent(shorten.toString()).split("\n")[0];
    }

    public static String getHelpString(IRCCommand ic) {
        return ic.getName() + " / Description: " + ic.getDescription() + " / Usage: " + ic.getUsage().replaceAll("(?i)<command>", ic.getName()) + " / Type: " + ic.getCommandType().getDescription();
    }

}
