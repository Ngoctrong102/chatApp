package data.serialize;

import java.io.Serializable;

public class RequestRegister implements Serializable {
    private String username;
    private String password;

    public RequestRegister(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
