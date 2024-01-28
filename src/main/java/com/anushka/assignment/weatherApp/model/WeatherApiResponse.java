package com.anushka.assignment.weatherApp.model;

import java.util.ArrayList;

public class WeatherApiResponse {
    public String cod;
    public int message;
    public int cnt;
    public ArrayList<List> list;
    public City city;
}
