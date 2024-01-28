package com.anushka.assignment.weatherApp.utils;

public enum  Messages{
    RainyWeather("Raining, carry an umbrella"),
    ThunderStorm("Don’t step out! A Storm is brewing!"),
    HighWinds("It’s too windy, watch out!"),
    Hot("Hot outside, use sunscreen lotion"),
    Clear("Clear sky ");


    private String msg;

    Messages(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }



}
