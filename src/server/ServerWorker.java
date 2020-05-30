package server;

import data.serialize.*;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerWorker extends Thread{
    private Server server;
    private Socket clientSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final ArrayList<ChatRoom> listRoom = new ArrayList<>();
    private User user;
    private Connection connection;
    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        try {
            connection = DB.getConnection();
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            handleClient();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    private void handleClient() throws IOException, ClassNotFoundException, SQLException {
        this.outputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(this.clientSocket.getInputStream());
        while (true){
            Serializable request = (Serializable)inputStream.readObject();
            if (request instanceof DataLogin){
                handleLogin((DataLogin)request);
            }
            if (request instanceof RequestFriend){
                returnListFriend((RequestFriend)request);
            }
            if (request instanceof RequestMess){
                returnMess((RequestMess)request);
            }
            if (request instanceof RequestSendMess){
                sendMess((RequestSendMess)request);
            }
            if (request instanceof Disconnect){
                disconnect((Disconnect)request);
            }
        }
    }

    private void disconnect(Disconnect request) {
        for (ChatRoom room: listRoom){
            server.turnOff(room,this);
        }
    }

    private void sendMess(RequestSendMess request) {
        for (ChatRoom room: listRoom){
            if (room.getID() == request.message.roomID){
                try {
                    room.sendMess(request.message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void returnMess(RequestMess request) {
        PreparedStatement ps = null;
        try {
            ps = this.connection.prepareStatement("SELECT * FROM Messages WHERE roomID = ?");
            ps.setString(1, String.valueOf(request.roomID));
            ResultSet resultSet = ps.executeQuery();
            outputStream.writeObject(new ResponseMess(resultSet));
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    private void returnListFriend(RequestFriend request) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT friendIDs FROM Users WHERE id = ?");
            ps.setString(1, String.valueOf(request.id));
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()==false) outputStream.writeObject(new ResponseFriend());
            String listID = resultSet.getString("friendIDs");
            String stm = "SELECT * FROM Users WHERE id IN ("+ listID + ")";
            ps = this.connection.prepareStatement(stm);
            resultSet = ps.executeQuery();
            outputStream.writeObject(new ResponseFriend(resultSet));
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    private void handleMessage(Message req) {


    }

    private boolean handleLogin(DataLogin req) throws SQLException, IOException {
        PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM Users WHERE username = ?");
        ps.setString(1, req.getUsername());
        System.out.println(req.getUsername() + " " + req.getPassword());
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()==false) return false;
        if (resultSet.getString("password").equals(req.getPassword())){
            user = new User(resultSet);
            startAllRoom();
            outputStream.writeObject(user);
        }
        return true;
    }

    private void startAllRoom() {
        for(int roomID: user.getRoomIDs()){
            ChatRoom room = server.getRoom(roomID);
            if (room!=null){
                room.addUserOnl(this);
                listRoom.add(room);
            }
            else {
                room = new ChatRoom(roomID);
                room.addUserOnl(this);
                listRoom.add(room);
                server.addRoom(room);
            }
        }
    }

    public void send(Message message) throws IOException {
        outputStream.writeObject(message);
    }
}
