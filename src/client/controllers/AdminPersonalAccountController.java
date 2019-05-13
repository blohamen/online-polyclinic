package client.controllers;

import client.Connection;
import client.WindowManager;
import client.models.CommandObject;
import client.models.MedicalDirection;
import config.Actions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.util.Optional;

public class AdminPersonalAccountController extends Connection {

    void fetchDirections() throws IOException, ClassNotFoundException {
        CommandObject<MedicalDirection[]> commandObject = new CommandObject<>(Actions.GET_ALL_MEDICAL_DIRECTIONS);
        connect();
        objectOutputStream.writeObject(commandObject);
        commandObject.setObject((MedicalDirection[]) objectInputStream.readObject());
    }

    void openAlert(boolean isError, String text) {
        Alert alert = new Alert(isError ? Alert.AlertType.ERROR: Alert.AlertType.INFORMATION);
        alert.setHeaderText(text);
        alert.showAndWait();
    }

    @FXML
    void onAddDirectionButtonPress(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавить направление");
        dialog.setHeaderText("Окно добавления направления");
        dialog.setContentText("Пожалуйста, введите название направления:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            CommandObject<MedicalDirection> commandObject = new CommandObject<>(Actions.ADD_DIRECTION);
            MedicalDirection medicalDirection = new MedicalDirection();
            medicalDirection.setTitle(result.get());
            commandObject.setObject(medicalDirection);
            try {
                connect();
                objectOutputStream.writeObject(commandObject);
                commandObject.setObject((MedicalDirection) objectInputStream.readObject());
                closeConnection();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (commandObject.getObject() != null) {
                openAlert(false,"Направление было добавлено!");
            } else  {
                openAlert(true, "Направление с таким именем уже есть в базе!");
            }
        }
    }

    @FXML
    void onAddServiceButtonPress(ActionEvent event) {

    }

    @FXML
    void onAddServiceDateButtonPress(ActionEvent event) {

    }

    @FXML
    void onDeleteServiceButtonPress(ActionEvent event) {

    }

    @FXML
    void onExitButtonPress(ActionEvent event) {
        WindowManager.closeWindow(event);
    }

    @FXML
    void onSearchUserServicesButtonPress(ActionEvent event) {

    }

    @FXML
    void initialize() {

    }
}
