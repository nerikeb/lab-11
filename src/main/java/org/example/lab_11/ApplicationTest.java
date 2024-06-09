package org.example.lab_11;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class ApplicationTest extends Application {

    private WeatherDataController weatherDataController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("weatherdata.fxml"));
        Parent root = loader.load();
        weatherDataController = loader.getController();  // Get the controller instance

        primaryStage.setTitle("Weather App");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Call the stop method in HelloController to shutdown the scheduler
        if (weatherDataController != null) {
            weatherDataController.stop();
        }

        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}