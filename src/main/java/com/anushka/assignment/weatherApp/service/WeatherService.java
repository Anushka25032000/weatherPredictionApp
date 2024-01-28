package com.anushka.assignment.weatherApp.service;

import com.anushka.assignment.weatherApp.model.ResponseObj;

import java.util.ArrayList;

public interface WeatherService {
    ArrayList<ResponseObj> getWeatherForCity(String city);

}
