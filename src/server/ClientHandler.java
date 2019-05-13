package server;

import client.models.*;
import config.Actions;
import server.database.DatabaseHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private DatabaseHandler db;

    public ClientHandler(Server server, Socket socket) {
        try {
            db = new DatabaseHandler();
            this.socket = socket;
            this.server = server;
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            new Thread(() -> {
                CommandObject<?> commandObject;
                try {
                    commandObject = (CommandObject<?>) objectInputStream.readObject();
                    String action = commandObject.getCommand();
                    System.out.println(action);
                    switch (action) {
                        case Actions.AUTHORIZE: {
                            User user =  db.authorize((User) commandObject.getObject());
                            objectOutputStream.writeObject(user);
                            break;
                        }
                        case Actions.REGISTRATION: {
                            boolean isRegistered = db.registration((User) commandObject.getObject());
                            User user = new User();
                            user.setRegistered(isRegistered);
                            objectOutputStream.writeObject(user);
                            break;
                        }
                        case Actions.GET_ALL_MEDICAL_DIRECTIONS: {
                            MedicalDirection[] medicalDirections = db.getAllMedicalDirections();
                            objectOutputStream.writeObject(medicalDirections);
                            break;
                        }
                        case Actions.GET_MEDICAL_SERVICES: {
                            MedicalService[] medicalServices = db.getMedicalServicesByDirection((MedicalDirection) commandObject.getObject());
                            objectOutputStream.writeObject(medicalServices);
                            break;
                        }
                        case Actions.GET_DATES_FOR_SERVICES: {
                            MedicalServiceDate[] medicalServiceDates = db.getDatesByService((MedicalService) commandObject.getObject());
                            objectOutputStream.writeObject(medicalServiceDates);
                            break;
                        }
                        case Actions.ADD_SERVICE: {
                            db.addService((MedicalServiceAction) commandObject.getObject());
                            objectOutputStream.writeObject(null);
                            break;
                        }
                        case Actions.GET_ALL_USER_SERVICES: {
                            MedicalServiceAction[] medicalServiceActions = db.getAllUsersServices((User) commandObject.getObject());
                            objectOutputStream.writeObject(medicalServiceActions);
                            break;
                        }
                        case Actions.DECLINE_MEDICAL_SERVICE_BY_USER: {
                            db.declineMedicalService((MedicalServiceAction) commandObject.getObject(), true);
                            break;
                        }
                        case Actions.ADD_DIRECTION: {
                            MedicalDirection medicalDirection = db.addDirection((MedicalDirection) commandObject.getObject());
                            objectOutputStream.writeObject(medicalDirection);
                            break;
                        }
                    }
                    socket.close();
                } catch (IOException | ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
