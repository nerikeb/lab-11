module org.example.lab_11 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires java.desktop;


    opens org.example.lab_11 to javafx.fxml;
    exports org.example.lab_11;
}