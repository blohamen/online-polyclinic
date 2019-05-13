package client.controllers;

import client.*;
import client.models.CommandObject;
import client.models.User;
import config.Actions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import server.Server;

import javax.swing.*;
import java.io.IOException;

public class RegistrationPageController extends Connection {
    @FXML
    private TextField loginInput;

    @FXML
    private PasswordField passInput;

    boolean registration(String userName, String password) throws IOException, ClassNotFoundException {
        User userCreds = new User(userName, password);
        CommandObject<User> userCommandObject = new CommandObject<>(Actions.REGISTRATION);
        userCommandObject.setObject(userCreds);
        connect();
        objectOutputStream.writeObject(userCommandObject);
        userCommandObject.setObject((User) objectInputStream.readObject());
        closeConnection();
        return userCommandObject.getObject().isRegistered();
    }


    @FXML
    void onExitButtonPress(ActionEvent event) {
        WindowManager.closeWindow(event);
    }

    @FXML
    void onRegisterButtonPress(ActionEvent event) {
        if (Service.isInputValid(loginInput, passInput)) {
            try {
               boolean isRegistered = registration(
                        loginInput.getText(),
                        passInput.getText()
                );
                if (isRegistered) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Пользователь зарегистрирован");
                    alert.showAndWait();
                    WindowManager.closeWindow(event);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Пользователь уже существует в базе");
                    alert.showAndWait();
                }
            } catch (IOException | ClassNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Произошла ошибка подключения к серверу!");
                alert.showAndWait();
            }
        }
    }
}
