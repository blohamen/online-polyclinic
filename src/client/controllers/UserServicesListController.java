package client.controllers;

import client.Connection;
import client.Store;
import client.WindowManager;
import client.models.*;
import config.Actions;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.Arrays;

public class UserServicesListController extends Connection {
    private ObservableList<MedicalServiceAction> medicalServiceActionsList = FXCollections.observableArrayList();

    @FXML
    private TableView<MedicalServiceAction> listTable;

    @FXML
    private TableColumn<MedicalServiceAction, String> medicalDirectionColumn;

    @FXML
    private TableColumn<MedicalServiceAction, String> medicalServiceColumn;

    @FXML
    private TableColumn<MedicalServiceAction, String> dateColumn;

    @FXML
    private TableColumn<MedicalServiceAction, String> priceColumn;

    @FXML
    void onCancelServiceButtonPress(ActionEvent event) {
        MedicalServiceAction selectedMedicalServiceAction = listTable.getSelectionModel().getSelectedItem();
        if (selectedMedicalServiceAction != null) {
            try {
                CommandObject<MedicalServiceAction> commandObject = new CommandObject<>(Actions.DECLINE_MEDICAL_SERVICE_BY_USER);
                commandObject.setObject(selectedMedicalServiceAction);
                connect();
                objectOutputStream.writeObject(commandObject);
                closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Услуга была отменена!");
            alert.setContentText("Для обновления страницы перезайдите на неё!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Пожалуйста, выберите услугу из таблицы!");
            alert.showAndWait();
        }
    }

    @FXML
    void onReturnMenuButtonPress(ActionEvent event) {
        WindowManager.closeWindow(event);
    }

    @FXML
    void initialize() {
        CommandObject<User> commandObject = new CommandObject<>(Actions.GET_ALL_USER_SERVICES);
        commandObject.setObject(Store.activeUser);
        MedicalServiceAction[] medicalServiceActions;
        try {
            connect();
            objectOutputStream.writeObject(commandObject);
            medicalServiceActions = ((MedicalServiceAction[]) objectInputStream.readObject());

            medicalDirectionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedicalDirection().getTitle()));
            medicalServiceColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedicalService().getTitle()));
            dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedicalServiceDate().getDate()));
            priceColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getMedicalService().getPrice())));
            medicalServiceActionsList.addAll(Arrays.asList(medicalServiceActions));
            listTable.setItems(medicalServiceActionsList);

            closeConnection();
        }
            catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
