package client.controller;

import client.ClientSocket;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import data.serialize.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AppViewController {
    private ClientSocket client;
    private User user;
    private int roomCur;
    private int nextPosMess;

    @FXML
    private JFXButton friendsIcon = new JFXButton();
    @FXML
    private JFXButton roomsIcon = new JFXButton();
    @FXML
    private Group friendList = new Group();
    @FXML
    private Group messGroup = new Group();
    @FXML
    private Label username = new Label();
    @FXML
    private JFXTextArea msg = new JFXTextArea();
    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    public void setData(ClientSocket cli, User user) {
        this.client = cli;
        this.user = user;
    }
    public void loadView(){
        loadViewUser();
        renderRoom();

    }

    public void getFriends(ActionEvent actionEvent) {
        try {
            client.requestFriends(new RequestFriend(user.getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void renderRoom() {
        friendList.getChildren().clear();
        int i = 0;
        for (int roomID: user.getRoomIDs()){
            if (i==0) {
                client.requestMess(roomID);
                roomCur = roomID;
            }
            HBox hb = new HBox();
            hb.setAlignment(Pos.CENTER_LEFT);
            hb.setLayoutX(-3);
            hb.setLayoutY(i*68);
            hb.setPrefHeight(68);
            hb.setPrefWidth(279);
            hb.setStyle("-fx-background-color: #393C43;");
            hb.getProperties().put("class","active");
            hb.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    roomCur = roomID;
                    client.requestMess(roomID);
                }
            });
            File file = new File("src/client/image/avartar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt= new ImageView(image);
            avt.setFitHeight(63);
            avt.setFitWidth(70);
            avt.setPickOnBounds(true);
            avt.setPreserveRatio(true);
            Label username = new Label("Room "+roomID);
            username.setPrefHeight(38);
            username.setPrefWidth(172);
            username.setTextFill(Paint.valueOf("WHITE"));
            username.setFont(Font.font(22));
            hb.getChildren().addAll(avt,username);
            friendList.getChildren().add(hb);
            i++;
        }
    }

    private void loadViewUser() {
        username.setText(user.getUsername());

    }

    public void handlebutton(ActionEvent actionEvent) {

    }



    public void renderFriend(ResponseFriend res) {
        friendList.getChildren().clear();
        int i = 0;
        for (ResponseFriend.FriendInfo friendInfo: res.listFriend){
            HBox hb = new HBox();
            hb.setAlignment(Pos.CENTER_LEFT);
            hb.setLayoutX(-3);
            hb.setLayoutY(i*68);
            hb.setPrefHeight(68);
            hb.setPrefWidth(279);
            hb.setStyle("-fx-background-color: #393C43;");
            hb.getProperties().put("class","active");
            hb.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.println("bam duoc r");
                }
            });
            File file = new File("src/client/image/avartar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt= new ImageView(image);
            avt.setFitHeight(63);
            avt.setFitWidth(70);
            avt.setPickOnBounds(true);
            avt.setPreserveRatio(true);
            Label username = new Label(friendInfo.username);
            username.setPrefHeight(38);
            username.setPrefWidth(172);
            username.setTextFill(Paint.valueOf("WHITE"));
            username.setFont(Font.font(22));
            hb.getChildren().addAll(avt,username);
            friendList.getChildren().add(hb);
            i++;
        }
    }

    public void renderMess(ResponseMess res){
        messGroup.getChildren().clear();
        nextPosMess = 0;
        for (Message mess: res.arrayListMess){
            HBox hb = new HBox();
            hb.setLayoutY(nextPosMess);
            hb.setPrefHeight(76);
            hb.setPrefWidth(773);
            File file = new File("src/client/image/avartar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt= new ImageView(image);
            Label username = new Label("User " + mess.from);
            username.setTextFill(Paint.valueOf("WHITE"));
            username.setFont(Font.font(22));
            username.setPrefHeight(38);
            username.setPrefWidth(713);
            Label content = new Label(mess.content);
            content.setContentDisplay(ContentDisplay.valueOf("CENTER"));
            content.setPrefWidth(716);
            content.setPrefHeight(59);
            content.setTextFill(Paint.valueOf("WHITE"));
            content.setFont(Font.font(19));
            VBox vb = new VBox();
            vb.setAlignment(Pos.valueOf("TOP_CENTER"));
            vb.setPrefHeight(82);
            vb.getChildren().addAll(username,content);
            if (mess.from == user.getId()){
                username.setAlignment(Pos.valueOf("TOP_RIGHT"));
                content.setAlignment(Pos.valueOf("TOP_RIGHT"));
                hb.getChildren().add(vb);
                hb.getChildren().add(avt);
            }
            else {
                username.setAlignment(Pos.valueOf("TOP_LEFT"));
                content.setAlignment(Pos.valueOf("TOP_LEFT"));
                hb.getChildren().add(avt);
                hb.getChildren().add(vb);
            }
            messGroup.getChildren().add(hb);
            nextPosMess+=68;
        }



    }

    public void getRooms(ActionEvent actionEvent) {
        friendList.getChildren().clear();
        renderRoom();
    }

    public void Sendmsg(ActionEvent actionEvent) {
        Message mess = new Message();
        mess.from = user.getId();
        mess.roomID = roomCur;
        mess.content = msg.getText();
        msg.setText("");
        try {
            client.sendMess(new RequestSendMess(mess));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renderNewMess(Message mess) {
        HBox hb = new HBox();
        hb.setLayoutY(nextPosMess);
        hb.setPrefHeight(76);
        hb.setPrefWidth(773);
        File file = new File("src/client/image/avartar.png");
        Image image = new Image(file.toURI().toString());
        ImageView avt= new ImageView(image);
        Label username = new Label("User " + mess.from);
        username.setTextFill(Paint.valueOf("WHITE"));
        username.setFont(Font.font(22));
        username.setPrefHeight(38);
        username.setPrefWidth(713);
        Label content = new Label(mess.content);
        content.setContentDisplay(ContentDisplay.valueOf("CENTER"));
        content.setPrefWidth(716);
        content.setPrefHeight(59);
        content.setTextFill(Paint.valueOf("WHITE"));
        content.setFont(Font.font(19));
        VBox vb = new VBox();
        vb.setAlignment(Pos.valueOf("TOP_CENTER"));
        vb.setPrefHeight(82);
        vb.getChildren().addAll(username,content);
        if (mess.from == user.getId()){
            username.setAlignment(Pos.valueOf("TOP_RIGHT"));
            content.setAlignment(Pos.valueOf("TOP_RIGHT"));
            hb.getChildren().add(vb);
            hb.getChildren().add(avt);
        }
        else {
            username.setAlignment(Pos.valueOf("TOP_LEFT"));
            content.setAlignment(Pos.valueOf("TOP_LEFT"));
            hb.getChildren().add(avt);
            hb.getChildren().add(vb);
        }
        messGroup.getChildren().add(hb);
        nextPosMess+=68;
    }
}
