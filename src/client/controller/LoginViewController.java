package client.controller;

import client.ClientSocket;
import data.serialize.DataLogin;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

public class LoginViewController {
    public Button btnlogin;
    private ClientSocket client;
    @FXML
    public TextField Username = new TextField();
    @FXML
    public PasswordField Password = new PasswordField();
    public void login() {
        String username = this.Username.getText();
        String password = this.Password.getText();
        DataLogin dataLogin = new DataLogin(username,password);
        try {
            client.objectOutputStream.writeObject(dataLogin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add Enter action
    public void keyListener(KeyEvent event) throws IOException {
        if(event.getCode() == KeyCode.ENTER) {
            // Do stuff
            login();
        }
    }


    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    public void setClientSocket(ClientSocket cli) {
        this.client = cli;
    }
}
