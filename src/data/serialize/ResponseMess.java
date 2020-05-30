package data.serialize;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ResponseMess implements Serializable {
    public ArrayList<Message> arrayListMess = new ArrayList<Message>();
    public ResponseMess(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            Message mess = new Message();
            mess.roomID = resultSet.getInt("roomID");
            mess.from = resultSet.getInt("fromUser");
            mess.content = resultSet.getString("content");
            arrayListMess.add(mess);
        }
    }
}
