module lk.ijse.sams {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    opens lk.ijse.sams to javafx.fxml;
    opens lk.ijse.sams.controller to javafx.fxml;
    opens lk.ijse.sams.model to javafx.fxml;

    exports lk.ijse.sams;
}
