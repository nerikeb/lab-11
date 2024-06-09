package org.example.lab_11;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WeatherData implements Subject{
    double temperature;
    float humidity;
    float pressure;
    String date;
    private long sunrise;
    private long sunset;
    String apiKey = "";
    String city = "";

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer o) {
        if (! observers.contains(o ))observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        if(observers.contains(o)) observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for(Observer o : observers)
            o.update(temperature,humidity,pressure);
    }

    public void updateTimeLabel(Label lblTime) {
        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC); // Get current UTC time
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(Integer.parseInt(date));
        LocalDateTime localTime = currentTime.plusSeconds(zoneOffset.getTotalSeconds()); // Convert to local time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = localTime.format(formatter);

        Platform.runLater(() -> lblTime.setText("Current Time: " + formattedTime));
    }

    public void measurementChanged() throws URISyntaxException, IOException, InterruptedException {
        //in this method we get the url to get the required information and parse it into json.
        //with this we can get the correct attributes and add it into the class

        String URL = ("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "units=metric");
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder(new URI(URL)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

        JsonObject main = jsonObject.getAsJsonObject("main");
        temperature = main.get("temp").getAsDouble();
        pressure = main.get("pressure").getAsInt();
        humidity = main.get("humidity").getAsInt();
        date = jsonObject.get("timezone").getAsString(); // Get timezone offset in seconds


        //attributes needed for the picture
        JsonObject sys = jsonObject.getAsJsonObject("sys");
        sunrise = sys.get("sunrise").getAsLong();
        sunset = sys.get("sunset").getAsLong();

        notifyObservers();
    }

    public String getDate() {return date;}

    public void setDate(String date) {this.date = date;}

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Observer> getObservers() {
        return observers;
    }

    public void setObservers(List<Observer> observers) {
        this.observers = observers;
    }

    public long getSunrise() {return sunrise;}

    public void setSunrise(long sunrise) {this.sunrise = sunrise;}

    public long getSunset() {return sunset;}

    public void setSunset(long sunset) {this.sunset = sunset;}

    @Override
    public String toString() {
        return "WeatherData{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", observers=" + observers +
                '}';
    }

    public List<WeatherForecast> fetch5DayForecast() throws URISyntaxException, IOException, InterruptedException {
        //this part is to gain the information for the forecast
        //it will be in an array list that has the data, min and max temps for the grid in the forecast window

        String URL = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + apiKey + "&units=metric";
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder(new URI(URL)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray list = jsonObject.getAsJsonArray("list");

        List<WeatherForecast> forecastList = new ArrayList<>();
        for (int i = 0; i < list.size(); i += 8) { // 8 data points per day (every 3 hours)
            JsonObject dayData = list.get(i).getAsJsonObject();
            String date = dayData.get("dt_txt").getAsString().split(" ")[0];
            double minTemp = dayData.getAsJsonObject("main").get("temp_min").getAsDouble();
            double maxTemp = dayData.getAsJsonObject("main").get("temp_max").getAsDouble();


            forecastList.add(new WeatherForecast(date, minTemp, maxTemp));
        }
        return forecastList;
    }
}
