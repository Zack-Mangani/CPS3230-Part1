package com.Wea;

public class Weather implements WeatherService {
    private double temperature;
    private double precipitation;

    public Weather() {
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    public double getPrecipitation() {
        return precipitation;
    }



}
