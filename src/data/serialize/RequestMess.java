package data.serialize;

import java.io.Serializable;

public class RequestMess implements Serializable {
    public int roomID;
    public RequestMess(int roomID) { this.roomID = roomID;}
}
