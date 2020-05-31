package data.serialize;

import java.io.Serializable;

public class ResponseNewRoom implements Serializable {
    public RoomInfo roomInfo;
    public ResponseNewRoom(RoomInfo roomInfo){
        this.roomInfo = roomInfo;
    }
}
