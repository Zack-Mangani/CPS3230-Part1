package com.Wea;

public class WeatherServiceStub extends Weather {
    private double temperature;
    private double precipitation;

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
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
