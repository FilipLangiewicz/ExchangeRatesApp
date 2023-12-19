package pl.pw.edu.mini.zpoif.Api;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Api {
    private final String linkTableA = "http://api.nbp.pl/api/exchangerates/tables/A/";
    private final String linkTableB = "http://api.nbp.pl/api/exchangerates/tables/B/";
    private final String linkTableC = "http://api.nbp.pl/api/exchangerates/tables/C/";


    public CurrencyRate[] getApiData(HttpClient httpClient) {
        CurrencyRate[] tableA = importTableData(linkTableA, httpClient);
        CurrencyRate[] tableB = importTableData(linkTableB, httpClient);
        CurrencyRate[] tableC = importTableData(linkTableC, httpClient);


        return mergeTables(tableA, tableB, tableC);
    }

    private CurrencyRate[] mergeTables(CurrencyRate[] tableA, CurrencyRate[] tableB, CurrencyRate[] tableC) {
        return Stream.concat(
                Stream.concat(Arrays.stream(tableA), Arrays.stream(tableB)),
                Arrays.stream(tableC)
        ).toArray(CurrencyRate[]::new);
    }

    private CurrencyRate[] importTableData(String url, HttpClient httpClient) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            return readResponseAndMap(response);
        } catch (ConnectException ex) {
            Alert noConnectionAlert = new Alert(Alert.AlertType.ERROR);
            noConnectionAlert.setHeaderText("Nie można połączyć się z serwerem.");
            noConnectionAlert.setContentText("Sprawdź połączenie internetowe i uruchom ponownie aplikację.");
            noConnectionAlert.showAndWait();
            Platform.exit();
            return null;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private CurrencyRate[] readResponseAndMap(HttpResponse<String> response){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(response.body());
            System.out.println(CurrencyRate.class);
            return objectMapper.readValue(response.body(), CurrencyRate[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



}
