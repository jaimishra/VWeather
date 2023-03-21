package com.virtusaweather.repository

import androidx.lifecycle.MutableLiveData
import com.virtusaweather.interfaceapi.WeatherApi
import com.virtusaweather.model.NetworkResult
import com.virtusaweather.model.weather.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherApi: WeatherApi) {

    private val _weatherLiveData = MutableLiveData<NetworkResult<WeatherResponse>>()
    val weatherLiveData get() = _weatherLiveData

    suspend fun getWeatherData(city_url: String) {
        withContext(Dispatchers.IO) {
            _weatherLiveData.postValue(NetworkResult.Loading())
            val response = weatherApi.getWeatherData(city_url)
            if (response.isSuccessful && response.body() != null) {
                _weatherLiveData.postValue(NetworkResult.Success(response.body()!!))
            } else if (response.errorBody() != null) {
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                _weatherLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
            } else {
                _weatherLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
            }
        }
    }

}
