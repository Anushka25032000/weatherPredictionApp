package com.anushka.assignment.weatherApp.controller;


import com.anushka.assignment.weatherApp.model.ResponseObj;
import com.anushka.assignment.weatherApp.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Controller
public class HomeController {

    private final WeatherService weatherService;

    public HomeController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public String home() {
        return "form.html";
    }


    @GetMapping("/error")
    public String Error() {
        return "ERROR !!!!";
    }

    @GetMapping("/form")
    public String showForm() {
        try {
            return "form.html";
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();

            // Forward to the error page
            ModelAndView errorModelAndView = new ModelAndView("error");
            errorModelAndView.addObject("errorMessage", "An error occurred");
            return "Error.html";
        }
    }

    @PostMapping("/submitForm")
    public String submitForm(@RequestParam String inputValue, Model model) {

        String city =  inputValue.substring(0, 1).toUpperCase() + inputValue.substring(1).toLowerCase();

        try {
            ArrayList<ResponseObj> ans = weatherService.getWeatherForCity(inputValue);

            model.addAttribute("city", city);
            model.addAttribute("day1", ans.get(0));
            model.addAttribute("day2", ans.get(1));
            model.addAttribute("day3", ans.get(1));

            return "result.html";
        }
        catch (Exception e)
        {
            return "Error.html";
        }


    }

//    @GetMapping("/resultPage")
//    public String showResultPage(Model model) {
//        ArrayList<ResponseObj> ans = weatherService.getWeatherForCity("delhi");
//
//        System.out.println("Model --> " + model.getAttribute("ans"));
//        return "result.html";
////        model.addAttribute("message", "Hello, Spring Boot!");
//
////        return "result.html"; // The name of the result page HTML template
//    }

}