package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
    private final int serverPort;

    private final ArrayList<ServerWorker> workerList = new ArrayList<>();

    private final ArrayList<ChatRoom> roomList = new ArrayList<>();
    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    public ArrayList<ServerWorker> getWorkerList(){return workerList;}

    public ArrayList<ChatRoom> getRoomList() {
        return roomList;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true) {
                System.out.println("Waiting for connection ...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accept connection from " + clientSocket);
                ServerWorker worker = new ServerWorker(this, clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatRoom getRoom(int roomID) {
        for (ChatRoom room: roomList){
            if (room.getID() == roomID){
                return room;
            }
        }
        return null;
    }

    public void addRoom(ChatRoom room) {
        roomList.add(room);
    }

    public void turnOff(ChatRoom room, ServerWorker worker) {
        for (ChatRoom chatRoom: roomList){
            if (chatRoom == room){
//                if room.turnOff(worker) == true;  xóa room này ra khoi roomList
//                xóa worker ra khỏi workerList
            }
        }
    }

    public void addUserToRoom(ServerWorker worker, int roomID) {
        for (ChatRoom chatRoom: roomList){
            if (chatRoom.getID() == roomID){
                chatRoom.addUserOnl(worker);
                return;
            }
        }
        worker.startRoom(roomID);
    }

    public void removeWorker(ServerWorker serverWorker) {
        workerList.remove(serverWorker);
    }
}
