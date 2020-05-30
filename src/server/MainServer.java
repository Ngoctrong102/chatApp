package server;

public class MainServer {
    public static void main(String[] args) {
        int port = 3333;
        Server serverManager = new Server(port);
        serverManager.start();
    }
}
