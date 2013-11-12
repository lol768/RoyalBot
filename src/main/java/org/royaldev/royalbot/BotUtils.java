package org.royaldev.royalbot;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
        final StringBuilder sb = new StringBuilder(e.getClass().getName());
        sb.append(": ").append(e.getMessage());
        for (StackTraceElement ste : e.getStackTrace()) sb.append("\n").append("  at ").append(ste.toString());
        return sb.toString();
    }

    public static String pastebin(String paste) {
        final CloseableHttpClient hc = HttpClients.createDefault();
        final HttpPost hp = new HttpPost("http://pastebin.com/api/api_post.php");
        final List<BasicNameValuePair> posts = new ArrayList<BasicNameValuePair>();
        posts.add(new BasicNameValuePair("api_dev_key", RoyalBot.getInstance().getConfig().getPastebinAPIKey()));
        posts.add(new BasicNameValuePair("api_paste_format", "text"));
        posts.add(new BasicNameValuePair("api_option", "paste"));
        posts.add(new BasicNameValuePair("api_paste_private", "1"));
        posts.add(new BasicNameValuePair("api_paste_code", paste));
        try {
            hp.setEntity(new UrlEncodedFormEntity(posts, "UTF-8"));
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
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(he.getContent()));
            try {
                return br.readLine();
            } finally {
                br.close();
                hc.close();
            }
        } catch (IOException ex) {
            return null;
        }
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
        final RoyalBot rb = RoyalBot.getInstance();
        if (!rb.getConfig().getPastebinEnabled()) return null;
        final String pastebin = BotUtils.pastebin(BotUtils.getStackTrace(ex));
        if (pastebin != null) {
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
        return getContent(shorten.toString());
    }

}
