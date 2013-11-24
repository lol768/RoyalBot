package org.royaldev.royalbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.CharOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.LongOptionHandler;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.royaldev.royalbot.commands.*;
import org.royaldev.royalbot.configuration.Config;
import org.royaldev.royalbot.configuration.ConfigurationSection;
import org.royaldev.royalbot.listeners.YouTubeListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class RoyalBot {

    public static void main(String[] args) {
        new RoyalBot(args);
    }

    private final PircBotX bot;
    private final Logger logger = Logger.getLogger("org.royaldev.royalbot.RoyalBot");
    @SuppressWarnings("FieldCanBeLocal")
    private String botVersion = this.getClass().getPackage().getImplementationVersion();
    private final CommandHandler ch = new CommandHandler();
    private final Config c;
    private static RoyalBot instance;

    @Option(name = "-n", usage = "Define the nickname of the bot", aliases = {"--nick"})
    private String botNick = "RoyalBot";
    @Option(name = "-r", usage = "Define the real name of the bot", aliases = {"--real-name"})
    private String botRealname = "RoyalBot";
    @Option(name = "-f", usage = "Define the response the a CTCP FINGER query", aliases = {"--finger"})
    private String botFinger = "RoyalDev's IRC Management Bot";
    @Option(name = "-l", usage = "Define the bot's login to the server", aliases = {"--login"})
    private String botLogin = "RoyalBot";
    @Option(name = "-s", usage = "Set the server to connect to", aliases = {"--server"}, required = true)
    private String serverHostname;
    @Option(name = "-P", usage = "Set the password of the server", aliases = {"--server-password"})
    private String serverPassword = "";
    @Option(name = "-A", usage = "Set the NickServ password to use", aliases = {"--nickserv-password"})
    private String nickServPassword = "";
    @Option(name = "-z", usage = "Sets the path to the configuration file", aliases = {"--config"})
    private String configPath = null;
    @Option(name = "-C", usage = "Sets the command prefix (one character) to use for the bot", aliases = {"--command-prefix"}, handler = CharOptionHandler.class)
    private char commandPrefix = ':';
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    @Option(name = "-c", usage = "List of channels to join (e.g. \"#chan #chan2\"", aliases = {"--channels"})
    private String[] channels = new String[0];
    @Option(name = "-p", usage = "Set the port of the server to connect to", aliases = {"--port"}, handler = IntOptionHandler.class)
    private int serverPort = 6667;
    @Option(name = "-d", usage = "Sets the delay between queued messages", aliases = {"--message-delay"}, handler = LongOptionHandler.class)
    private long messageDelay = 1000L;

    private RoyalBot(String[] args) {
        final ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new Formatter() {
            private final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            public String format(LogRecord logRecord) {
                final StringBuilder sb = new StringBuilder();
                sb.append(dtf.print(System.currentTimeMillis()));
                sb.append(" [").append(logRecord.getLevel().getLocalizedName()).append("] ");
                sb.append(logRecord.getMessage()).append("\n");
                return sb.toString();
            }
        });
        getLogger().setUseParentHandlers(false);
        getLogger().addHandler(ch);
        // Set up log format before logging
        getLogger().info("Starting.");
        instance = this;
        final CmdLineParser clp = new CmdLineParser(this);
        try {
            clp.parseArgument(args);
        } catch (CmdLineException e) {
            System.out.println(e.getMessage());
            clp.printUsage(System.out);
            System.exit(1);
        }
        saveDefaultConfig();
        c = new Config(configPath);
        addCommands();
        addChannelCommands();
        final Configuration.Builder<PircBotX> cb = new Configuration.Builder<PircBotX>();
        cb.setServer(serverHostname, serverPort)
                .setName(botNick)
                .setRealName(botRealname)
                .setLogin(botLogin)
                .setFinger(botFinger)
                .setVersion("RoyalBot " + botVersion)
                .addListener(new BaseListeners(this))
                .setMessageDelay(messageDelay)
                .setAutoNickChange(true);
        for (String channel : channels) cb.addAutoJoinChannel(channel);
        for (String channel : c.getChannels()) cb.addAutoJoinChannel(channel);
        if (!serverPassword.isEmpty()) cb.setServerPassword(serverPassword);
        if (!nickServPassword.isEmpty()) cb.setNickservPassword(nickServPassword);
        addListeners(cb);
        bot = new PircBotX(cb.buildConfiguration());
        getLogger().info("Connecting.");
        new Thread(new Runnable() {
            public void run() {
                try {
                    bot.startBot();
                } catch (Exception e) {
                    getLogger().severe("Could not start bot: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
                    System.exit(1);
                }
            }
        }).start();
    }

    private void saveDefaultConfig() {
        final File f;
        try {
            f = (configPath == null) ? new File(URLDecoder.decode(RoyalBot.class.getProtectionDomain().getCodeSource().getLocation().toURI().resolve(".").getPath(), "UTF-8"), "config.yml") : new File(configPath);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        if (f.exists()) return;
        getLogger().info("Saving default config.");
        try {
            if (!f.createNewFile()) return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            InputStream file = RoyalBot.class.getResourceAsStream("/config.yml");
            OutputStream os = new FileOutputStream(f);
            try {
                int read;
                while ((read = file.read()) != -1) os.write(read);
                os.flush();
            } finally {
                os.close();
                file.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        getLogger().info("Saved!");
    }

    private void addCommands() {
        ch.registerCommand(new AdminCommand());
        ch.registerCommand(new BaxFaxCommand());
        ch.registerCommand(new ChannelCommandCommand());
        ch.registerCommand(new ChuckCommand());
        ch.registerCommand(new DefineCommand());
        ch.registerCommand(new HelpCommand());
        ch.registerCommand(new JoinCommand());
        ch.registerCommand(new MCAccountCommand());
        ch.registerCommand(new MCPingCommand());
        ch.registerCommand(new PartCommand());
        ch.registerCommand(new PingCommand());
        ch.registerCommand(new QuitCommand());
        ch.registerCommand(new RepositoryCommand());
        ch.registerCommand(new RoyalBotCommand());
        ch.registerCommand(new ShakespeareInsultCommand());
        ch.registerCommand(new ShortenCommand());
        ch.registerCommand(new WolframAlphaCommand());
    }

    private void addListeners(Configuration.Builder<PircBotX> cb) {
        cb.addListener(new YouTubeListener());
    }

    private void addChannelCommands() {
        ConfigurationSection cs = getConfig().getChannelCommands();
        final ObjectMapper om = new ObjectMapper();
        for (final String channel : cs.getKeys(false)) {
            ConfigurationSection channelCommands = cs.getConfigurationSection(channel);
            for (final String command : channelCommands.getKeys(false)) {
                JsonNode jn;
                try {
                    jn = om.readTree(channelCommands.getString(command, ""));
                } catch (Exception ex) {
                    continue;
                }
                final String name = jn.path("name").asText().trim();
                final String desc = jn.path("description").asText().trim();
                final String usage = jn.path("usage").asText().trim();
                final String auth = jn.path("auth").asText().trim();
                final String script = jn.path("script").asText().trim();
                final List<String> aliases = new ArrayList<String>();
                for (String alias : jn.path("aliases").asText().trim().split(","))
                    aliases.add(alias.trim() + ":" + channel);
                if (name.isEmpty() || desc.isEmpty() || usage.isEmpty() || auth.isEmpty() || script.isEmpty()) continue;
                final IRCCommand.AuthLevel al;
                try {
                    al = IRCCommand.AuthLevel.valueOf(auth.toUpperCase());
                } catch (IllegalArgumentException e) {
                    continue;
                }
                ch.registerCommand(new ChannelCommand() {
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
                });
            }
        }
    }

    public PircBotX getBot() {
        return bot;
    }

    public Logger getLogger() {
        return logger;
    }

    public Config getConfig() {
        return c;
    }

    public CommandHandler getCommandHandler() {
        return ch;
    }

    public char getCommandPrefix() {
        return commandPrefix;
    }

    public static RoyalBot getInstance() {
        return instance;
    }
}
