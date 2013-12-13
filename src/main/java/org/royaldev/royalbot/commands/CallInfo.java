package org.royaldev.royalbot.commands;

/**
 * This class contains information on the calling of any given command.
 */
public class CallInfo {

    private final String label;
    private final UsageType usageType;

    public CallInfo(String label, UsageType usageType) {
        this.label = label;
        this.usageType = usageType;
    }

    public String getLabel() {
        return label;
    }

    public UsageType getUsageType() {
        return usageType;
    }

    public static enum UsageType {
        MESSAGE,
        PRIVATE
    }
}
