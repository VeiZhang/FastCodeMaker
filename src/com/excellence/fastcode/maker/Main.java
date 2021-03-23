package com.excellence.fastcode.maker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load root layout from fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main.fxml"));

        Parent root = loader.load();
        primaryStage.setTitle("FastCode Maker");
        primaryStage.setScene(new Scene(root, 520, 600));
        primaryStage.setResizable(false);
        primaryStage.show();

        // relate controller with application
        Controller controller = loader.getController();
        controller.setStage(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
