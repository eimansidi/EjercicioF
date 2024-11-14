module com.example.ejf {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ejf to javafx.fxml;
    exports com.example.ejf;
    exports com.example.ejf.model;
    opens com.example.ejf.model to javafx.fxml;
}