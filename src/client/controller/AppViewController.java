package client.controller;

import client.ClientSocket;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import data.serialize.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AppViewController {
    private ClientSocket client;
    private User user;
    private int roomCur;
    private int nextPosMess;
    private File file;

    @FXML
    public Label lblroomName;
    @FXML
    public JFXTextField txtroomName;
    @FXML
    public JFXButton addRoom;
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
    private ScrollPane scrollMess = new ScrollPane();
    @FXML
    private HBox hbCur = null;
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
    public void renderRoom() {
        friendList.getChildren().clear();
        int i = 0;
        for (RoomInfo roomInfo: user.getRooms()){
            HBox hb = new HBox();
            if (i==0) {
                hbCur = hb;
                changeRoom(roomInfo.roomID,roomInfo.roomName);
            }
            hb.setId("room"+String.valueOf(roomInfo.roomID));
            System.out.println(hb.getId());
            hb.setAlignment(Pos.CENTER_LEFT);
            hb.setLayoutX(-3);
            hb.setLayoutY(i*68);
            hb.setPrefHeight(68);
            hb.setPrefWidth(279);
            hb.setStyle( i==0 ? "-fx-background-color: #393C43;" : "");
            hb.getProperties().put("class","active");
            hb.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    hbCur.setStyle("");
                    hb.setStyle("-fx-background-color: #393C43;");
                    hbCur = hb;
                    ImageView iv = (ImageView)hbCur.lookup("#new"+roomInfo.roomID);
                    if (iv != null) hb.getChildren().remove(hb.getChildren().size()-1);
                    System.out.println("room"+String.valueOf(roomInfo.roomID));
                    changeRoom(roomInfo.roomID,roomInfo.roomName);
                }
            });
            File file = new File("src/client/image/avartar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt= new ImageView(image);
            avt.setFitHeight(63);
            avt.setFitWidth(70);
            avt.setPickOnBounds(true);
            avt.setPreserveRatio(true);
            Label username = new Label("Room "+roomInfo.roomName);
            username.setPrefHeight(38);
            username.setPrefWidth(172);
            username.setTextFill(Paint.valueOf("WHITE"));
            username.setFont(Font.font(22));
            hb.getChildren().addAll(avt,username);
            friendList.getChildren().add(hb);
            i++;
        }
    }

    public void changeRoom(int roomID, String roomName) {
        roomCur = roomID;
        client.requestMess(roomID);
        lblroomName.setText(roomName);
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
            scrollMess.setVvalue(1);
        }
    }

    public void getRooms(ActionEvent actionEvent) {
        friendList.getChildren().clear();
        renderRoom();
    }

    public void Sendmsg(ActionEvent actionEvent) {
        if (this.file != null){
            try {
                client.sendFile(new FileMess(user.getId(),roomCur,file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.file = null;
            return;
        }
        Message mess = new Message();
        mess.from = user.getId();
        mess.roomID = roomCur;
        mess.content = msg.getText();
        if (mess.content.equals("")) return;
        msg.setText("");
        try {
            client.sendMess(new RequestSendMess(mess));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renderNewMess(Message mess) {
        if (mess.roomID == roomCur) {
            HBox hb = new HBox();
            hb.setLayoutY(nextPosMess);
            hb.setPrefHeight(76);
            hb.setPrefWidth(773);
            File file = new File("src/client/image/avartar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt = new ImageView(image);
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
            vb.getChildren().addAll(username, content);
            if (mess.from == user.getId()) {
                username.setAlignment(Pos.valueOf("TOP_RIGHT"));
                content.setAlignment(Pos.valueOf("TOP_RIGHT"));
                hb.getChildren().add(vb);
                hb.getChildren().add(avt);
            } else {
                username.setAlignment(Pos.valueOf("TOP_LEFT"));
                content.setAlignment(Pos.valueOf("TOP_LEFT"));
                hb.getChildren().add(avt);
                hb.getChildren().add(vb);
            }
            messGroup.getChildren().add(hb);
            nextPosMess += 68;
            scrollMess.setVvalue(1);
        }
        else {
            File filejoin = new File("src/client/image/join.png");
            Image imagejoin = new Image(filejoin.toURI().toString());
            ImageView ivjoin = new ImageView(imagejoin);
            ivjoin.setFitWidth(30);
            ivjoin.setFitHeight(28);
            ivjoin.setPickOnBounds(true);
            ivjoin.setPreserveRatio(true);
            ivjoin.setId("#new"+mess.roomID);
            System.out.println("#room"+mess.roomID);
            for (Node node: messGroup.getChildren()){
                System.out.println(((HBox)node).getId()+"đây nè");
            }
        }
    }


    public void createRoom(ActionEvent actionEvent) {
        client.creatRoom(new RequestCreateNewRoom(txtroomName.getText(),user.getId()));
        txtroomName.setText("");
        System.out.println("tạo phòng mới");
    }

    public void viewListMem(MouseEvent mouseEvent) {

    }

    public void listRoomToJoin(ActionEvent actionEvent) {
        client.requestRoomToJoin();
    }

    public void renderRoomToJoin(ResponseListRoom res) {
        friendList.getChildren().clear();
        int i = 0;
        for (RoomInfo roomInfo: res.roomInfos){
            HBox hb = new HBox();
            hb.setAlignment(Pos.CENTER_LEFT);
            hb.setLayoutX(2);
            hb.setLayoutY(i*68);
            hb.setPrefHeight(68);
            hb.setPrefWidth(279);
            hb.setStyle( i==0 ? "-fx-background-color: #393C43;" : "");
            hb.getProperties().put("class","active");
            File file = new File("src/client/image/avartar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt= new ImageView(image);
            avt.setFitHeight(63);
            avt.setFitWidth(70);
            avt.setPickOnBounds(true);
            avt.setPreserveRatio(true);
            Label username = new Label("Room "+roomInfo.roomName);
            username.setPrefHeight(38);
            username.setPrefWidth(172);
            username.setTextFill(Paint.valueOf("WHITE"));
            username.setFont(Font.font(22));
            File filejoin = new File("src/client/image/join.png");
            Image imagejoin = new Image(filejoin.toURI().toString());
            ImageView ivjoin = new ImageView(imagejoin);
            ivjoin.setFitWidth(30);
            ivjoin.setFitHeight(28);
            ivjoin.setPickOnBounds(true);
            ivjoin.setPreserveRatio(true);
            ivjoin.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.println("join");
                    client.requestJoin(roomInfo.roomID);
                }
            });
            hb.getChildren().addAll(avt,username,ivjoin);
            friendList.getChildren().add(hb);
            i++;
        }
    }

    public void chooseFile(ActionEvent actionEvent) {
        FileChooser FC = new FileChooser();
        this.file = FC.showOpenDialog(null);
        if (this.file != null){
            System.out.println("có file rồi");
        }
    }
}
