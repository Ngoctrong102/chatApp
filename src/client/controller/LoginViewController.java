package client.controller;

import client.ClientSocket;
import com.jfoenix.controls.JFXButton;
import data.serialize.DataLogin;
import data.serialize.RequestLogout;
import data.serialize.RequestRegister;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class LoginViewController {
    public Button btnlogin;
    private ClientSocket client;
    @FXML
    public TextField Username = new TextField();
    @FXML
    public PasswordField Password = new PasswordField();
    @FXML
    private JFXButton btnregister;
    @FXML
    private Pane LoginPane;
    @FXML
    private Pane RegisterPane;
    @FXML
    private TextField Username1;
    @FXML
    private PasswordField Password1;
    @FXML
    private PasswordField Password2;


    public void login() {
        String username = this.Username.getText();
        String password = this.Password.getText();
        DataLogin dataLogin = new DataLogin(username,password);
        try {
            client.objectOutputStream.writeObject(dataLogin);
            Username.setText("");
            Password.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void keyListener(KeyEvent event) throws IOException {
        if(event.getCode() == KeyCode.ENTER) {
            login();
        }
    }

    @FXML
    void createaccount(ActionEvent event) {
        String username= this.Username1.getText();
        String password = this.Password1.getText();
        String confirmpass = this.Password2.getText();
        RequestRegister register = new RequestRegister(username,password);
        if (!confirmpass.equals(password)){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setContentText("Password is different!");
            alert.showAndWait();
            Password1.setText("");
            Password2.setText("");
        } else {
            try {
                client.objectOutputStream.writeObject(register);
                LoginPane.setVisible(true);
                RegisterPane.setVisible(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    void register(ActionEvent event) {
        LoginPane.setVisible(false);
        RegisterPane.setVisible(true);
    }

    @FXML
    void backlogin(ActionEvent event) {
        LoginPane.setVisible(true);
        RegisterPane.setVisible(false);
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    public void setClientSocket(ClientSocket cli) {
        this.client = cli;
    }
}
