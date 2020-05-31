package data.serialize;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ResponseListRoom implements Serializable {
    public ArrayList<RoomInfo> roomInfos;
    public ResponseListRoom(ResultSet resultSet) throws SQLException {
        roomInfos = new ArrayList<RoomInfo>();
        while (resultSet.next()){
            RoomInfo roomInfo = new RoomInfo(resultSet.getInt("id"),resultSet.getString("roomName"));
            roomInfos.add(roomInfo);
        }
    }
}
