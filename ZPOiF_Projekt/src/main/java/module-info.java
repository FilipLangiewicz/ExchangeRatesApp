module com.example.zpoif_projekt {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens pl.pw.edu.mini.zpoif.Application to javafx.fxml;
    exports pl.pw.edu.mini.zpoif.Application;
}