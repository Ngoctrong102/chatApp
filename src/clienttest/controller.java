package clienttest;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class controller {
    @FXML
    private AnchorPane rootAP = new AnchorPane();

    public void createNewBtn(ActionEvent actionEvent) {
        System.out.println("clicked");
        for (int i=0;i<10;i++) {
            HBox hb = new HBox();
            hb.setStyle("-fx-background-color: #FF0000;");
            hb.setLayoutX(30+i*10);
            hb.setLayoutY(20+i*10);
            hb.setPrefHeight(10);
            hb.setPrefWidth(10);
            int finalI = i;
            hb.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.println("bam duoc r"+Integer.toString(finalI));
                }
            });
            rootAP.getChildren().add(hb);
        }
    }
}
