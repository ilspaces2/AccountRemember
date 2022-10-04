package ru.accountremember.repository;

import ru.accountremember.AccountRememberApp;
import ru.accountremember.model.Site;
import ru.accountremember.utils.PasswordCoderDecoder;

import javax.crypto.SecretKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SiteDBStore {

    private final PasswordCoderDecoder pcd = new PasswordCoderDecoder();
    private final Connection connection;

    public SiteDBStore(Connection connection) {
        this.connection = connection;
    }

    public Site add(Site site) {
        try (PreparedStatement ps = connection.prepareStatement(
                "insert into site (name, login, password) values (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            SecretKey secretKey = pcd.getKey(site.getName(), site.getLogin());
            ps.setString(1, site.getName());
            ps.setString(2, site.getLogin());
            ps.setString(3, pcd.encrypt(site.getPassword(), secretKey));
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    site.setId(generatedKeys.getInt(1));
                }
            }
            AccountRememberApp.LOG.info("Site added: {}", site.getName());
        } catch (Exception e) {
            AccountRememberApp.LOG.error("Add error: {}", e.getMessage());
        }
        return site;
    }

    public List<Site> findAll() {
        List<Site> sites = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("select * from site")) {
            try (ResultSet resultSet = ps.executeQuery()) {
                Site site;
                while ((site = findItem(resultSet)) != null) {
                    sites.add(site);
                }
            }
        } catch (Exception e) {
            AccountRememberApp.LOG.error("FindAll error: {}", e.getMessage());
        }
        return sites;
    }

    public List<Site> findByName(String name) {
        List<Site> sites = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("select * from site where name=?")) {
            ps.setString(1, name);
            try (ResultSet resultSet = ps.executeQuery()) {
                Site site;
                while ((site = findItem(resultSet)) != null) {
                    sites.add(site);
                }
            }
        } catch (Exception e) {
            AccountRememberApp.LOG.error("findByName error: {}", e.getMessage());
        }
        return sites;
    }


    public void deleteAll() {
        try (PreparedStatement ps = connection.prepareStatement("delete from site")) {
            if (ps.executeUpdate() > 0) {
                AccountRememberApp.LOG.info("Base is empty");
            }
        } catch (Exception e) {
            AccountRememberApp.LOG.error("delete all error: {}", e.getMessage());
        }
    }

    public void deleteById(int id) {
        try (PreparedStatement ps = connection.prepareStatement("delete from site where id=?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                AccountRememberApp.LOG.info("Site deleted: {}", id);
            }
        } catch (Exception e) {
            AccountRememberApp.LOG.error("delete by id error: {}", e.getMessage());
        }
    }

    private Site findItem(ResultSet resultSet) throws Exception {
        Site site = null;
        if (resultSet.next()) {
            site = new Site(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    pcd.decrypt(resultSet.getString(4), pcd.getKey(
                            resultSet.getString(2),
                            resultSet.getString(3)))
            );
        }
        return site;
    }
}
