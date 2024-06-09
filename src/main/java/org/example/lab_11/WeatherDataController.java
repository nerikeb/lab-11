package org.example.lab_11;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherDataController implements Initializable {

    @FXML
    private Button btnCitySearch;

    @FXML
    private ImageView imgTime;

    @FXML
    private Label lblCity;

    @FXML
    private Label lblHum;

    @FXML
    private Label lblPress;

    @FXML
    private Label lblTemp;

    @FXML
    private Label lblTime;

    @FXML
    private TextField txtCity;

    private ScheduledExecutorService scheduler;
    private final String dayImage = "/01d.png";
    private final String nightImage = "/01n.png";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    public void viewForecast(javafx.event.ActionEvent event) {
        //this is to essentially open the new forecast window and to load the data for it

        try {
            // Close the current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            WeatherData weatherData = new WeatherData();
            weatherData.setCity(txtCity.getText());

            //get the data from forecast in weatherdata
            List<WeatherForecast> forecastList = weatherData.fetch5DayForecast();

            //load the new window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("forecast.fxml"));
            AnchorPane root = loader.load();

            ForecastController forecastController = loader.getController();
            forecastController.updateForecast(forecastList,txtCity.getText());

            Stage stage = new Stage();
            stage.setTitle("5-Day Weather Forecast");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> lblCity.setText("Error occurred while loading forecast"));
        }
    }

    public void btnSearchCity(javafx.event.ActionEvent actionEvent) throws URISyntaxException, IOException, InterruptedException {
        //this is to get the cityname from the user and to thread the information


        String cityText = txtCity.getText();

        //make sure there is an actual city name
        if (cityText == null || cityText.isEmpty()) {
            lblCity.setText("Please enter a city name");
            return;
        }


        lblCity.setText(cityText);

        // Stop any existing scheduled tasks
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }

        // Start a new scheduled task
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> fetchWeatherData(cityText), 0, 1, TimeUnit.MINUTES);
    }

    private void fetchWeatherData(String cityText) {
        //this is to get all the requiring information to be displayed

        try {
            WeatherData weatherData = new WeatherData();
            weatherData.setCity(cityText);

            weatherData.measurementChanged();

            // Update time label
            weatherData.updateTimeLabel(lblTime);

            weatherData.measurementChanged();//get the data

            // Update the UI on the JavaFX Application Thread
            Platform.runLater(() -> {
                lblTemp.setText(String.format("%.2f°C", weatherData.getTemperature()));
                lblHum.setText(String.format("%.2f%%", weatherData.getHumidity()));
                lblPress.setText(String.format("%.2f hPa", weatherData.getPressure()));

                //this is for the picture to update. the picture changes from day to night
                long currentTime = Instant.now().getEpochSecond();
                long sunrise = weatherData.getSunrise();
                long sunset = weatherData.getSunset();

                String imagePath;
                if (currentTime >= sunrise && currentTime <= sunset) {
                    imagePath = dayImage;
                } else {
                    imagePath = nightImage;
                }

                Image image = new Image(getClass().getResourceAsStream(imagePath));
                imgTime.setImage(image);
            });

        } catch (URISyntaxException | IOException | InterruptedException e) {
            Platform.runLater(() -> lblCity.setText("Error fetching data"));
            e.printStackTrace();
        }
    }

    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

}

