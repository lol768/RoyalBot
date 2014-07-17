package org.royaldev.royalbot;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.CharOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.LongOptionHandler;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.managers.ThreadedListenerManager;
import org.royaldev.royalbot.commands.ChannelCommand;
import org.royaldev.royalbot.commands.impl.*;
import org.royaldev.royalbot.commands.impl.channelmanagement.ChannelManagementCommand;
import org.royaldev.royalbot.configuration.Config;
import org.royaldev.royalbot.configuration.ConfigurationSection;
import org.royaldev.royalbot.handlers.CommandHandler;
import org.royaldev.royalbot.handlers.ListenerHandler;
import org.royaldev.royalbot.listeners.impl.YouTubeListener;
import org.royaldev.royalbot.plugins.PluginLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The main bot.
 */
public class RoyalBot {

    private static RoyalBot instance;
    private final PircBotX bot;
    private final Logger logger = Logger.getLogger("org.royaldev.royalbot.RoyalBot");
    @SuppressWarnings("FieldCanBeLocal")
    private final String botVersion = this.getClass().getPackage().getImplementationVersion();
    private final CommandHandler ch = new CommandHandler();
    private final ListenerHandler lh = new ListenerHandler();
    private final PluginHandler ph = new PluginHandler();
    @SuppressWarnings("FieldCanBeLocal")
    private final PluginLoader pl = new PluginLoader(this);
    private final Config c;
    private Random random;
    @Option(name = "-n", usage = "Define the nickname of the bot", aliases = {"--nick"})
    private String botNick = "LolBot";
    @Option(name = "-r", usage = "Define the real name of the bot", aliases = {"--real-name"})
    private String botRealname = "LolBot";
    @Option(name = "-f", usage = "Define the response the a CTCP FINGER query", aliases = {"--finger"})
    private String botFinger = "lol768's IRC bot, modified by Bionicrm (created by RoyalDev)";
    @Option(name = "-l", usage = "Define the bot's login to the server", aliases = {"--login"})
    private String botLogin = "LolBot";
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
    @Option(name = "-c", usage = "List of channels to join (e.g. \"#chan #chan2\")", aliases = {"--channels"}, handler = StringArrayOptionHandler.class)
    private String[] channels = new String[0];
    @Option(name = "-p", usage = "Set the port of the server to connect to", aliases = {"--port"}, handler = IntOptionHandler.class)
    private int serverPort = 6667;
    @Option(name = "-d", usage = "Sets the delay between queued messages", aliases = {"--message-delay"}, handler = LongOptionHandler.class)
    private long messageDelay = 1000L;

    private RoyalBot(String[] args) {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ex) {
            random = new Random();
        }
        final ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new Formatter() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            @Override
            public String format(LogRecord logRecord) {
                return sdf.format(new Date()) + " [" + logRecord.getLevel().getLocalizedName() + "] " + logRecord.getMessage() + System.getProperty("line.separator");
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
        final Configuration.Builder<PircBotX> cb = new Configuration.Builder<>();
        cb.setServer(serverHostname, serverPort)
                .setName(botNick)
                .setRealName(botRealname)
                .setLogin(botLogin)
                .setFinger(botFinger)
                .setVersion("RoyalBot " + botVersion)
                .setListenerManager(new ThreadedListenerManager<>())
                .addListener(new BaseListeners(this))
                .setMessageDelay(messageDelay)
                .setAutoNickChange(true);
        for (String channel : channels) cb.addAutoJoinChannel(channel);
        for (String channel : c.getChannels()) cb.addAutoJoinChannel(channel);
        if (!serverPassword.isEmpty()) cb.setServerPassword(serverPassword);
        if (!nickServPassword.isEmpty()) cb.setNickservPassword(nickServPassword);
        bot = new PircBotX(cb.buildConfiguration());
        addListeners();
        pl.loadPlugins();
        getLogger().info("Connecting.");
        new Thread(new Runnable() {
            public void run() {
                try {
                    bot.startBot();
                } catch (Exception e) {
                    e.printStackTrace();
                    getLogger().severe("Could not start bot: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
                    System.exit(1);
                }
            }
        }).start();
    }

    /**
     * Starts the bot from the commandline.
     *
     * @param args Arguments to be used with the bot
     */
    public static void main(String[] args) {
        new RoyalBot(args);
    }

    /**
     * Statically gets the instance of this bot
     *
     * @return Instance
     */
    public static RoyalBot getInstance() {
        return instance;
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
        try (InputStream file = RoyalBot.class.getResourceAsStream("/config.yml"); OutputStream os = new FileOutputStream(f)) {
            int read;
            while ((read = file.read()) != -1) os.write(read);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        getLogger().info("Saved!");
    }

    private void addCommands() {
        ch.register(new AdminCommand());
        ch.register(new LolFaxCommand());
        ch.register(new ChannelManagementCommand());
        ch.register(new ChooseCommand());
        ch.register(new ChuckCommand());
        ch.register(new DefineCommand());
        ch.register(new GoogleCommand());
        ch.register(new HelpCommand());
        ch.register(new IgnoreCommand());
        ch.register(new IsUpCommand());
        ch.register(new JoinCommand());
        ch.register(new MCAccountCommand());
        ch.register(new MCPingCommand());
        ch.register(new MessageCommand());
        ch.register(new NumberFactCommand());
        ch.register(new PartCommand());
        ch.register(new PingCommand());
        ch.register(new QuitCommand());
        ch.register(new RepositoryCommand());
        ch.register(new RollCommand());
        ch.register(new RoyalBotCommand());
        ch.register(new ShakespeareInsultCommand());
        ch.register(new ShortenCommand());
        ch.register(new UrbanDictionaryCommand());
        ch.register(new WeatherCommand());
        ch.register(new WolframAlphaCommand());
    }

    private void addListeners() {
        lh.register(new YouTubeListener());
    }

    private void addChannelCommands() {
        ConfigurationSection cs = getConfig().getChannelCommands();
        for (final String channel : cs.getKeys(false)) {
            ConfigurationSection channelCommands = cs.getConfigurationSection(channel);
            for (final String command : channelCommands.getKeys(false)) {
                final ChannelCommand cc;
                try {
                    cc = BotUtils.createChannelCommand(channelCommands.getString(command, ""), channel);
                } catch (Exception e) {
                    continue;
                }
                ch.register(cc);
            }
        }
    }

    /**
     * Gets the actual backend of the bot.
     *
     * @return PircBotX backend
     */
    public PircBotX getBot() {
        return bot;
    }

    /**
     * Gets the bot's logger. If being accessed by a plugin, {@link org.royaldev.royalbot.plugins.IRCPlugin#getLogger()}
     * should probably being used instead.
     *
     * @return The logger associated with the bot
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets the bot's configuration. All changes made are saved immediately.
     *
     * @return Configuration for the bot
     */
    public Config getConfig() {
        return c;
    }

    /**
     * Gets the CommandHandler of the bot.
     *
     * @return CommandHandler associated with the bot
     */
    public CommandHandler getCommandHandler() {
        return ch;
    }

    /**
     * Gets the ListenerHandler of the bot.
     *
     * @return ListenerHandler associated with the bot
     */
    public ListenerHandler getListenerHandler() {
        return lh;
    }

    /**
     * Gets the PluginHandler of the bot.
     *
     * @return PluginHandler associated with the bot
     */
    public PluginHandler getPluginHandler() {
        return ph;
    }

    /**
     * Gets the PluginLoader for use internally.
     *
     * @return PluginLoader
     */
    protected PluginLoader getPluginLoader() {
        return pl;
    }

    /**
     * Gets the prefix to be used in front of channel commands.
     *
     * @return char
     */
    public char getCommandPrefix() {
        return commandPrefix;
    }

    /**
     * Gets the public Random used for the bot. In order to ensure more random results, it is recommended to use this
     * Random when generating numbers.
     *
     * @return Random
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Gets the path that the JAR is contained in.
     *
     * @return Path the jar is at
     */
    public String getPath() {
        try {
            return new File(RoyalBot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
        } catch (Exception ex) {
            return null;
        }
    }
}
