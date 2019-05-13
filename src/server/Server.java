package server;

import config.ServerConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends ServerConfig {
    public Server() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен. Ожидаю клиентов...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Пользователь подключен: " + socket.getInetAddress() + ":" + socket.getPort() + "(" + socket.getLocalPort() + ")");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
