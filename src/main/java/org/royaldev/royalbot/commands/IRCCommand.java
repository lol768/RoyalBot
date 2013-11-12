package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;

public interface IRCCommand {

    /**
     * This method is called when a command is received. Depending on what {@link #getCommandType()} returns, the event
     * passed to this method will either be a {@link org.pircbotx.hooks.events.MessageEvent} or a
     * {@link org.pircbotx.hooks.events.PrivateMessageEvent}.
     *
     * @param event Event of receiving command
     * @param args  Arguments passed to the command
     */
    public void onCommand(GenericMessageEvent event, String[] args);

    /**
     * This should return the name of the command. An example would be "ping"
     * <br/>
     * Case does not matter; do not include a command prefix.
     *
     * @return Name of the command.
     */
    public String getName();

    /**
     * Gets the usage for this command. Should follow this format:
     * <pre>"&lt;command&gt; [required] (optional)"</pre>
     * Do not replace "&lt;command&gt;" with the actual name of the command; that will automatically be done.
     *
     * @return Usage string
     */
    public String getUsage();

    /**
     * Gets a brief description of the command.
     *
     * @return <em>Brief</em> description
     */
    public String getDescription();

    /**
     * Gets an array of names that can be used for this command.
     *
     * @return Array, not null
     */
    public String[] getAliases();

    /**
     * This should return what type of command this is.
     *
     * @return CommandType
     */
    public CommandType getCommandType();

    /**
     * Returns the auth level a user must be in order to use the command.
     *
     * @return AuthLevel
     */
    public AuthLevel getAuthLevel();

    public static enum CommandType {
        MESSAGE("Channel message only"),
        PRIVATE("Private message only"),
        BOTH("Channel or private message");

        private final String description;

        CommandType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public static enum AuthLevel {
        PUBLIC,
        ADMIN,
        SUPERADMIN
    }
}
