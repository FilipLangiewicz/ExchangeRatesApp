package pl.pw.edu.mini.zpoif.Application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.http.HttpClient;

public class HelloController {
    @FXML
    private Label welcomeText;
    private final HttpClient httpClient = HttpClient.newBuilder().build();

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}