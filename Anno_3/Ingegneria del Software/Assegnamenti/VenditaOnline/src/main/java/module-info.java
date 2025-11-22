module com.robuschi.venditaonline {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.robuschi to javafx.fxml;
    exports com.robuschi;
}