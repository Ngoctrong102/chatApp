package client.controller;

import client.ClientSocket;
import client.MainClient;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.sun.security.ntlm.Client;
import data.serialize.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
    private Label id;
    @FXML
    private JFXTextArea msg = new JFXTextArea();
    @FXML
    private ScrollPane scrollMess = new ScrollPane();
    @FXML
    private HBox hbCur = null;
    @FXML
    private BorderPane chatframe;
    @FXML
    private JFXButton btnMem;

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    public void setData(ClientSocket cli, User user) {
        this.client = cli;
        this.user = user;
    }
    public void loadView(){
        roomCur = user.getRooms().size() > 0? roomCur = user.getRooms().get(0).roomID:0;
        loadViewUser();
        //renderRoom();
        chatframe.setVisible(false);
    }

    public void getFriends(ActionEvent actionEvent) {
        try {
            client.requestFriends(new RequestFriend(user.getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void renderRoom() {
        btnMem.setVisible(true);
        friendList.getChildren().clear();
        int i = 0;
        for (RoomInfo roomInfo: user.getRooms()){
            HBox hb = new HBox();
            if (roomInfo.roomID==roomCur) {
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
            hb.setStyle( roomInfo.roomID == roomCur ? "-fx-background-color: #393C43;" : "");
            hb.getProperties().put("class","active");
            int finalI = i;
            hb.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    hbCur.setStyle("");
                    hb.setStyle("-fx-background-color: #393C43;");
                    hbCur = hb;
                    user.getRooms().get(finalI).hasNewMess = false;
                    if (hbCur.getChildren().get(hbCur.getChildren().size()-1) instanceof Pane){
                        hbCur.getChildren().remove(hbCur.getChildren().size()-1);
                    }
                    changeRoom(roomInfo.roomID,roomInfo.roomName);
                }
            });
            File file = new File("src/client/image/groupavatar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt= new ImageView(image);
            avt.setFitHeight(63);
            avt.setFitWidth(70);
            avt.setPickOnBounds(true);
            avt.setPreserveRatio(true);
            Label username = new Label(roomInfo.roomName);
            username.setPrefHeight(38);
            username.setPrefWidth(172);
            username.setTextFill(Paint.valueOf("WHITE"));
            username.setFont(Font.font(22));
            hb.getChildren().addAll(avt,username);
            if (roomInfo.hasNewMess){
                File filejoin = new File("src/client/image/newmess.png");
                Image imagejoin = new Image(filejoin.toURI().toString());
                ImageView ivjoin = new ImageView(imagejoin);
                ivjoin.setFitWidth(30);
                ivjoin.setFitHeight(28);
                ivjoin.setPickOnBounds(true);
                ivjoin.setPreserveRatio(true);
                Pane pane = new Pane();
                pane.getChildren().add(ivjoin);
                hb.getChildren().add(pane);
            }
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
        id.setText("#"+ user.getId());
    }

    public void handlebutton(ActionEvent actionEvent) {

    }

    public void renderFriend(ResponseFriend res) {
        friendList.getChildren().clear();
        int i = 0;
        for (ResponseFriend.FriendInfo fi: res.listFriend){
            HBox hb = new HBox();
            System.out.println(hb.getId());
            hb.setAlignment(Pos.CENTER_LEFT);
            hb.setLayoutX(-3);
            hb.setLayoutY(i*68);
            hb.setPrefHeight(66);
            hb.setPrefWidth(270);
            hb.getProperties().put("class","active");
            File file = new File("src/client/image/avartar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt= new ImageView(image);
            avt.setFitHeight(63);
            avt.setFitWidth(70);
            avt.setPickOnBounds(true);
            avt.setPreserveRatio(true);
            Label username = new Label(fi.username);
            username.setPrefHeight(38);
            username.setPrefWidth(172);
            username.setTextFill(Paint.valueOf("WHITE"));
            username.setFont(Font.font(22));
            hb.getChildren().addAll(avt,username);
            if (true){
                File filejoin = new File("src/client/image/btnonline.png");
                Image imagejoin = new Image(filejoin.toURI().toString());
                ImageView ivjoin = new ImageView(imagejoin);
                ivjoin.setFitWidth(30);
                ivjoin.setFitHeight(28);
                ivjoin.setPickOnBounds(true);
                ivjoin.setPreserveRatio(true);
                hb.getChildren().add(ivjoin);
            }
            friendList.getChildren().add(hb);
            i++;
        }
    }

    public void renderMess(ResponseMess res){
        chatframe.setVisible(true);
        messGroup.getChildren().clear();
        nextPosMess = 0;
        for (Message mess: res.arrayListMess){
            HBox hb = new HBox();
            hb.setLayoutY(nextPosMess);
            hb.setPrefHeight(76);
            hb.setPrefWidth(785);
            File file = new File("src/client/image/avartar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt= new ImageView(image);
            avt.setFitWidth(68);
            avt.setFitHeight(63);
            Label username = new Label((String.valueOf(mess.from)));
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
            vb.setPrefWidth(730);
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
        chatframe.setVisible(false);
        friendList.getChildren().clear();
        renderRoom();
    }

    public void Sendmsg() {
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

    public void sendMsgKeyEvent(KeyEvent event) throws IOException {
        if(event.getCode() == KeyCode.ENTER) {
            Sendmsg();
        }
    }

    public void renderNewMess(Message mess) {
        if (mess.roomID == roomCur) {
            HBox hb = new HBox();
            hb.setLayoutY(nextPosMess);
            hb.setPrefHeight(76);
            hb.setPrefWidth(785);
            File file = new File("src/client/image/avartar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt = new ImageView(image);
            avt.setFitWidth(68);
            avt.setFitHeight(63);
            Label username = new Label((String.valueOf(mess.from)));
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
            vb.setPrefWidth(730);
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
            for (RoomInfo roomInfo: user.getRooms()){
                if (roomInfo.roomID == mess.roomID){
                    roomInfo.hasNewMess = true;
                }
            }
            renderRoom();
        }

    }

    public void renderNewFile(FileMess fileRender) {
        if (fileRender.roomID == roomCur) {
            HBox hb = new HBox();
            hb.setLayoutY(nextPosMess);
            hb.setPrefHeight(76);
            hb.setPrefWidth(773);
            File file = new File("src/client/image/avartar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt = new ImageView(image);
            Label username = new Label("User " + fileRender.from);
            username.setTextFill(Paint.valueOf("WHITE"));
            username.setFont(Font.font(22));
            username.setPrefHeight(38);
            username.setPrefWidth(713);
            Label content = new Label(fileRender.fileName);

            content.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton() == MouseButton.PRIMARY) {     // add action show image
                        if (fileRender.fileName.contains(".png") || fileRender.fileName.contains(".jpeg") ||
                                fileRender.fileName.contains(".jpg") ) {
                            byte[] imageInByte = fileRender.fileData;
                            BufferedImage img1 = null;
                            try {
                                img1 = ImageIO.read(new ByteArrayInputStream(imageInByte));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Image image = SwingFXUtils.toFXImage(img1, null);
                            ImageView imageView = new ImageView(image);
                            imageView.setFitHeight(1500);
                            imageView.setFitWidth(1500);
                            imageView.setPreserveRatio(true);

                            // Create display
                            GridPane pane = new GridPane();
                            pane.getChildren().addAll(imageView);
                            Scene picture = new Scene(pane, 1500, 800);

                            Stage window = new Stage();
                            window.setScene(picture);
                            window.setTitle(fileRender.fileName);
                            window.show();
                        }

                        event.consume();

                    } else if (event.getButton() == MouseButton.SECONDARY) { // add action save file to location
                        FileChooser ch = new FileChooser();
                        ch.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
                        ch.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpg"));
                        ch.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
                        ch.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt", "*.txt"));
                        ch.getExtensionFilters().add(new FileChooser.ExtensionFilter("doc", "*.doc"));
                        File file = ch.showSaveDialog(null);

                        if (file != null) {
                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                out.write(fileRender.fileData);
                                out.close();
                            } catch (Exception e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error Dialog");
                                alert.setContentText("Oops, File not found!");
                                alert.showAndWait();
                            }

                        }
                    }
                }
            });

            content.setContentDisplay(ContentDisplay.valueOf("CENTER"));
            content.setPrefWidth(716);
            content.setPrefHeight(59);
            content.setTextFill(Paint.valueOf("WHITE"));
            content.setFont(Font.font(19));
            VBox vb = new VBox();
            vb.setAlignment(Pos.valueOf("TOP_CENTER"));
            vb.setPrefHeight(82);
            vb.getChildren().addAll(username, content);
            if (fileRender.from == user.getId()) {
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
            for (RoomInfo roomInfo: user.getRooms()){
                if (roomInfo.roomID == fileRender.roomID){
                    roomInfo.hasNewMess = true;
                }
            }
            renderRoom();
        }
    }

    public void createRoom() {
        client.creatRoom(new RequestCreateNewRoom(txtroomName.getText(),user.getId()));
        txtroomName.setText("");
        System.out.println("tạo phòng mới");
    }

    public void createRoomKeyEvent(KeyEvent event) throws IOException {
        if(event.getCode() == KeyCode.ENTER) {
            createRoom();
        }
    }


    public void listRoomToJoin(ActionEvent actionEvent) {
        chatframe.setVisible(false);
        client.requestRoomToJoin();
    }

    public void renderRoomToJoin(ResponseListRoom res) {
        btnMem.setVisible(false);
        friendList.getChildren().clear();
        int i = 0;
        for (RoomInfo roomInfo: res.roomInfos){
            HBox hb = new HBox();
            hb.setAlignment(Pos.CENTER_LEFT);
            hb.setLayoutX(2);
            hb.setLayoutY(i*68);
            hb.setPrefHeight(66);
            hb.setPrefWidth(270);
            hb.setStyle( i==0 ? "-fx-background-color: #393C43;" : "");
            hb.getProperties().put("class","active");
            File file = new File("src/client/image/groupavatar.png");
            Image image = new Image(file.toURI().toString());
            ImageView avt= new ImageView(image);
            avt.setFitHeight(63);
            avt.setFitWidth(70);
            avt.setPickOnBounds(true);
            avt.setPreserveRatio(true);
            Label username = new Label(roomInfo.roomName);
            username.setPrefHeight(38);
            username.setPrefWidth(172);
            username.setTextFill(Paint.valueOf("WHITE"));
            username.setFont(Font.font(22));
            File filejoin = new File("src/client/image/joingroup.png");
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

    public void viewListMem(ActionEvent actionEvent) {
        System.out.println("view list mem");
        client.memOf(roomCur);
    }

    @FXML
    void Logout(ActionEvent event) throws IOException {
        RequestLogout requestlogout = new RequestLogout(this.user);
        try {
            client.objectOutputStream.writeObject(requestlogout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainClient.showLoginView();
    }
}
