package data.serialize;

import java.io.Serializable;

public class RequestMemOf implements Serializable {
    public int roomID;
    public RequestMemOf(int roomID){
        this.roomID = roomID;
    }
}
