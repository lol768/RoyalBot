package org.royaldev.royalbot.commands.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.commands.CallInfo;
import org.royaldev.royalbot.commands.NoticeableCommand;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;

public class WeatherCommand extends NoticeableCommand {

    private final ObjectMapper om = new ObjectMapper();
    private final DecimalFormat df = new DecimalFormat("###.##");
    private final String DEGREE = "\u00ba";

    private double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15D;
    }

    private double kelvinToFahrenheit(double kelvin) {
        return celsiusToFahrenheit(kelvinToCelsius(kelvin));
    }

    private double celsiusToFahrenheit(double celsius) {
        return ((9D / 5D) * celsius) + 32D;
    }

    private String[] getOpenWeatherMapData(JsonNode jn) {
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
        return new String[]{
                cityName,
                df.format(kelvinToCelsius(kelvin)) + DEGREE,
                df.format(kelvinToFahrenheit(kelvin)) + DEGREE,
                df.format(kelvinToCelsius(high)) + DEGREE,
                df.format(kelvinToFahrenheit(high)) + DEGREE,
                df.format(kelvinToCelsius(low)) + DEGREE,
                df.format(kelvinToFahrenheit(low)) + DEGREE,
                StringUtils.capitalize(weather),
                df.format(cloudiness) + "% cloudy",
                df.format(wind * 3.6D),
                df.format(humidity) + "%"
        };
    }

    private String[] getWundergroundData(JsonNode jn) {
        JsonNode co = jn.path("current_observation");
        JsonNode fo = jn.path("forecast").path("simpleforecast").path("forecastday").path(0);
        return new String[]{
                co.path("display_location").path("city").asText(),
                df.format(co.path("temp_c").asDouble()) + DEGREE,
                df.format(co.path("temp_f").asDouble()) + DEGREE,
                df.format(fo.path("high").path("celsius").asDouble()) + DEGREE,
                df.format(fo.path("high").path("fahrenheit").asDouble()) + DEGREE,
                df.format(fo.path("low").path("celsius").asDouble()) + DEGREE,
                df.format(fo.path("low").path("fahrenheit").asDouble()) + DEGREE,
                fo.path("conditions").asText(),
                co.path("weather").asText(),
                co.path("wind_kph").asText(),
                co.path("relative_humidity").asText()
        };
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo callInfo, String[] args) {
        final RoyalBot rb = RoyalBot.getInstance();
        if (args.length < 1) {
            notice(event, "Not enough arguments.");
            return;
        }
        String url;
        try {
            final String query = URLEncoder.encode(StringUtils.join(args, ' '), "UTF-8");
            if (rb.getConfig().isWundergroundEnabled())
                url = String.format("http://api.wunderground.com/api/%s/conditions/forecast/q/%s.json", URLEncoder.encode(rb.getConfig().getWundergroundAPIKey(), "UTF-8"), query);
            else url = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s", query);
        } catch (UnsupportedEncodingException ex) {
            notice(event, "Couldn't encode in UTF-8.");
            return;
        }
        JsonNode jn;
        try {
            jn = om.readTree(BotUtils.getContent(url));
        } catch (Exception ex) {
            notice(event, "Unknown area.");
            return;
        }
        event.respond(String.format("Weather in %s: Currently %sC (%sF). High is %sC (%sF); low is %sC (%sF). %s. %s. Wind at %skm/h. Humidity is %s.",
                rb.getConfig().isWundergroundEnabled() ? getWundergroundData(jn) : getOpenWeatherMapData(jn)
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
