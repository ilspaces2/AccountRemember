package ru.accountremember.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.accountremember.AccountRememberApp;
import ru.accountremember.utils.DBConnect;

import java.sql.Connection;

import org.h2.tools.Backup;

public class LoginController {

    public static final Logger log = LoggerFactory.getLogger(LoginController.class.getName());

    public static Connection connection;
    @FXML
    private TextField login;
    @FXML
    private Label returnMessage;

    @FXML
    private PasswordField userPassword;

    @FXML
    private PasswordField dataBasePassword;

    @FXML
    private void loginButton() {
        connection = new DBConnect().init(login.getText(), userPassword.getText(), dataBasePassword.getText());
        if (connection != null) {
            ((Stage) returnMessage.getScene().getWindow()).close();
            openAccountRememberView();
        } else {
            login.clear();
            userPassword.clear();
            dataBasePassword.clear();
            returnMessage.setText("Login or passwords invalid");
        }
    }

    private void openAccountRememberView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AccountRememberApp.class.getResource("account_remember_view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 820, 520));
            stage.setTitle("Account remember");
            stage.show();
            closeAndBackup(stage);
        } catch (Exception e) {
            log.error("OpenAccountRememberView : {}", e.getMessage());
        }
    }

    private void closeAndBackup(Stage stage) {
        stage.setOnCloseRequest(windowEvent -> {
            try {
                connection.close();
                Backup.execute("~/.dataDB/DB.zip", "./", "DB", true);
            } catch (Exception e) {
                log.error("CloseAndBackup program : {}", e.getMessage());
            }
        });
    }
}
