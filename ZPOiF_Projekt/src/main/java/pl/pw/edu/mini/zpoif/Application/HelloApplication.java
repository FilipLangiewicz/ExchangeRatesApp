package pl.pw.edu.mini.zpoif.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.pw.edu.mini.zpoif.Api.Api;

import java.io.IOException;
import java.net.http.HttpClient;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //launch();
        HttpClient httpClient = HttpClient.newBuilder().build();
        Api api = new Api();
        api.getApiData(httpClient);
    }
}