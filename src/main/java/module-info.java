module com.example.cinamacentermanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires mysql.connector.java;
    requires javafx.base;
    requires com.github.librepdf.openpdf;
    requires java.desktop;

    opens com.example.cinamacentermanagement.controllers to javafx.fxml,java.base;
    exports com.example.cinamacentermanagement;
    exports com.example.cinamacentermanagement.database;
    exports com.example.cinamacentermanagement.controllers;
    opens com.example.cinamacentermanagement.database to java.sql;
    opens com.example.cinamacentermanagement to java.base, javafx.fxml;
}