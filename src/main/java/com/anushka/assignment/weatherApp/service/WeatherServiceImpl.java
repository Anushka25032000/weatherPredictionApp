package com.anushka.assignment.weatherApp.service;

import com.anushka.assignment.weatherApp.model.List;
import com.anushka.assignment.weatherApp.model.ResponseObj;
import com.anushka.assignment.weatherApp.model.WeatherApiResponse;
import com.anushka.assignment.weatherApp.utils.Messages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Override
    public ArrayList<ResponseObj> getWeatherForCity(String city) {

        WeatherApiResponse weatherApiResponse = fetchWeatherDetails(city);

        ArrayList<ResponseObj> weatherPrediction = PredictThreeDaysWeather(weatherApiResponse);
        return weatherPrediction;
    }


    private WeatherApiResponse fetchWeatherDetails( String city) {

        // Call OpenWeatherMap API to get weather data
        // https://api.openweathermap.org/data/2.5/forecast?q=london&appid=d2929e9483efc82c82c32ee7e02d563e&cnt=10

        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + apiKey + "&cnt=32";

        RestTemplate restTemplate = new RestTemplate();
        WeatherApiResponse apiResponse = restTemplate.getForObject(apiUrl, WeatherApiResponse.class);

        return apiResponse;
    }

    private ArrayList<ResponseObj> PredictThreeDaysWeather(WeatherApiResponse apiResponse) {

        // fetch current date
        String currentTime = Instant.now().toString();
        String currentDate = currentTime.substring(0,10);
        String respDate="";
        ArrayList<ResponseObj> respArray = new ArrayList<ResponseObj>();

        int nextDateIndex=0;


        while(nextDateIndex<apiResponse.list.size())
        {

            respDate = apiResponse.list.get(nextDateIndex).dt_txt.substring(0,10);

            if(respDate.equals(currentDate))
            {
                nextDateIndex++;
            }
            else {
                break;
            }
        }

        Integer numberOfDays = 3;

        for(int j=0;j<numberOfDays;j++)
        {
//            ResponseObj resp = createRespObj();
            String nextdate = getNextDate(currentDate,1);
            Double temp_min=Double.MAX_VALUE;
            Double temp_max=Double.MIN_VALUE;
            Integer id =-1;


            // Standards are defined here  - https://openweathermap.org/weather-conditions#Weather-Condition-Codes-2
            // parameters - https://openweathermap.org/current
            // Group 2xx: Thunderstorm
            // Group 3xx: Drizzle
            // Group 5xx: Rain
            // Group 6xx: Snow
            // Group 7xx: Atmosphere
            // Group 800 : Clear
            // Group 80x : Clouds


            Boolean Rainy = false;
            Boolean Windy = false;
            Boolean Hot = false;
            Boolean ThunderStorm = false;

            for(int k=0;k<8;k++,nextDateIndex++)
            {
                Double tempInKel = apiResponse.list.get(nextDateIndex).main.temp;

                Double tempInCel = getCelciusTemp(tempInKel);


                temp_min = Math.min(temp_min, tempInCel);
                temp_max = Math.max(temp_max,tempInCel);
                id = apiResponse.list.get(nextDateIndex).weather.get(0).id;

                // checking Rain
                if((id>=500&&id<600) || (id>=300&&id<400))
                {
                    Rainy = true;
                }

                // checking Thunderstorms

                if(id>=200&&id<300)
                {
                    ThunderStorm = true;
                }

                // checking Hot
                if(tempInCel>30)
                {
                    Hot = true;
                }

                Double meterPerSecWindSpeed = apiResponse.list.get(nextDateIndex).wind.speed;
                Double milesPerHourWindSpeed = meterPerSecWindSpeed / 2.236936;
//                System.out.println("WIND SPEED IN M per hour  ----- > " + milesPerHourWindSpeed);
                if(milesPerHourWindSpeed > 10)
                {

                    Windy = true;
                }

            }

            ResponseObj resp = new ResponseObj();
            String message = setMessage(ThunderStorm,Rainy,Hot,Windy);
//            System.out.println("temp  = " + temp_min +"-------"+temp_max);
//            System.out.println("res == "+ apiResponse.list.get(nextDateIndex).main.temp_max + " -----"+apiResponse.list.get(nextDateIndex).main.temp_min );

            resp.setDate(currentDate);
            resp.setTemp_max(String.format("%.2f", temp_max ) + '°' +"C");
            resp.setTemp_min(String.format("%.2f",temp_min) + '°' + "C");
            resp.setMessage(message);
            respArray.add(resp);

            currentDate = nextdate;
        }


        return respArray;
    }
//
//    private ResponseObj createRespObj() {
//    }

    private Double getCelciusTemp(Double tempInKel) {

        return tempInKel - 273.15;
    }
    private String setMessage(Boolean thunderStorm,Boolean rainy,Boolean hot, Boolean windy ) {

    String msg="";
    if(thunderStorm)
    {
        msg = String.valueOf(Messages.ThunderStorm.getMsg());
    }
    else if(rainy)
    {
        msg = String.valueOf(Messages.RainyWeather.getMsg());
    }
    else if(hot)
    {
        msg = String.valueOf(Messages.Hot.getMsg());
    }
    else if(windy) {
        msg = String.valueOf(Messages.HighWinds.getMsg());
    }
    else {
        msg = String.valueOf(Messages.Clear.getMsg());
    }

        return msg;
    }

    public static String getNextDate(String  curDate, int d) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date date;
        try {
            date = format.parse(curDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, d);
        return format.format(calendar.getTime());
    }
}
