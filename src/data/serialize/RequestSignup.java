package data.serialize;

import java.io.Serializable;

public class RequestSignup implements Serializable {
    private String username;
    private String password;

    public RequestSignup(String username, String password){
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
