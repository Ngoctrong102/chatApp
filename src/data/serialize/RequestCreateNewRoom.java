package data.serialize;

import java.io.Serializable;

public class RequestCreateNewRoom implements Serializable {
    public String roomName;
    public int userID;

    public RequestCreateNewRoom(String roomName, int userID){
        this.roomName = roomName;
        this.userID = userID;
    }
}
