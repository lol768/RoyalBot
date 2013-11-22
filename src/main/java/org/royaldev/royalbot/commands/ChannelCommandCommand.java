package org.royaldev.royalbot.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.RoyalBot;

public class ChannelCommandCommand implements IRCCommand {

    private final RoyalBot rb = RoyalBot.getInstance();
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments!");
            return;
        }
        final String subcommand = args[0];
        if (subcommand.equalsIgnoreCase("list")) {
            if (args.length < 2) {
                event.respond("Not enough arguments!");
                return;
            }
            final String channel = args[1];
            int number = 0;
            for (IRCCommand ic : rb.getCommandHandler().getAllCommands()) {
                if (!(ic instanceof ChannelCommand)) continue;
                ChannelCommand cc = (ChannelCommand) ic;
                if (!cc.getChannel().equalsIgnoreCase(channel)) continue;
                event.respond(BotUtils.getHelpString(cc));
                number++;
            }
            if (number < 1) event.respond("No commands for that channel.");
        } else if (subcommand.equalsIgnoreCase("add")) {
            if (args.length < 3) {
                event.respond("Not enough arguments.");
                return;
            }
            final String channel = args[1];
            final String url = args[2];
            if (!event.getBot().getUserChannelDao().getChannel(channel).getOps().contains(event.getUser()) && !event.getUser().isIrcop()) {
                event.respond("You are not an operator in that channel.");
                return;
            }
            JsonNode jn;
            try {
                jn = om.readTree(BotUtils.getContent(url));
            } catch (Exception ex) {
                String paste = BotUtils.linkToStackTrace(ex);
                event.respond("An error occurred reading that!" + ((paste != null) ? " (" + paste + ")" : ""));
                return;
            }
            final String name = jn.path("name").asText().trim();
            final String desc = jn.path("description").asText().trim();
            final String usage = jn.path("usage").asText().trim();
            final String auth = jn.path("auth").asText().trim();
            final String script = jn.path("script").asText().trim();
            if (name.isEmpty() || desc.isEmpty() || usage.isEmpty() || auth.isEmpty() || script.isEmpty()) {
                event.respond("Invalid JSON.");
                return;
            }
            final AuthLevel al;
            try {
                al = AuthLevel.valueOf(auth.toUpperCase());
            } catch (IllegalArgumentException e) {
                event.respond("Invalid auth level.");
                return;
            }
            rb.getCommandHandler().registerCommand(new ChannelCommand() {
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
                    return new String[0];
                }

                @Override
                public AuthLevel getAuthLevel() {
                    return al;
                }
            });
            event.respond("Attempted to register command.");
            rb.getConfig().getChannelCommands().set(channel + "." + name, jn.toString());
            rb.getConfig().save();
        } else if (subcommand.equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                event.respond("Not enough arguments.");
                return;
            }
            final String channel = args[1];
            final String command = args[2];
            if (!event.getBot().getUserChannelDao().getChannel(channel).getOps().contains(event.getUser()) && !event.getUser().isIrcop()) {
                event.respond("You are not an operator in that channel.");
                return;
            }
            IRCCommand ic = rb.getCommandHandler().getCommand(command + ":" + channel);
            if (ic == null || !(ic instanceof ChannelCommand)) {
                event.respond("No such command.");
                return;
            }
            if (!((ChannelCommand) ic).getChannel().equalsIgnoreCase(channel)) {
                event.respond("That command does not appear to register with the channel.");
                return;
            }
            rb.getCommandHandler().unregisterCommand(ic.getName());
            event.respond("Unregistered.");
            rb.getConfig().getChannelCommands().set(channel + "." + ic.getName(), null);
            rb.getConfig().save();
        } else {
            event.respond("No such subcommand.");
        }
    }

    @Override
    public String getName() {
        return "channelcommand";
    }

    @Override
    public String getUsage() {
        return "<command> [subcommand/help]";
    }

    @Override
    public String getDescription() {
        return "Creates channel-specific commands!";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cc", "chancom"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.PRIVATE;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.PUBLIC;
    }
}
