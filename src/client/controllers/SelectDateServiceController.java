package client.controllers;

import client.Connection;
import client.Store;
import client.WindowManager;
import client.models.CommandObject;
import client.models.MedicalService;
import client.models.MedicalServiceAction;
import client.models.MedicalServiceDate;
import config.Actions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.util.Arrays;

public class SelectDateServiceController extends Connection {
    private ObservableList<MedicalServiceDate> medicalServiceDatesList = FXCollections.observableArrayList();

    @FXML
    private ComboBox<MedicalServiceDate> dateSelectCombo;

    void addService() {
        CommandObject<MedicalServiceAction> commandObject = new CommandObject<>(Actions.ADD_SERVICE);
        Store.currentService.setCurrentUser(Store.activeUser);
        commandObject.setObject(Store.currentService);
        try {
            connect();
            objectOutputStream.writeObject(commandObject);
            objectInputStream.readObject();
            closeConnection();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void openSelectDateWindow(ActionEvent event) throws IOException {
        WindowManager windowManager = new WindowManager(
                600,
                400,
                "./views/success-added-service-page.fxml",
                "Успех!",
                false,
                true,
                false
        );
        windowManager.openNewWindow(
                (Node) event.getSource(),
                windowManager.getFXMLLoader().load()
        );
    }

    @FXML
    void onBackButtonPress(ActionEvent event) {
        WindowManager.closeWindow(event);
    }

    @FXML
    void onOkButtonPress(ActionEvent event) {
        if (dateSelectCombo.getValue() != null) {
            Store.currentService.setMedicalServiceDate(dateSelectCombo.getValue());
            addService();
            try {
                openSelectDateWindow(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else  {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Пожалуйста, выберите дату и время!");
            alert.showAndWait();
        }
    }

    @FXML
    void initialize() {
        CommandObject<MedicalService> commandObject = new CommandObject<>(Actions.GET_DATES_FOR_SERVICES);
        commandObject.setObject(Store.currentService.getMedicalService());
        MedicalServiceDate[] medicalServiceDates;
        try {
            connect();
            objectOutputStream.writeObject(commandObject);
            medicalServiceDates = (MedicalServiceDate[]) objectInputStream.readObject();
            medicalServiceDatesList.addAll(Arrays.asList(medicalServiceDates));
            dateSelectCombo.setItems(medicalServiceDatesList);
            closeConnection();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
