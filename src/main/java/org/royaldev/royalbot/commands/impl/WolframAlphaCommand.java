package org.royaldev.royalbot.commands.impl;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.commands.NoticeableCommand;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URLEncoder;

public class WolframAlphaCommand extends NoticeableCommand {

    private final RoyalBot rb = RoyalBot.getInstance();

    @Override
    public void onCommand(GenericMessageEvent event, String label, String[] args) {
        if (!rb.getConfig().getWolframAlphaEnabled()) {
            notice(event, "WolframAlpha is disabled on this bot.");
            return;
        }
        if (args.length < 1) {
            notice(event, "Not enough arguments.");
            return;
        }
        final String query = StringUtils.join(args, ' ');
        final String content;
        try {
            content = BotUtils.getContent(String.format("http://api.wolframalpha.com/v2/query?appid=%s&input=%s", rb.getConfig().getWolframAlphaAPIKey(), URLEncoder.encode(query, "UTF-8")));
        } catch (Exception e) {
            notice(event, "An error occurred while contacting WolframAlpha.");
            return;
        }
        final Document d;
        try {
            d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(content)));
        } catch (Exception ex) {
            notice(event, BotUtils.formatException(ex));
            return;
        }
        Element root = d.getDocumentElement();
        root.normalize();
        if (!root.hasAttribute("success") || !root.getAttribute("success").equalsIgnoreCase("true")) {
            StringBuilder sb = new StringBuilder("WolframAlpha returned with an error!");
            if (!root.getAttribute("error").equalsIgnoreCase("false"))
                sb.append(" ").append(root.getAttribute("error"));
            notice(event, sb.toString());
            return;
        }
        final String toSend;
        try {
            Element e = (Element) root.getElementsByTagName("pod").item(1);
            e = (Element) e.getElementsByTagName("subpod").item(0);
            e = (Element) e.getElementsByTagName("plaintext").item(0);
            toSend = e.getTextContent();
        } catch (Exception e) {
            notice(event, BotUtils.formatException(e));
            return;
        }
        event.respond(toSend.replaceAll("(\r)?\n", " / "));
    }

    @Override
    public String getName() {
        return "wolframalpha";
    }

    @Override
    public String getUsage() {
        return "<command> [query]";
    }

    @Override
    public String getDescription() {
        return "Sends the provided query to WolframAlpha";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"wa"};
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
