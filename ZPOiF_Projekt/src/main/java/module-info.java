module com.example.zpoif_projekt {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.example.zpoif_projekt to javafx.fxml;
    exports com.example.zpoif_projekt;
}