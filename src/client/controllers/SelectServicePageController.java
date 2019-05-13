package client.controllers;

import client.Connection;
import client.Store;
import client.WindowManager;
import client.models.CommandObject;
import client.models.MedicalDirection;
import client.models.MedicalService;
import client.models.MedicalServiceAction;
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

public class SelectServicePageController extends Connection {
    private ObservableList<MedicalDirection> medicalDirections = FXCollections.observableArrayList();
    private ObservableList<MedicalService> medicalServicesList = FXCollections.observableArrayList();

    @FXML
    private ComboBox<MedicalDirection> medicalDirectionCombo;

    @FXML
    private ComboBox<MedicalService> medicalServicesCombo;


    void openSelectDateWindow(ActionEvent event) throws IOException {
        WindowManager windowManager = new WindowManager(
                600,
                400,
                "./views/user-select-date-service-page.fxml",
                "Выбор даты",
                false,
                true,
                false
        );
        windowManager.openNewWindow(
                (Node) event.getSource(),
                windowManager.getFXMLLoader().load()
        );
    }

    void fetchMedicalServicesByDirection(MedicalDirection medicalDirection) {
        CommandObject<MedicalDirection> commandObject = new CommandObject<>(Actions.GET_MEDICAL_SERVICES);
        commandObject.setObject(medicalDirection);
        MedicalService[] medicalServices;
        try {
            connect();
            objectOutputStream.writeObject(commandObject);
            medicalServices = (MedicalService[]) objectInputStream.readObject();
            medicalServicesCombo.getItems().clear();
            medicalServicesList.addAll(Arrays.asList(medicalServices));
            medicalServicesCombo.setItems(medicalServicesList);
            closeConnection();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onMedicalDirectionSelected(ActionEvent event) {
        medicalServicesCombo.setDisable(false);
        fetchMedicalServicesByDirection(medicalDirectionCombo.getValue());
    }

    @FXML
    void onExitButtonPress(ActionEvent event) {
        WindowManager.closeWindow(event);
    }

    @FXML
    void onOkButtonPress(ActionEvent event) {
        if (medicalServicesCombo.getValue() != null && medicalDirectionCombo.getValue() != null) {
            Store.currentService = new MedicalServiceAction();
            Store.currentService.setMedicalDirection(medicalDirectionCombo.getValue());
            Store.currentService.setMedicalService(medicalServicesCombo.getValue());
            try {
                openSelectDateWindow(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Пожалуйста, выберите направление и услугу!");
            alert.showAndWait();
        }
    }
    @FXML
    void initialize() {
        CommandObject<MedicalDirection[]> commandObject = new CommandObject<>(Actions.GET_ALL_MEDICAL_DIRECTIONS);
        try {
            connect();
            objectOutputStream.writeObject(commandObject);
            commandObject.setObject((MedicalDirection[]) objectInputStream.readObject());
            medicalDirections.addAll(Arrays.asList(commandObject.getObject()));
            medicalDirectionCombo.setItems(medicalDirections);
            closeConnection();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
