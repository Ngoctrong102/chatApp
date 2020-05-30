package client;

import data.serialize.*;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientSocket {
    public Socket socket;
    public ObjectInputStream objectInputStream;
    public ObjectOutputStream objectOutputStream;

    public ClientSocket(){
        try {
            socket = new Socket("localhost",3333);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void requestMess(int roomID){
        try {
            objectOutputStream.writeObject(new RequestMess(roomID));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        socket.getInputStream().close();
        objectInputStream.close();
        objectOutputStream.writeObject(new Disconnect());
        socket.close();
    }

    public void login(DataLogin req) {
        try {
            objectOutputStream.writeObject(req);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void requestFriends(RequestFriend requestFriend) throws IOException {
        objectOutputStream.writeObject(requestFriend);
    }

    public void sendMess(RequestSendMess requestSendMess) throws IOException {
        objectOutputStream.writeObject(requestSendMess);
    }
}
