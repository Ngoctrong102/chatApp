package data.serialize;

import java.io.Serializable;

public class RoomInfo implements Serializable {
    public int roomID;
    public String roomName;
    public boolean hasNewMess = false;

    public RoomInfo(int roomID, String roomName){
        this.roomID = roomID;
        this.roomName = roomName;
    }
}
