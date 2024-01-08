package pl.pw.edu.mini.zpoif.Application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.CheckComboBox;
import pl.pw.edu.mini.zpoif.Api.Api;
import pl.pw.edu.mini.zpoif.Api.CurrencyRate;
import pl.pw.edu.mini.zpoif.Api.Rate;
import pl.pw.edu.mini.zpoif.Api.Table;

import java.net.URL;
import java.net.http.HttpClient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Math.round;

public class HelloController implements Initializable {
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
    private Button generateButton;
    @FXML
    private DatePicker datePicker;
    @FXML
    private DatePicker datePicker1;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private DatePicker endDatePicker1;
    @FXML
    private CheckComboBox<Rate> currencyChoiceBox;
    @FXML
    private LineChart<String, Number> currencyChart;
    private final HttpClient httpClient = HttpClient.newBuilder().build();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HttpClient httpClient = HttpClient.newBuilder().build();
        Api api = new Api();
        CurrencyRate[] data = api.getApiData(httpClient, "http://api.nbp.pl/api/exchangerates/tables/A/", "http://api.nbp.pl/api/exchangerates/tables/B/");
        ObservableList<Table> table2 = FXCollections.observableArrayList();
        getTableData(data, table2);
        setTable(table2);
        setChoiceBoxProperties(data);
    }
    private void getTableData(CurrencyRate[] currencyRates, ObservableList<Table> tableData) {
        for(int num=0; num<currencyRates.length; num++){
            List<Rate> rates = currencyRates[num].getRates();
            for (int i = 0; i < rates.size(); i++) {
                Rate rate2 = currencyRates[num].getRates().get(i);
                double rate = rate2.getMid();
                String name = rate2.getCurrency();
                Date date = currencyRates[num].getEffectiveDate();
                String formattedDate = formatDate(date);
                tableData.add(new Table(name, formattedDate, rate));
            }
        }
    }
    private String formatDate(Date date) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }
    private void setTable(ObservableList<Table> table2) {
        currency.prefWidthProperty().bind(table.widthProperty().multiply(0.33));
        date.prefWidthProperty().bind(table.widthProperty().multiply(0.33));
        rate.prefWidthProperty().bind(table.widthProperty().multiply(0.33));
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