package pl.pw.edu.mini.zpoif.Application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.http.HttpClient;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private TableView<String> table;
    private final HttpClient httpClient = HttpClient.newBuilder().build();

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}