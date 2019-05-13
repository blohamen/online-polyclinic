package client;

import config.ServerConfig;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection extends ServerConfig {
    protected Socket clientSocket;
    protected ObjectOutputStream objectOutputStream;
    protected ObjectInputStream objectInputStream;
    protected void connect() throws IOException {
        if (clientSocket == null || clientSocket.isClosed()) {
            clientSocket = new Socket(SERVER_IP, PORT);
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        }
    }
    protected void closeConnection() {
        try {
            clientSocket.close();
            objectInputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
