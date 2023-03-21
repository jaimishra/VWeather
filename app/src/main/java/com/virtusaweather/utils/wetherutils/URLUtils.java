package com.virtusaweather.utils.wetherutils;


import static com.virtusaweather.constants.Constants.COUNTRY_CODE_US;

import com.virtusaweather.constants.Constants;
import com.virtusaweather.BuildConfig;


public class URLUtils {
    private final String link;
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?";
    private static String city_url;

    public URLUtils() {
        link = BASE_URL + "lat=" + LocationCoordinate.lat + "&lon=" +
                LocationCoordinate.lon + "&appid=" + BuildConfig.API_KEY;
    }

    public String getLink() {
        return link;
    }

    /* Showing only US cities by adding "US" code statically in Url.
    Due to this only US cities with name and zipcode can be found */
    public static void setCity_url(String cityName) {
        city_url = BASE_URL + "&q=" + cityName + "," + COUNTRY_CODE_US + "&appid=" + BuildConfig.API_KEY + "&units=" + Constants.UNITS;
    }

    public static String getCity_url() {
        return city_url;
    }

}
