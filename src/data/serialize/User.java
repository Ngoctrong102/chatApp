package data.serialize;

import server.ChatRoom;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private ArrayList<Integer> friendIDs = new ArrayList<Integer>();
    private ArrayList<Integer> roomIDs = new ArrayList<Integer>();

    public User(ResultSet resultSet) throws SQLException {
        id = resultSet.getInt("id");
        username = resultSet.getString("username");
        password = resultSet.getString("password");
        for(String friendID: resultSet.getString("friendIDs").split(",")){ friendIDs.add(Integer.parseInt(friendID)); }
        for(String roomID: resultSet.getString("roomIDs").split(",")){ roomIDs.add(Integer.parseInt(roomID)); }
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<Integer> getFriendIDs() {
        return friendIDs;
    }

    public ArrayList<Integer> getRoomIDs() {
        return roomIDs;
    }

    public int getId() {
        return id;
    }
}
