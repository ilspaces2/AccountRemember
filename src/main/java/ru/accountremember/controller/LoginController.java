package ru.accountremember.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

import ru.accountremember.AccountRememberApp;
import ru.accountremember.utils.DBConnect;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.tools.Backup;

public class LoginController {

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
        try {
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
        } catch (Exception e) {
            AccountRememberApp.LOG.error("Login error: {}", e.getMessage());
        }
    }

    private void openAccountRememberView() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AccountRememberApp.class.getResource("account_remember_view.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(fxmlLoader.load(), 820, 520));
        stage.setTitle("Account remember");
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            try {
                connection.close();
                Backup.execute("~/.db_bup/base.zip", "./", "AccRemBase", true);
            } catch (SQLException e) {
                AccountRememberApp.LOG.error("Exit program : {}", e.getMessage());
            }
        });
    }
}