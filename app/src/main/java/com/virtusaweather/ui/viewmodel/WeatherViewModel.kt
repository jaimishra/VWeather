package com.photon.demo.weatherApp.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtusaweather.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {

    val weatherLiveData get() = weatherRepository.weatherLiveData

    fun getWeatherData(city_url: String) {
        viewModelScope.launch {
            weatherRepository.getWeatherData(city_url)
        }
    }

}
