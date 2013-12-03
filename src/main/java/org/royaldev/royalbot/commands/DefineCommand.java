package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.RoyalBot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URLEncoder;

public class DefineCommand implements IRCCommand {

    private RoyalBot rb = RoyalBot.getInstance();

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        if (!rb.getConfig().getDictionaryAPIEnabled()) {
            event.respond("Dictionary API is turned off on this bot.");
            return;
        }
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        final String content;
        try {
            content = BotUtils.getContent(String.format("http://www.dictionaryapi.com/api/v1/references/collegiate/xml/%s?key=%s", URLEncoder.encode(args[0], "UTF-8"), URLEncoder.encode(rb.getConfig().getDictionaryAPIKey(), "UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            event.respond("Could not get definition!");
            return;
        }
        int entryNumber = 0;
        int defNumber = 0;
        if (args.length > 1) {
            try {
                String[] split = args[1].split("/");
                entryNumber = Integer.parseInt(split[0]) - 1;
                if (split.length > 1) defNumber = Integer.parseInt(split[1]) - 1;
            } catch (NumberFormatException ex) {
                event.respond("A number provided was not a number!");
                return;
            }
        }
        if (entryNumber < 0 || defNumber < 0) {
            event.respond("A number was less than one.");
            return;
        }
        final String toSend;
        final int numberEntries;
        final int numberDefinitions;
        try {
            final Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(content)));
            Element root = d.getDocumentElement();
            root.normalize();
            NodeList nl = root.getElementsByTagName("entry");
            numberEntries = nl.getLength();
            if (numberEntries < 1) {
                event.respond("No entries found.");
                return;
            }
            if (entryNumber + 1 > numberEntries) {
                event.respond("Invalid entry number.");
                return;
            }
            Element e = (Element) nl.item(entryNumber);
            nl = e.getElementsByTagName("dt");
            numberDefinitions = nl.getLength();
            if (defNumber + 1 > numberDefinitions) {
                event.respond("Invalid definition number.");
                return;
            }
            e = (Element) nl.item(defNumber);
            toSend = e.getTextContent();
        } catch (Exception e) {
            event.respond(BotUtils.formatException(e));
            return;
        }
        event.respond(String.format("(%s entries, %s defs) %s", numberEntries, numberDefinitions, toSend.substring(1)));
    }

    @Override
    public String getName() {
        return "define";
    }

    @Override
    public String getUsage() {
        return "<command> [word] (entry/definition)";
    }

    @Override
    public String getDescription() {
        return "Fetches a word from the dictionary";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"definition"};
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
