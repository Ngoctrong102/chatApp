package data.serialize;

import java.io.Serializable;

public class RequestJoinRoom implements Serializable {
    public int roomID;

    public RequestJoinRoom(int roomID) {
        this.roomID = roomID;
    }
}
