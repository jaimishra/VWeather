package com.virtusaweather.ui;

import static com.virtusaweather.constants.Constants.PERMISSION_CODE;
import static com.virtusaweather.utils.AppUtils.hideKeyboard;
import static com.virtusaweather.utils.CheckConnectivity.isInternetConnected;
import static com.virtusaweather.utils.wetherutils.CityFind.getCityNameUsingNetwork;
import static com.virtusaweather.utils.wetherutils.CityFind.setLongitudeLatitude;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.photon.demo.weatherApp.presenter.viewmodel.WeatherViewModel;
import com.virtusaweather.R;
import com.virtusaweather.databinding.FragmentMainBinding;
import com.virtusaweather.model.NetworkResult;
import com.virtusaweather.model.weather.WeatherResponse;
import com.virtusaweather.utils.AppUtils;
import com.virtusaweather.utils.wetherutils.LocationCoordinate;
import com.virtusaweather.utils.wetherutils.URLUtils;
import com.virtusaweather.utils.wetherutils.UpdateUI;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends Fragment {
    private WeatherViewModel weatherViewModel;
    private FragmentMainBinding binding;
    private String city;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listeners();
        bindObserver();
        checkConnection();
    }

    private void updateUI(WeatherResponse data) {
        int condition = data.getWeather().get(0).getId();
        String description = data.getWeather().get(0).getDescription();
        String temperature = String.valueOf(Math.round(data.getMain().getTemp()));
        String min_temperature = String.valueOf(Math.round(data.getMain().getTemp_min()));
        String max_temperature = String.valueOf(Math.round(data.getMain().getTemp_max()));
        String pressure = String.valueOf(data.getMain().getPressure());
        String windSpeed = String.valueOf(data.getWind().getSpeed());
        String humidity = String.valueOf(data.getMain().getHumidity());

        binding.layout.nameTv.setText(city);
        binding.layout.tempTv.setText(getResources().getString(R.string.temperature_in_degrees, temperature));
        binding.layout.minTempTv.setText(getResources().getString(R.string.temperature_min, min_temperature));
        binding.layout.maxTempTv.setText(getResources().getString(R.string.temperature_max, max_temperature));
        binding.layout.conditionDescTv.setText(description);
        binding.layout.pressureTv.setText(getResources().getString(R.string.weather_pressure, pressure));
        binding.layout.windTv.setText(getResources().getString(R.string.wind_speed, windSpeed));
        binding.layout.humidityTv.setText(getResources().getString(R.string.weather_humidity, humidity));

        binding.layout.conditionIv.setImageResource(
                getResources().getIdentifier(
                        UpdateUI.getIconID(condition),
                        "drawable",
                        requireActivity().getPackageName()
                ));
    }

    private void bindObserver() {
        final Observer<NetworkResult<WeatherResponse>> observer = result -> {
            if (result instanceof NetworkResult.Success) {
                hideProgressBar();
                if (result.getData() != null) {
                    showWeatherInfoViews();
                    city = result.getData().getName();
                    if (getActivity() != null) AppUtils.storeLastCityInPrefs(getActivity(), city);
                    LocationCoordinate.lat = String.valueOf(result.getData().getCoord().getLat());
                    LocationCoordinate.lon = String.valueOf(result.getData().getCoord().getLon());
                    updateUI(result.getData());
                }
                // After the successfully city search making the cityEt(editText) is Empty.
                binding.layout.cityEt.setText("");
            } else if (result instanceof NetworkResult.Error) {
                hideProgressBar();
                hideWeatherInfoViews();
                 /* if city data not found using location service
                       check in shared prefs if city is available and if yes get data.
                       just a hack to show the data as we will get the location from
                       india only and we are showing only US cities*/
                if (getActivity() != null && AppUtils.getLastCityName(getActivity()) != null) {
                    searchCity(AppUtils.getLastCityName(getActivity()));
                } else {
                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (result instanceof NetworkResult.Loading) {
                showProgressBar();
            }
        };
        weatherViewModel.getWeatherLiveData().observe(getViewLifecycleOwner(), observer);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void listeners() {
        binding.layout.mainLayout.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard(view);
            return false;
        });
        binding.layout.searchBarIv.setOnClickListener(view -> searchCity(binding.layout.cityEt.getText().toString()));

        binding.layout.searchBarIv.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard(view);
            return false;
        });

        binding.layout.cityEt.setOnFocusChangeListener((view, bool) -> {
            if (!bool) {
                hideKeyboard(view);
            }
        });

        binding.layout.cityEt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchCity(binding.layout.cityEt.getText().toString());
                    hideKeyboard(view);
                    return true;
                }
                return false;
            }
        });
    }

    private void searchCity(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter the city name", Toast.LENGTH_SHORT).show();
        } else {
            getWeatherData(cityName);
        }
    }

    private void getWeatherData(String cityName) {
        showProgressBar();
        URLUtils.setCity_url(cityName);
        weatherViewModel.getWeatherData(URLUtils.getCity_url());
    }

    private void getDataUsingNetwork() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(requireActivity());
        //check permission
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        } else {
            client.getLastLocation().addOnSuccessListener(location -> {
                setLongitudeLatitude(location);
                city = getCityNameUsingNetwork(getActivity(), location);
                // if city available using location service
                if (city != null && !city.equals("")) {
                    searchCity(city);
                } else {
                    /* if city not available using location service
                       check in shared prefs if city is available and if yes get data */
                    if (getActivity() != null && AppUtils.getLastCityName(getActivity()) != null) {
                        searchCity(AppUtils.getLastCityName(getActivity()));
                    }
                }
            });
        }
    }

    private void checkConnection() {
        if (!isInternetConnected(requireActivity())) {
            hideMainLayout();
            Toast.makeText(getActivity(), "Please check your internet connection",
                    Toast.LENGTH_SHORT).show();
        } else {
            hideProgressBar();
            showMainLayout();
            getDataUsingNetwork();
        }
    }

    private void hideProgressBar() {
        binding.progress.setVisibility(View.GONE);

    }

    private void showProgressBar() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void hideWeatherInfoViews() {
        binding.layout.weatherInfoGroup.setVisibility(View.GONE);
    }

    private void showWeatherInfoViews() {
        binding.layout.weatherInfoGroup.setVisibility(View.VISIBLE);
    }

    private void showMainLayout() {
        binding.layout.mainLayout.setVisibility(View.VISIBLE);
    }

    private void hideMainLayout() {
        binding.layout.mainLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}