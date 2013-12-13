package org.royaldev.royalbot.commands;

/**
 * This class contains information on the calling of any given command.
 */
public class CallInfo {

    private final String label;
    private final UsageType usageType;

    /**
     * Creates a new CallInfo object initialized with the data necessary.
     *
     * @param label     User-provided command name
     * @param usageType Where the command was used
     */
    public CallInfo(String label, UsageType usageType) {
        this.label = label;
        this.usageType = usageType;
    }

    /**
     * Returns the name of the command given by the user.
     *
     * @return Name of the command (possibly alias)
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns where the usage occurred.
     *
     * @return UsageType
     */
    public UsageType getUsageType() {
        return usageType;
    }

    /**
     * Usage of the command.
     */
    public static enum UsageType {
        /**
         * Command was used in a channel message.
         */
        MESSAGE,
        /**
         * Command was used in a private message.
         */
        PRIVATE
    }
}
