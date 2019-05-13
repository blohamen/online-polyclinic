package client.controllers;

import client.Store;
import client.WindowManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.IOException;


public class PersonalAccountController {
    @FXML
    Label userNameLabel;

    void openNewWindow(ActionEvent event, String path, String windowTitle) throws IOException {
        WindowManager windowManager = new WindowManager(
                600,
                400,
                path,
                windowTitle,
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
    void onAddServiceButtonPress(ActionEvent event) {
        try {
            openNewWindow(
                    event,
                    "./views/user-select-services-page.fxml",
                    "Выбор услуги"
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onShowServicesButtonPress(ActionEvent event) {
        try {
            openNewWindow(
                    event,
                    "./views/user-services-list-page.fxml",
                    "Мои услуги"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onExitButtonPress(ActionEvent event) {
        WindowManager.closeWindow(event);
    }

    @FXML
    void initialize() {
        userNameLabel.setText(Store.activeUser.getUserName());
    }

}
