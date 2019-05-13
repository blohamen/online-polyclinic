package client.controllers;

import client.Store;
import client.WindowManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SuccessAddServiceController {

    @FXML
    private Label userNameLabel;

    @FXML
    private Label medicalDirectionLabel;

    @FXML
    private Label medicalServiceLabel;

    @FXML
    private Label serviceDateLabel;

    @FXML
    private Label priceLabel;

    @FXML
    void onExitButtonPress(ActionEvent event) {
        WindowManager.closeWindow(event);
    }

    @FXML
    void initialize() {
        userNameLabel.setText(Store.activeUser.getUserName());
        medicalDirectionLabel.setText(Store.currentService.getMedicalDirection().getTitle());
        medicalServiceLabel.setText(Store.currentService.getMedicalService().getTitle());
        serviceDateLabel.setText(Store.currentService.getMedicalServiceDate().getDate());
        priceLabel.setText(String.valueOf(Store.currentService.getMedicalService().getPrice()));
    }

}
