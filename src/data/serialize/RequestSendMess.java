package data.serialize;

import java.io.Serializable;

public class RequestSendMess implements Serializable {
    public Message message;
    public RequestSendMess(Message message){
        this.message = message;
    }
}
