module ru.accountremember {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires org.slf4j;
    requires com.h2database;

    opens ru.accountremember to javafx.fxml;
    opens ru.accountremember.controller to javafx.fxml;
    opens ru.accountremember.model;
    opens ru.accountremember.repository;
    opens db;

    exports ru.accountremember;
}