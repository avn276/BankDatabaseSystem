module com.example.bankdatabase {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.bankdatabase to javafx.fxml;
    exports com.example.bankdatabase;
}