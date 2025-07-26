open module com.example.lab1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.graphics;
    requires javafx.base;
    requires com.google.gson;

    requires org.hibernate.orm.core;
    requires java.naming;
    requires java.sql;
    requires java.persistence;

    exports com.example.lab1;
    exports com.example.lab1.entities;
    exports com.example.lab1.model;
    exports com.example.lab1.database;
}