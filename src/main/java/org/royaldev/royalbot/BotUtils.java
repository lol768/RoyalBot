package org.royaldev.royalbot;

public class BotUtils {

    public static String formatException(Exception e) {
        return "Exception! " + e.getClass().getSimpleName() + ": " + e.getMessage();
    }

}
