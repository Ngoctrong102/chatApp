package data.serialize;

import java.io.Serializable;

public class RequsestMemOf implements Serializable {
    public int roomID;
    public RequsestMemOf(int roomID){
        this.roomID = roomID;
    }
}
