package client.controllers;

import client.Connection;
import client.WindowManager;
import client.models.*;
import config.Actions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Optional;

public class AdminPersonalAccountController extends Connection {
    private MedicalDirection[] medicalDirections;
    private MedicalService[] medicalServicesList;
    private MedicalServiceDate[] medicalServiceDates;

    MedicalServiceAction addService(MedicalServiceAction medicalServiceAction) throws IOException, ClassNotFoundException {
        CommandObject<MedicalServiceAction> commandObject = new CommandObject<>(Actions.ADD_SERVICE);
        commandObject.setObject(medicalServiceAction);
        connect();
        objectOutputStream.writeObject(commandObject);
        return (MedicalServiceAction) objectInputStream.readObject();
    }

    void fetchDirections() throws IOException, ClassNotFoundException {
        CommandObject<MedicalDirection[]> commandObject = new CommandObject<>(Actions.GET_ALL_MEDICAL_DIRECTIONS);
        connect();
        objectOutputStream.writeObject(commandObject);
        medicalDirections = (MedicalDirection[]) objectInputStream.readObject();
        closeConnection();
    }

    void fetchServicesByDirection(MedicalDirection medicalDirection) throws IOException, ClassNotFoundException {
        CommandObject<MedicalDirection> commandObject = new CommandObject<>(Actions.GET_MEDICAL_SERVICES);
        commandObject.setObject(medicalDirection);
        connect();
        objectOutputStream.writeObject(commandObject);
        medicalServicesList = (MedicalService[]) objectInputStream.readObject();
        closeConnection();
    }

    void fetchAllServices() throws IOException, ClassNotFoundException {
        CommandObject<MedicalService> commandObject = new CommandObject<>(Actions.GET_ALL_MEDICAL_SERVICES);
        connect();
        objectOutputStream.writeObject(commandObject);
        medicalServicesList = (MedicalService[]) objectInputStream.readObject();
        closeConnection();
    }

    void fetchDatesByService(MedicalService medicalService) throws IOException, ClassNotFoundException {
        CommandObject<MedicalService> commandObject = new CommandObject<>(Actions.GET_DATES_FOR_SERVICES);
        commandObject.setObject(medicalService);
        connect();
        objectOutputStream.writeObject(commandObject);
        medicalServiceDates = (MedicalServiceDate[]) objectInputStream.readObject();
        closeConnection();
    }

    void openAlert(boolean isError, String text) {
        Alert alert = new Alert(isError ? Alert.AlertType.ERROR: Alert.AlertType.INFORMATION);
        alert.setHeaderText(text);
        alert.showAndWait();
    }

    void openServiceDialog(MedicalDirection medicalDirection) {
        Dialog<Pair<String, Float>> serviceDialog = new Dialog<>();
        serviceDialog.setTitle("Добавить услугу");
        serviceDialog.setHeaderText("Для добавление услуги введите её название");
        ButtonType loginButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        serviceDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField serviceTitle = new TextField();
        serviceTitle.setPromptText("Название услуги");
        TextField price = new TextField();
        price.setPromptText("Цена");

        grid.add(new Label("Название услуги:"), 0, 0);
        grid.add(serviceTitle, 1, 0);
        grid.add(new Label("Цена:"), 0, 1);
        grid.add(price, 1, 1);

        Node loginButton = serviceDialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        serviceTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        price.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        serviceDialog.getDialogPane().setContent(grid);

        serviceDialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(serviceTitle.getText(), Float.valueOf(price.getText()));
            }
            return null;
        });
        Optional<Pair<String, Float>> result = serviceDialog.showAndWait();

        result.ifPresent(titlePrice -> {
            MedicalServiceAction medicalServiceAction = new MedicalServiceAction();
            medicalServiceAction.setMedicalDirection(medicalDirection);
            MedicalService medicalService = new MedicalService();
            medicalService.setTitle(titlePrice.getKey());
            medicalService.setPrice(titlePrice.getValue());
            medicalServiceAction.setMedicalService(medicalService);
            try {
                if(addService(medicalServiceAction) != null) {
                    openAlert(false, "Услуга была успешна добавлена");
                } else {
                    openAlert(true, "Услуга с таким именем уже есть в базе!");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            closeConnection();
        });
    }

    void openDateDialog(MedicalService medicalService) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавить дату и время");
        dialog.setHeaderText("Ввод даты и времени в формате yyyy-mm-dd HH:mm:ss");
        dialog.setContentText("Пожалуйста, введите дату и время в нужном формате:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            CommandObject<MedicalServiceAction> commandObject = new CommandObject<>(Actions.ADD_DIRECTION);
            medicalDirection.setTitle(result.get());
            commandObject.setObject(medicalDirection);
        }
    }

    @FXML
    void onAddDirectionButtonPress(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавить направление");
        dialog.setHeaderText("Окно добавления направления");
        dialog.setContentText("Пожалуйста, введите название направления:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
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
        try {
            fetchDirections();
            ChoiceDialog<MedicalDirection> dialog = new ChoiceDialog<>(medicalDirections[0], medicalDirections);
            dialog.setTitle("Выбор направления");
            dialog.setHeaderText("Для продолжения, выберите направление");
            dialog.setContentText("Выберите направление: ");
            Optional<MedicalDirection> result = dialog.showAndWait();
            if (result.isPresent()){
                fetchServicesByDirection(result.get());
                openServiceDialog(result.get());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAddServiceDateButtonPress(ActionEvent event) {
        try {
            fetchAllServices();
            ChoiceDialog<MedicalService> dialog = new ChoiceDialog<>(medicalServicesList[0], medicalServicesList);
            dialog.setTitle("Выбор услуги");
            dialog.setHeaderText("Для продолжения, выберите услугу");
            dialog.setContentText("Выберите услугу: ");
            Optional<MedicalService> result = dialog.showAndWait();
            if (result.isPresent()) {
                fetchDatesByService(result.get());

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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
