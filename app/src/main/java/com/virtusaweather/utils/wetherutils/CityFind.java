package com.virtusaweather.utils.wetherutils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.util.List;
import java.util.Locale;

public class CityFind {

    public static void setLongitudeLatitude(Location location) {
        try {
            LocationCoordinate.lat = String.valueOf(location.getLatitude());
            LocationCoordinate.lon = String.valueOf(location.getLongitude());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static String getCityNameUsingNetwork(Context context, Location location) {
        String city = "";
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            city = addresses.get(0).getLocality();
        } catch (Exception e) {
            Log.d("city", "Error to find the city.");
        }
        return city;
    }
}
