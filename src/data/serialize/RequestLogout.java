package data.serialize;

import java.io.Serializable;

public class RequestLogout implements Serializable {
    private User username;


    public RequestLogout(User username)  {
        this.username = username;

    }

    
}
