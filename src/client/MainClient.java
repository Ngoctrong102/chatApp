package client;

import client.controller.AppViewController;
import client.controller.LoginViewController;
import data.serialize.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class MainClient extends Application {
    private static Stage primaryStage;
    private Thread wait;
    private static ClientSocket cli;
    private static AppViewController avc;
    private boolean conti = true;
    private static User user;
    public static void main(String[] args) throws IOException {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        MainClient.primaryStage = primaryStage;
        MainClient.cli = new ClientSocket();
        wait = new Thread( new Runnable() {
            @Override
            public void run() {
                while (conti){
                    System.out.println("wait");
                    Serializable res = null;
                    try {
                        res = (Serializable) MainClient.cli.objectInputStream.readObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Serializable finalRes = res;
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            MainClient.handleRespond(finalRes);
                        }
                    });
                }
            }
        });
        wait.start();
        showLoginView();
        primaryStage.show();
    }

    private static void handleRespond(Serializable res) {
        if (res instanceof User){
            try {
                user = (User)res;
                showAppView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (res instanceof ResponseFriend){
            avc.renderFriend((ResponseFriend)res);
        }
        if (res instanceof ResponseMess){
            avc.renderMess((ResponseMess)res);
        }
        if (res instanceof Message){
            avc.renderNewMess((Message)res);
        }
        if (res instanceof ResponseNewRoom){
            ResponseNewRoom newRoom = (ResponseNewRoom)res;
            user.getRooms().add(newRoom.roomInfo);
            avc.renderRoom();
            avc.changeRoom(newRoom.roomInfo.roomID,newRoom.roomInfo.roomName);
        }
        if (res instanceof ResponseListRoom){
            avc.renderRoomToJoin((ResponseListRoom)res);
        }
        if (res instanceof FileMess){
            saveFile((FileMess)res);
            avc.renderNewFile((FileMess)res);
        }
    }

    private static void saveFile(FileMess res) {
        File fileReceive = new File(user.getUsername()+ File.separator + res.fileName);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileReceive));
            bos.write(res.fileData);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showAppView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainClient.class.getResource("view/AppView.fxml"));
        Parent root = loader.load();
        File directory = new File(user.getUsername());
        if (!directory.exists()){
            directory.mkdir();
        }
        avc = loader.getController();
        avc.setData(MainClient.cli,MainClient.user);
        avc.loadView();
        primaryStage.setTitle("Chat app");
        primaryStage.setScene(new Scene(root));
    }

    private void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainClient.class.getResource("view/LoginView.fxml"));
        Parent root = loader.load();

        LoginViewController lvc = loader.getController();
        lvc.setClientSocket(MainClient.cli);
        primaryStage.setTitle("Chat app");
        primaryStage.setScene(new Scene(root));
    }


    private void logout(){

    }

    @Override
    public void stop() {
        System.out.println("Stage is closing");
        conti = false;
        try {
            cli.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
