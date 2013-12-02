package org.royaldev.royalbot.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;

public class WeatherCommand implements IRCCommand {

    private final ObjectMapper om = new ObjectMapper();
    private final DecimalFormat df = new DecimalFormat("###.##");

    private double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15D;
    }

    private double kelvinToFahrenheit(double kelvin) {
        return celsiusToFahrenheit(kelvinToCelsius(kelvin));
    }

    private double celsiusToFahrenheit(double celsius) {
        return ((9D / 5D) * celsius) + 32D;
    }

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        String url;
        try {
            url = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s", URLEncoder.encode(StringUtils.join(args, ' '), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            event.respond("Couldn't encode in UTF-8.");
            return;
        }
        JsonNode jn;
        try {
            jn = om.readTree(BotUtils.getContent(url));
        } catch (Exception ex) {
            event.respond("Unknown area.");
            return;
        }
        JsonNode main = jn.path("main");
        String cityName = jn.path("name").asText();
        if (cityName.trim().isEmpty()) cityName = "area";
        String weather = jn.path("weather").path(0).path("description").asText();
        double cloudiness = jn.path("clouds").path("all").asDouble();
        double wind = jn.path("wind").path("speed").asDouble();
        double humidity = main.path("humidity").asDouble();
        double kelvin = main.path("temp").asDouble();
        double low = main.path("temp_min").asDouble();
        double high = main.path("temp_max").asDouble();
        final String DEGREE = "\u00ba";
        event.respond(String.format("Weather in %s: Currently %sC (%sF). High is %sC (%sF); low is %sC (%sF). %s. %s%% cloudy. Wind at %sm/s. Humidity is %s%%.",
                cityName,
                df.format(kelvinToCelsius(kelvin)) + DEGREE,
                df.format(kelvinToFahrenheit(kelvin)) + DEGREE,
                df.format(kelvinToCelsius(high)) + DEGREE,
                df.format(kelvinToFahrenheit(high)) + DEGREE,
                df.format(kelvinToCelsius(low)) + DEGREE,
                df.format(kelvinToFahrenheit(low)) + DEGREE,
                StringUtils.capitalize(weather),
                df.format(cloudiness),
                df.format(wind),
                df.format(humidity)
        ));
    }

    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public String getUsage() {
        return "<command> [area]";
    }

    @Override
    public String getDescription() {
        return "Gets the current weather for an area";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"temp", "temperature", "w"};
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
