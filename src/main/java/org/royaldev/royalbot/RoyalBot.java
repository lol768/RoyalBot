package org.royaldev.royalbot;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.CharOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.LongOptionHandler;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.royaldev.royalbot.commands.AdminCommand;
import org.royaldev.royalbot.commands.HelpCommand;
import org.royaldev.royalbot.commands.MCPingCommand;
import org.royaldev.royalbot.commands.PingCommand;
import org.royaldev.royalbot.commands.QuitCommand;
import org.royaldev.royalbot.configuration.Config;
import org.royaldev.royalbot.listeners.YouTubeListener;

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
    @Option(name = "-s", usage = "Set the server to connect to", aliases = {"--server"})
    private String serverHostname = "irc.esper.net";
    @Option(name = "-P", usage = "Set the password of the server", aliases = {"--server-password"})
    private String serverPassword = "";
    @Option(name = "-A", usage = "Set the NickServ password to use", aliases = {"--nickserv-password"})
    private String nickServPassword = "";
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
        instance = this;
        c = new Config();
        final CmdLineParser clp = new CmdLineParser(this);
        try {
            clp.parseArgument(args);
        } catch (CmdLineException e) {
            System.out.println(e.getMessage());
            clp.printUsage(System.out);
            System.exit(1);
        }
        final ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord logRecord) {
                final StringBuilder sb = new StringBuilder();
                sb.append("[").append(logRecord.getLevel().getLocalizedName()).append("] ");
                sb.append(logRecord.getMessage()).append("\n");
                return sb.toString();
            }
        });
        getLogger().setUseParentHandlers(false);
        getLogger().addHandler(ch);
        addCommands();
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
        if (!serverPassword.isEmpty()) cb.setServerPassword(serverPassword);
        if (!nickServPassword.isEmpty()) cb.setNickservPassword(nickServPassword);
        addListeners(cb);
        bot = new PircBotX(cb.buildConfiguration());
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

    private void addCommands() {
        ch.registerCommand(new HelpCommand());
        ch.registerCommand(new PingCommand());
        ch.registerCommand(new QuitCommand());
        ch.registerCommand(new MCPingCommand());
        ch.registerCommand(new AdminCommand());
    }

    private void addListeners(Configuration.Builder<PircBotX> cb) {
        cb.addListener(new YouTubeListener());
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
