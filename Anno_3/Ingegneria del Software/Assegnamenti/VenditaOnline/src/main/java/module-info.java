module com.robuschi.venditaonline {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.robuschi to javafx.fxml;
    exports com.robuschi;
}