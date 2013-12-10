package org.royaldev.royalbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.pircbotx.User;
import org.royaldev.royalbot.commands.ChannelCommand;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BotUtils {

    private final static ObjectMapper om = new ObjectMapper();

    /**
     * Gets the appropriate string to send to a user if an exception is encountered.
     *
     * @param t Exception to format
     * @return Message to send user; never null
     */
    public static String formatException(Throwable t) {
        return "Exception! " + t.getClass().getSimpleName() + ": " + t.getMessage();
    }

    /**
     * Converts a Throwable's stack trace into a String.
     *
     * @param t Throwable
     * @return Stack trace as string
     */
    public static String getStackTrace(Throwable t) {
        final StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * Pastes something to Hastebin.
     *
     * @param paste String to paste
     * @return Hastebin URL or null if error encountered
     */
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
     * Convenience method to get a stack trace from an Exception, send it to Hastebin, and then shorten the link with
     * is.gd.
     * <br/>
     * <strong>Note:</strong> If <em>any</em> errors occur, this will simply return null, and you will get no feedback
     * of the error.
     *
     * @param t Exception to do this with
     * @return Shortened link to the stack trace or null
     */
    public static String linkToStackTrace(Throwable t) {
        String pastebin = BotUtils.pastebin(BotUtils.getStackTrace(t));
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
        return sb.substring(0, sb.length() - 1); // remove last newline
    }

    /**
     * Shortens a URL with is.gd.
     *
     * @param url URL to shorten
     * @return Shortened URL
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String shortenURL(String url) throws IOException, URISyntaxException {
        final URL shorten = new URL("http://is.gd/create.php?format=simple&url=" + URLEncoder.encode(url, "UTF-8"));
        return getContent(shorten.toString());
    }

    /**
     * Gets a string to send a user if help is requested for a command.
     *
     * @param ic Command to get help for
     * @return String to send user
     */
    public static String getHelpString(IRCCommand ic) {
        return ic.getName() + " / Description: " + ic.getDescription() + " / Usage: " + ic.getUsage().replaceAll("(?i)<command>", ic.getName()) + " / Aliases: " + Arrays.toString(ic.getAliases()) + " / Type: " + ic.getCommandType().getDescription();
    }

    /**
     * Creates a channel-specific command based on JSON.
     *
     * @param commandJson JSON of command
     * @param channel     Channel for command
     * @return ChannelCommand
     * @throws RuntimeException If there is any error
     */
    public static ChannelCommand createChannelCommand(String commandJson, final String channel) throws RuntimeException {
        final JsonNode jn;
        try {
            jn = om.readTree(commandJson);
        } catch (Exception ex) {
            String paste = BotUtils.linkToStackTrace(ex);
            throw new RuntimeException("An error occurred reading that!" + ((paste != null) ? " (" + paste + ")" : ""));
        }
        final String name = jn.path("name").asText().trim();
        final String desc = jn.path("description").asText().trim();
        final String usage = jn.path("usage").asText().trim();
        final String auth = jn.path("auth").asText().trim();
        final String script = jn.path("script").asText().trim();
        final List<String> aliases = new ArrayList<String>();
        for (String alias : jn.path("aliases").asText().trim().split(",")) {
            alias = alias.trim();
            if (alias.isEmpty()) continue;
            aliases.add(alias + ":" + channel);
        }
        if (name.isEmpty() || desc.isEmpty() || usage.isEmpty() || auth.isEmpty() || script.isEmpty()) {
            throw new RuntimeException("Invalid JSON.");
        }
        final IRCCommand.AuthLevel al;
        try {
            al = IRCCommand.AuthLevel.valueOf(auth.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid auth level.");
        }
        return new ChannelCommand() {
            @Override
            public String getBaseName() {
                return name;
            }

            @Override
            public String getChannel() {
                return channel;
            }

            @Override
            public String getJavaScript() {
                return script;
            }

            @Override
            public String getUsage() {
                return usage;
            }

            @Override
            public String getDescription() {
                return desc;
            }

            @Override
            public String[] getAliases() {
                return aliases.toArray(new String[aliases.size()]);
            }

            @Override
            public AuthLevel getAuthLevel() {
                return al;
            }
        };
    }

    /**
     * Checks if a hostmask matches a pattern. This replaces "*" with ".+" prior to checking, and it does use regex, as
     * one would assume.
     *
     * @param hostmask     Hostmask of a user
     * @param checkAgainst Hostmask pattern to check against
     * @return true if hostmask matches checkAgainst, false if otherwise
     */
    public static boolean doesHostmaskMatch(String hostmask, String checkAgainst) {
        checkAgainst = checkAgainst.replace("*", ".+");
        return hostmask.matches(checkAgainst);
    }

    /**
     * Checks to see if a hostmask is ignored by the bot.
     *
     * @param hostmask Hostmask to check
     * @return true if hostmask is ignored, false if not
     */
    public static boolean isIgnored(String hostmask) {
        final List<String> ignores = RoyalBot.getInstance().getConfig().getIgnores();
        for (String ignore : ignores) {
            if (ignore.equals(hostmask)) return true;
            if (doesHostmaskMatch(hostmask, ignore)) return true;
        }
        return false;
    }

    /**
     * Temporary workaround for PircBotX not returning the right value for {@link org.pircbotx.User#getHostmask()}.
     *
     * @param user User to get hostmask of
     * @return Real hostmask
     */
    public static String generateHostmask(User user) {
        return user.getNick() + "!" + user.getLogin() + "@" + user.getHostmask();
    }

}
