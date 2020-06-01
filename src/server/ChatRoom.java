package server;

import data.serialize.FileMess;
import data.serialize.Message;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChatRoom {
    private final int roomID;
    private final ArrayList<ServerWorker> listUserOnl = new ArrayList<>();
    private Connection connection;

    public ChatRoom(int roomID) {
        try {
            this.connection = DB.getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.roomID = roomID;
    }


    public void addUserOnl(ServerWorker serverWorker){
        listUserOnl.add(serverWorker);
    }

    public int getID() {
        return this.roomID;
    }

    public void sendMess(Message message) throws IOException {
        addMessToDB(message);
        for (ServerWorker worker: listUserOnl){
            worker.send(message);
        }
    }

    private void addMessToDB(Message message)  {
        PreparedStatement ps = null;
        message.content = message.content.replaceAll("\'|\"","");
        try {
            String stm = "INSERT INTO Messages (roomID,fromUser,content) VALUES ("+ Integer.toString(message.roomID) +","+ Integer.toString(message.from) +",N'"+ message.content +"')";
            System.out.println(stm);
            ps = connection.prepareStatement(stm);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public boolean turnOff(ServerWorker worker) {
//        if (listUserOnl k cos ai -> tắt ( ){ xóa user ra khỏi listOnl }-> return true
        return true;
    }

    public void sendFile(FileMess request) {
        PreparedStatement ps = null;
        request.fileName = request.fileName.replaceAll("\'|\"","");
        try {
            String stm = "INSERT INTO Messages (roomID,fromUser,content) VALUES ("+ Integer.toString(request.roomID) +","+ Integer.toString(request.from) +",N'"+ request.fileName +"<FILE>')";
            System.out.println(stm);
            ps = connection.prepareStatement(stm);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (ServerWorker worker: listUserOnl){
            worker.sendF(request);
        }
    }

    public ArrayList<ServerWorker> getUsers() {
        return listUserOnl;
    }
}
