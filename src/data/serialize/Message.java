package data.serialize;

import java.io.Serializable;

public class Message implements Serializable {
    public int from;
    public int roomID;
    public String content;
}
