package com.anushka.assignment.weatherApp.controller;


import com.anushka.assignment.weatherApp.model.ResponseObj;
import com.anushka.assignment.weatherApp.model.WeatherApiResponse;
import com.anushka.assignment.weatherApp.service.WeatherService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Controller
@RestController
public class WeatherAppController {

    private final WeatherService weatherService;

    public WeatherAppController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }


    @Cacheable("Cache")
    @GetMapping("/weather/{city}")
    public String getWeather(@PathVariable@RequestBody String city, Model model) {

        try{
            ArrayList<ResponseObj> ans = weatherService.getWeatherForCity(city);
            model.addAttribute("res",ans);

            return "redirect:/resultPage";
        }
        catch (Exception e) {
            ArrayList<ResponseObj> ans = null;
            model.addAttribute("res",ans);
            return "redirect:/resultPage";
        }


    }

    @GetMapping("/getAppData")
    public String getAppData()
    {
        String uri = "https://api.openweathermap.org/data/2.5/forecast?q=london&appid=d2929e9483efc82c82c32ee7e02d563e&cnt=10";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

}
