module com.example.ejf {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ejf to javafx.fxml;
    exports com.example.ejf;
}