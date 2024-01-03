package pl.pw.edu.mini.zpoif.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.pw.edu.mini.zpoif.Api.Api;
import pl.pw.edu.mini.zpoif.Api.CurrencyRate;


import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Arrays;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/pl/pw/edu/mini/zpoif/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        HttpClient httpClient = HttpClient.newBuilder().build();
        Api api = new Api();
        CurrencyRate[] data = api.getApiData(httpClient);
        System.out.println(Arrays.stream(data).toArray().toString());
    }
}