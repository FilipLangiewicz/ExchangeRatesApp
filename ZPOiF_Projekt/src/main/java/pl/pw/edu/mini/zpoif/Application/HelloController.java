package pl.pw.edu.mini.zpoif.Application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;
import org.controlsfx.control.CheckComboBox;
import pl.pw.edu.mini.zpoif.Api.Api;
import pl.pw.edu.mini.zpoif.Api.CurrencyRate;
import pl.pw.edu.mini.zpoif.Api.Rate;
import pl.pw.edu.mini.zpoif.Api.Table;
import pl.pw.edu.mini.zpoif.plotData.PlotData;
import pl.pw.edu.mini.zpoif.plotData.RatePlot;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Math.round;

public class HelloController implements Initializable {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final double wartoscDomyslnaMonet = 100;
    private final double wartoscDomyslnaChleb = 4;
    private final double wartoscDomyslnaBasen = 15;
    private final double wartoscDomyslnaKetchup = 5;
    private final double wartoscDomyslnaWalutJajko = 1;
    private final double wartoscDomyslnaWalutPiwo = 3;
    @FXML
    private Label welcomeText;
    @FXML
    private TableView<Table> table;
    @FXML
    private TableColumn<Table, String> currency;
    @FXML
    private TableColumn<Table, Date> date;
    @FXML
    private TableColumn<Table, Double> rate;

    //private CheckComboBox<Rate> currencyChoiceBox;
    @FXML
    private Button buttonPorownaj;
    @FXML
    private DatePicker endDateButton;
    @FXML
    private DatePicker startDateButton;
    @FXML
    private CheckComboBox<Rate> currencyChoiceBox;
    @FXML
    private LineChart<String, Number> wykresPorownanie;
    @FXML
    private ChoiceBox<Rate> ileCzegoCheckBox;
    @FXML
    private BarChart<String, Number> ileCzegoPlot;
    @FXML
    private Button ileCzegoButton;

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HttpClient httpClient = HttpClient.newBuilder().build();
        Api api = new Api();
        CurrencyRate[] data = api.getApiData(httpClient, "http://api.nbp.pl/api/exchangerates/tables/A/", "http://api.nbp.pl/api/exchangerates/tables/B/");

        //////////////// tworzenie tabelki //////////
        ObservableList<Table> table2 = FXCollections.observableArrayList();
        getTableData(data, table2);
        setTable(table2);


        //////////////// tworzenie porownania walut //////////

        setChoiceBoxProperties(data);
        buttonPorownaj.setOnAction(actionEvent -> {

            ObservableList<Rate> rates = currencyChoiceBox.getCheckModel().getCheckedItems();
            //if (!validateRates(rates)) return;
            LocalDate endDate = endDateButton.getValue();
            LocalDate startDate = startDateButton.getValue();
            //if (!validateDates(startDate, endDate)) return;
            wykresPorownanie.getData().clear();
            wykresPorownanie.setTitle("Kursy wybranych walut między " + startDate.format(dateTimeFormatter) + " a "
                    + endDate.format(dateTimeFormatter));
            for (Rate selectedRate : rates) {
                boolean isARate = data[0].getRates().contains(selectedRate);
                PlotData plotData = getPlotData(startDate, endDate, selectedRate, isARate);
                if (plotData == null) return;
                XYChart.Series<String, Number> series = processPlotData(plotData);
                wykresPorownanie.getData().add(series);
            }
            wykresPorownanie.setVisible(true);
            buttonPorownaj.setText("Porównaj waluty");
        });


        //////////////// tworzenie ileCzego //////////
        //ileCzego ileCzegoButton ileCzegoCheckBox ileCzegoPlot
        ileCzegoButton.setOnAction(actionEvent -> {
        });
    }



    private XYChart.Series<String, Number> processPlotData(PlotData chartData) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(String.format("%s (%s)", chartData.getCurrency(), chartData.getCode()));
        for (RatePlot rate : chartData.getRates()) {
            series.getData().add(new XYChart.Data<>(rate.getEffectiveDate(), rate.getMid()));
        }
        series.getData().sort(Comparator.comparing(XYChart.Data::getXValue));
        return series;
    }

    private PlotData getPlotData(LocalDate startDate, LocalDate endDate, Rate selectedRate, boolean isARate) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Błąd");
        String link = String.format("http://api.nbp.pl/api/exchangerates/rates/%s/%s/%s/%s/",
                isARate ? "A" : "B",
                selectedRate.getCode(),
                startDate.format(dateTimeFormatter),
                endDate.format(dateTimeFormatter));

        HttpRequest chartRequest = HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .uri(URI.create(link))
                .build();
        HttpResponse<String> chartResponse;
        buttonPorownaj.setText("Tworzenie wykresu...");
        try {
            chartResponse = httpClient.send(chartRequest, HttpResponse.BodyHandlers.ofString());
        } catch (ConnectException ex) {
            alert.setHeaderText("Brak internetu");
            alert.setContentText("Sprawdź połączenie internetowe");
            alert.showAndWait();
            buttonPorownaj.setText("Porównaj waluty");
            return null;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper chartMapper = new ObjectMapper();
        PlotData plotData;
        try {
            plotData = chartMapper.readValue(chartResponse.body(), PlotData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return plotData;
    }

    private void getTableData(CurrencyRate[] currencyRates, ObservableList<Table> tableData) {
        for(int num=0; num<currencyRates.length; num++){
            List<Rate> rates = currencyRates[num].getRates();
            for (int i = 0; i < rates.size(); i++) {
                Rate rate2 = currencyRates[num].getRates().get(i);
                double rate = round(rate2.getMid());
                String name = rate2.getCurrency();
                Date date = currencyRates[num].getEffectiveDate();
                String formattedDate = formatDate(date);
                tableData.add(new Table(name, formattedDate, rate));
            }
        }
    }

    private double round(double d) {
        return Math.round(d * 10000.0) / 10000.0;
    }

    private String formatDate(Date date) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }

    private void setTable(ObservableList<Table> table2) {
        currency.prefWidthProperty().bind(table.widthProperty().multiply(0.334));
        date.prefWidthProperty().bind(table.widthProperty().multiply(0.333));
        rate.prefWidthProperty().bind(table.widthProperty().multiply(0.333));
        currency.setCellValueFactory(new PropertyValueFactory<>("currency"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        rate.setCellValueFactory(new PropertyValueFactory<>("rate"));
        table.setItems(table2);
    }

    private void setChoiceBoxProperties(CurrencyRate[] currencyRates) {
        if (currencyRates != null) {
            currencyChoiceBox.getItems().addAll(FXCollections.observableArrayList(currencyRates[0].getRates()));
            currencyChoiceBox.getItems().addAll(FXCollections.observableArrayList(currencyRates[1].getRates()));
            Rate defaultRate = new Rate();
            defaultRate.setCode("PLN");
            defaultRate.setCurrency("złoty polski");
            defaultRate.setMid(1.00);
            currencyChoiceBox.getItems().add(defaultRate);
        }

    }

}