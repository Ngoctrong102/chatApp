package server;

import data.serialize.*;

import java.io.*;
import java.net.Socket;
import java.sql.*;
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
            if (request instanceof RequestLogout){
                handleLogout();
                System.out.println("Logout from " + user.getUsername());
            }
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
            if (request instanceof RequestCreateNewRoom){
                recreateNewRoom((RequestCreateNewRoom)request);
            }
            if (request instanceof RequestListRoomToJoin){
                returnListRoomToJoin();
            }
            if (request instanceof RequestJoinRoom){
                handleJoin(((RequestJoinRoom) request).roomID);
            }
            if (request instanceof FileMess){
                sendFile((FileMess)request);
            }
            if (request instanceof RequestMemOf){
                returnListUser(((RequestMemOf) request).roomID);
            }
        }
    }

    private void handleLogout() {
        server.removeWorker(this);
        for (ChatRoom room: listRoom){
            room.removeUserOff(this);
        }
    }

    private void returnListUser(int roomID) {
        for (ChatRoom chatRoom: listRoom){
            if (chatRoom.getID() == roomID){
                ArrayList<ServerWorker> listUser = chatRoom.getUsers();
                ResponseFriend users = new ResponseFriend();
                for (ServerWorker serverWorker: listUser){
                    users.add(serverWorker.user);
                }
                try {
                    outputStream.writeObject(users);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendFile(FileMess request) {
        for (ChatRoom room: listRoom){
            if (room.getID() == request.roomID){
                room.sendFile(request);
                break;
            }
        }
    }

    private void handleJoin(int roomID) {
        PreparedStatement ps = null;
        String tmp = "";
        String stm = "";
        try {
            for (RoomInfo roomInfo: user.getRooms()) tmp+=roomInfo.roomID+",";
            stm = "UPDATE Users SET roomIDs = " + "\'" + tmp + roomID + "\'" + " WHERE id = " + user.getId();
            System.out.println(stm);
            ps = connection.prepareStatement(stm);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        server.addUserToRoom(this,roomID);
        try {
            stm = "SELECT * FROM RoomChat WHERE id = " + roomID;
            System.out.println(stm);
            ps = connection.prepareStatement(stm);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            outputStream.writeObject(new ResponseNewRoom(new RoomInfo(roomID,resultSet.getString("roomName"))));
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void returnListRoomToJoin() {
        PreparedStatement ps = null;
        ArrayList<RoomInfo> lr = user.getRooms();
        String tmp = lr.size() > 0 ? Integer.toString(lr.get(0).roomID) : "";
        for (int i = 1; i< lr.size();i++) {
            tmp+="," + Integer.toString(lr.get(i).roomID);
        }
        String stm = "SELECT * FROM RoomChat WHERE id NOT IN (" + tmp + ")";
        if (tmp.equals("")) stm = "SELECT * FROM RoomChat";
        System.out.println(stm);
        try {
            ps = this.connection.prepareStatement(stm);
            ResultSet resultSet = ps.executeQuery();
            outputStream.writeObject(new ResponseListRoom(resultSet));
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    private void recreateNewRoom(RequestCreateNewRoom request) {
        PreparedStatement ps = null;
        try {
            String stm = "INSERT INTO RoomChat (roomName) VALUES (N'"+ request.roomName +"')";
            System.out.println(stm);
            ps = connection.prepareStatement(stm,Statement.RETURN_GENERATED_KEYS);
            int affectedRows = ps.executeUpdate();
            ResultSet resultSet = ps.getGeneratedKeys();
            resultSet.next();
            int roomID = resultSet.getInt(1);
            ChatRoom room = new ChatRoom(roomID);
            room.addUserOnl(this);
            server.addRoom(room);
            listRoom.add(room);
            updateRoomList(roomID,request.roomName);
            outputStream.writeObject(new ResponseNewRoom(new RoomInfo(roomID,request.roomName)));
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    private void updateRoomList(int roomID, String roomName) {
        PreparedStatement ps = null;
        try {
            String tmp = "";
            for (RoomInfo roomInfo: user.getRooms()) tmp+=roomInfo.roomID+",";
            String stm = "UPDATE Users SET roomIDs = " + "\'" + tmp + roomID + "\'" + " WHERE id = " + user.getId();
            System.out.println(stm);
            ps = connection.prepareStatement(stm);
            ps.executeUpdate();
            user.getRooms().add(new RoomInfo(roomID,roomName));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
            if ( resultSet.getString("roomIDs") != null) {
                if (!resultSet.getString("roomIDs").equals("")) {
                    String stm = "SELECT * FROM RoomChat WHERE id IN (" + resultSet.getString("roomIDs") + ")";
                    System.out.println(stm);
                    ps = this.connection.prepareStatement(stm);
                    resultSet = ps.executeQuery();
                    while (resultSet.next()) {
                        user.getRooms().add(new RoomInfo(resultSet.getInt("id"), resultSet.getString("roomName")));
                    }
                }
                startAllRoom();
            }
            outputStream.writeObject(user);
        }
        return true;
    }

    private void startAllRoom() {
        for(RoomInfo roomInfo: user.getRooms()){
            ChatRoom room = server.getRoom(roomInfo.roomID);
            if (room!=null){
                room.addUserOnl(this);
                listRoom.add(room);
            }
            else {
                room = new ChatRoom(roomInfo.roomID);
                room.addUserOnl(this);
                listRoom.add(room);
                server.addRoom(room);
            }
        }
    }

    public void send(Message message) throws IOException {
        outputStream.writeObject(message);
    }

    public void startRoom(int roomID) {
        ChatRoom room = new ChatRoom(roomID);
        room.addUserOnl(this);
        listRoom.add(room);
        server.addRoom(room);
    }

    public void sendF(FileMess request) {
        try {
            outputStream.writeObject(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
