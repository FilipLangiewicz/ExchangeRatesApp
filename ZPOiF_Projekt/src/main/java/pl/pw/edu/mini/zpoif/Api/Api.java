package pl.pw.edu.mini.zpoif.Api;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
//import com.fasterxml.jackson.databind.ObjectMapper;

public class Api {
    private String linkTableA = "http://api.nbp.pl/api/exchangerates/tables/A/";
    private String linkTableB = "http://api.nbp.pl/api/exchangerates/tables/B/";
    private String linkTableC = "http://api.nbp.pl/api/exchangerates/tables/C/";


    private CurrencyRate[] getApiData(HttpClient httpClient) {
        CurrencyRate[] tableA = importTableData(linkTableA, httpClient);
        CurrencyRate[] tableB = importTableData(linkTableB, httpClient);
        CurrencyRate[] tableC = importTableData(linkTableC, httpClient);

        CurrencyRate[] currencyRates = mergeTables(tableA, tableB, tableC);


        return currencyRates;
    }

    private CurrencyRate[] mergeTables(CurrencyRate[] tableA, CurrencyRate[] tableB, CurrencyRate[] tableC) {
        CurrencyRate[] mergedTable = null;
        return mergedTable;
    }

    private CurrencyRate[] importTableData(String url, HttpClient httpClient) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            //CurrencyRate[] currencyRates = readResponseAndMap(response);
            //return currencyRates;

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
        return null;
    }


    private CurrencyRate[] readResponseAndMap(HttpResponse<String> response){
        //CurrencyRate[] currencyRates =
        return null;
    }



}
