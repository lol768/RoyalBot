package org.royaldev.royalbot.configuration;

import org.apache.commons.io.FileUtils;
import org.royaldev.royalbot.RoyalBot;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ConfigUpdater implements Runnable {

    public static final String CONFIG_NAME = "config.yml";
    public static final String CONFIG_URL_GITHUB = ("https://raw.githubusercontent.com/Bionicrm/RoyalBot/master/src/main/resources/" + CONFIG_NAME);
    public static final int CONNECTION_TIMEOUT = (60 * 1000); // seconds
    public static final int UPDATE_PERIOD = (12 * (60 * 60 * 1000)); // hours

    private boolean continueUpdating = true;

    @Override
    public void run() {
        final URL url;
        try {
            url = new URL(CONFIG_URL_GITHUB);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return;
        }
        final File containingDirectory = new File(RoyalBot.getInstance().getPath(), CONFIG_NAME);
        do {
            try {
                FileUtils.copyURLToFile(url, containingDirectory, CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
                RoyalBot.getInstance().getConfig().load();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            try {
                Thread.sleep(UPDATE_PERIOD);
            } catch (InterruptedException ignored) {
                return;
            }
        } while (continueUpdating);
    }

    public synchronized void stop(Thread thisThread) {
        continueUpdating = false;
        thisThread.interrupt();
    }

}
