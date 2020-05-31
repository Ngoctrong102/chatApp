package data.serialize;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private ArrayList<Integer> friendIDs = new ArrayList<Integer>();
    private ArrayList<RoomInfo> rooms = new ArrayList<RoomInfo>();

    public User(ResultSet resultSet) throws SQLException {
        id = resultSet.getInt("id");
        username = resultSet.getString("username");
        password = resultSet.getString("password");
        for(String friendID: resultSet.getString("friendIDs").split(",")){ friendIDs.add(Integer.parseInt(friendID)); }
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

    public ArrayList<RoomInfo> getRooms() {
        return rooms;
    }

    public int getId() {
        return id;
    }
}
