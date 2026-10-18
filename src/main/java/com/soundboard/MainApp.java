package com.soundboard;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Virtual Soundboard v1.0");

        Label titleLabel = new Label("🔊 Virtual Soundboard Initialized!");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label statusLabel = new Label("Configurație gata pentru Audio Routing și Keyboard Hotkeys.");

        VBox root = new VBox(15, titleLabel, statusLabel);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 500, 350);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Afișăm în consolă plăcile audio detectate
        AudioEngine.listAudioDevices();
    }

    public static void main(String[] args) {
        launch(args);
    }
}