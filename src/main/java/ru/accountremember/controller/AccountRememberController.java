package ru.accountremember.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Clipboard;

import ru.accountremember.model.Site;
import ru.accountremember.repository.SiteDBStore;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AccountRememberController implements Initializable {

    private final ObservableList<Site> sitesMemStore = FXCollections.observableArrayList();

    private final SiteDBStore sitesDBStore = new SiteDBStore(LoginController.connection);
    @FXML
    private TableView<Site> tableSites;

    @FXML
    private TableColumn<Site, Integer> idColumn;

    @FXML
    private TableColumn<Site, String> siteColumn;

    @FXML
    private TableColumn<Site, String> loginColumn;

    @FXML
    private TableColumn<Site, String> passwordColumn;

    @FXML
    private TextField siteId;

    @FXML
    private TextField siteName;

    @FXML
    private TextField siteLogin;

    @FXML
    private TextField sitePassword;

    @FXML
    private void findAll() {
        sitesMemStore.clear();
        sitesMemStore.addAll(sitesDBStore.findAll());
        tableSites.setItems(sitesMemStore);
    }

    @FXML
    private void findByName() {
        if (!siteName.getText().isBlank()) {
            sitesMemStore.clear();
            sitesMemStore.addAll(sitesDBStore.findByName(siteName.getText()));
            tableSites.setItems(sitesMemStore);
            clearFields();
        }
    }

    @FXML
    private void addSite() {
        if (!siteName.getText().isBlank() && !siteLogin.getText().isBlank()
                && !sitePassword.getText().isBlank()) {
            sitesMemStore.add(
                    sitesDBStore.add(
                            new Site(
                                    siteName.getText(),
                                    siteLogin.getText(),
                                    sitePassword.getText())));
            clearFields();
        }
    }

    @FXML
    private void deleteById() {
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                "This operation delete site. Are you sure?",
                ButtonType.OK, ButtonType.CANCEL);
        if (!siteId.getText().isBlank() && siteId.getText().matches("\\d*")
                && alert.showAndWait().get() == ButtonType.OK) {
            sitesDBStore.deleteById(Integer.parseInt(siteId.getText()));
            sitesMemStore.remove(new Site(Integer.parseInt(siteId.getText())));
        }
        clearFields();
    }

    @FXML
    private void deleteAll() {
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                "This operation delete all base. Are you sure?",
                ButtonType.OK, ButtonType.CANCEL);
        if (!sitesMemStore.isEmpty() && alert.showAndWait().get() == ButtonType.OK) {
            sitesDBStore.deleteAll();
            sitesMemStore.clear();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableCell();
        copyDataFromTable(tableSites);
    }

    private void clearFields() {
        siteId.clear();
        siteName.clear();
        siteLogin.clear();
        sitePassword.clear();
    }

    private void initTableCell() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        siteColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
    }

    private void copyDataFromTable(TableView<Site> table) {
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        MenuItem item = new MenuItem("Copy");
        item.setOnAction(event -> {
            ObservableList<TablePosition> posList = table.getSelectionModel().getSelectedCells();
            int old_r = -1;
            StringBuilder clipboardString = new StringBuilder();
            for (TablePosition p : posList) {
                int r = p.getRow();
                int c = p.getColumn();
                Object cell = table.getColumns().get(c).getCellData(r);
                if (cell == null)
                    cell = "";
                if (old_r == r)
                    clipboardString.append('\t');
                else if (old_r != -1)
                    clipboardString.append('\n');
                clipboardString.append(cell);
                old_r = r;
            }
            final ClipboardContent content = new ClipboardContent();
            content.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(content);
        });
        ContextMenu menu = new ContextMenu();
        menu.getItems().add(item);
        table.setContextMenu(menu);
    }
}
