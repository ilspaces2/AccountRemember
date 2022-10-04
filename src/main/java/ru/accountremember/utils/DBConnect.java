package ru.accountremember.utils;

import ru.accountremember.AccountRememberApp;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnect {

    public Connection init(String login, String passUser, String passDB) {
        try (InputStream in = AccountRememberApp.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            Connection cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    login,
                    String.format("%s %s", passUser, passDB)
            );
           AccountRememberApp.LOG.info("DB connect done");
            return cn;
        } catch (Exception e) {
            AccountRememberApp.LOG.error("DB connect error: {}", e.getMessage());
        }
        return null;
    }
}
