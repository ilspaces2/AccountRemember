package ru.accountremember.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.accountremember.AccountRememberApp;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnect {

    public static final Logger log = LoggerFactory.getLogger(DBConnect.class.getName());

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
            log.info("DB connect done");
            return cn;
        } catch (Exception e) {
            log.error("DB connect error: {}", e.getMessage());
        }
        return null;
    }
}
