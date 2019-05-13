package client.controllers;

import client.*;
import client.models.CommandObject;
import client.models.User;
import config.Actions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AuthPageController extends Connection {
    @FXML
    private TextField loginInput;

    @FXML
    private PasswordField passInput;

    void openPersonalAccountPage(ActionEvent event, String path, String title) throws IOException {
        WindowManager windowManager = new WindowManager(
                600,
                400,
                path,
                title,
                false,
                true,
                false
        );
        windowManager.openNewWindow(
                (Node) event.getSource(),
                windowManager.getFXMLLoader().load()
        );
    }

    private User authorize(String userName, String password) throws IOException, ClassNotFoundException {
        User userCreds = new User(userName, password);
        CommandObject<User> userCommandObject = new CommandObject<>(Actions.AUTHORIZE);
        userCommandObject.setObject(userCreds);
        connect();
        objectOutputStream.writeObject(userCommandObject);
        userCommandObject.setObject((User) objectInputStream.readObject());
        closeConnection();
        if (userCommandObject.getObject() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Пользователь не найден");
            alert.showAndWait();
        }
        return userCommandObject.getObject();
    }

    @FXML
    void onAuthButtonPress(ActionEvent event) {
        if (Service.isInputValid(loginInput, passInput)) {
            try {
                Store.activeUser = authorize(
                        loginInput.getText(),
                        passInput.getText()
                );

                switch (Store.activeUser.getRole()) {
                    case Constants.USER_ROLE: {
                        openPersonalAccountPage(
                                event,
                                "./views/personal-account.fxml",
                                "Личный кабинет"
                            );
                        break;
                    }
                    case Constants.ADMIN_ROLE: {
                        openPersonalAccountPage(
                                event,
                                "./views/admin-personal-account.fxml",
                                "Личный кабинет администратора"
                        );
                        break;
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Произошла ошибка подключения к серверу!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    void onExitButtonPress(ActionEvent event) {
        WindowManager.closeWindow(event);
    }

    @FXML
    void onRegistrationButtonPress(ActionEvent event) throws IOException {
        WindowManager windowManager = new WindowManager(
                600,
                400,
                "./views/registration-page.fxml",
                "Страница регистрации",
                true,
                false,
                false
        );
        windowManager.openNewWindow(
                (Node) event.getSource(),
                windowManager.getFXMLLoader().load()
        );
    }
    @FXML
    void initialize() {
    }
}
