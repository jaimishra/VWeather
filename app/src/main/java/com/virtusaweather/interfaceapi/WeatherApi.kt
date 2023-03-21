package com.virtusaweather.interfaceapi
import com.virtusaweather.model.weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface WeatherApi {

    @GET
    suspend fun getWeatherData(@Url url: String?): Response<WeatherResponse>

}