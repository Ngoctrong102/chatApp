package data.serialize;

import java.io.Serializable;

public class DataLogin implements Serializable {
    private String username;
    private String password;

    public DataLogin(String username, String password){
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
