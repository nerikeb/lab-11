package org.example.lab_11;

public class CurrentWeatherDisplay implements Display,Observer{
    private  double temperature;
    private  float humidity;
    private float pressure;

    @Override
    public void display() {

        System.out.println("CURRENT WEATHER DATA");
        System.out.println("Current temp: " + temperature);
        System.out.println("Current hum: " + humidity);
        System.out.println("Current press: " + pressure);
    }

    @Override
    public void update(double temperature, float humidity, float pressure) {
        this.temperature=temperature;
        this.humidity=humidity;
        this.pressure=pressure;
    }
}
