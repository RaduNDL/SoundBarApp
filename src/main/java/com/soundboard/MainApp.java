package com.soundboard;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {

    private List<SoundItem> soundList;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Virtual Soundboard v1.0 - Full Backend Test");

        // 1. Încărcăm lista de sunete din config.json
        soundList = ConfigManager.loadSounds();

        // Dacă fișierul config.json nu există sau e gol, creăm un sunet de test implicit
        if (soundList.isEmpty()) {
            System.out.println("[Test] Se creează configurația inițială de test...");
            SoundItem defaultSound = new SoundItem("1", "Keyboard Typing Test", "sounds/test.wav", "F1");
            soundList.add(defaultSound);

            // Salvăm în config.json
            ConfigManager.saveSounds(soundList);
        } else {
            System.out.println("[Test] S-au încărcat " + soundList.size() + " sunete din config.json.");
        }

        Label titleLabel = new Label("🔊 Soundboard Test Interface & JSON Config");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Preluăm plăcile audio disponibile
        List<String> devices = AudioEngine.getAvailableOutputDevices();

        // --- BUTON TEST REDARE DIN CONFIG ---
        Button playFromConfigButton = new Button("▶ Redă primul sunet din config.json");
        playFromConfigButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        playFromConfigButton.setOnAction(e -> {
            if (!soundList.isEmpty()) {
                SoundItem itemToPlay = soundList.get(0);

                // Selectăm canalele SteelSeries Sonar active la tine pe PC
                String primaryDevice = findDeviceByName(devices, "SteelSeries Sonar - Gaming");
                String secondaryDevice = findDeviceByName(devices, "SteelSeries Sonar - Aux");

                if (primaryDevice == null && devices.size() > 0) primaryDevice = devices.get(0);
                if (secondaryDevice == null && devices.size() > 1) secondaryDevice = devices.get(1);

                System.out.println("[Test] Se reda sunetul: " + itemToPlay.getName() + " (Tastă: " + itemToPlay.getHotkey() + ")");
                AudioEngine.playSoundToTwoDevices(itemToPlay.getFilePath(), primaryDevice, secondaryDevice);
            } else {
                System.err.println("[Test] Lista de sunete este goală!");
            }
        });

        // --- BUTON STOP ALL ---
        Button stopButton = new Button("⏹ Stop All Sounds");
        stopButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-background-color: #ff4d4d; -fx-text-fill: white;");
        stopButton.setOnAction(e -> AudioEngine.stopAllSounds());

        VBox root = new VBox(15, titleLabel, playFromConfigButton, stopButton);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 500, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String findDeviceByName(List<String> devices, String keyword) {
        for (String dev : devices) {
            if (dev.toLowerCase().contains(keyword.toLowerCase())) {
                return dev;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
