package org.example.lab_11;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ForecastController implements Initializable {

    @FXML
    private Button btnBack;

    @FXML
    private GridPane forecastGrid;

    @FXML
    private Label lblCityName;

    @FXML
    void Goback(ActionEvent event) throws IOException {
        //this is to close the window and go back to the weatherdata window
        Stage stage = (Stage) btnBack.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("weatherdata.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void updateForecast(List<WeatherForecast> forecastList,String cityName) {
        //this is to add the information into the grid

        for (int i = 0; i < forecastList.size(); i++) {
            WeatherForecast forecast = forecastList.get(i);


            lblCityName.setText(cityName);
            Label dateLabel = new Label(forecast.getDate());
            Label minTempLabel = new Label(String.format("%.2f°C", forecast.getMinTemp()));
            Label maxTempLabel = new Label(String.format("%.2f°C", forecast.getMaxTemp()));

            forecastGrid.add(dateLabel, i, 0); // i+1 to skip the header column
            forecastGrid.add(minTempLabel, i, 1);
            forecastGrid.add(maxTempLabel, i, 2);
        }
    }
}
