package data.serialize;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ResponseFriend implements Serializable {
    public ArrayList<FriendInfo> listFriend;

    public ResponseFriend(){
        listFriend = new ArrayList<FriendInfo>();
    }
    public ResponseFriend(ResultSet resultSet) throws SQLException {
        listFriend = new ArrayList<FriendInfo>();
        while (resultSet.next()){
            FriendInfo fi = new FriendInfo(resultSet.getInt("id"),resultSet.getString("username"));
            listFriend.add(fi);
        }
    }

    public void add(User user) {
        FriendInfo friendInfo = new FriendInfo(user.getId(),user.getUsername());
        listFriend.add(friendInfo);
    }


    public class FriendInfo implements Serializable{
        public int id;
        public String username;

        public FriendInfo(int id, String username) {
            this.id = id;
            this.username = username;
        }
    }
}
