package com.soundboard;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {

    private ConfigManager.AppConfigData configData;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Virtual Soundboard v1.0 - Settings & Audio Routing");

        // Încărcăm configurația
        configData = ConfigManager.loadConfig();

        // Preluăm lista dinamică de dispozitive de ieșire detectate
        List<String> availableDevices = AudioEngine.getAvailableOutputDevices();

        // --- COMPONENTE GUI PENTRU SETĂRI AUDIO ---
        Label titleLabel = new Label("⚙️ Setări Dispozitive Audio");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Dropdown Dispozitiv Primar (Căști)
        Label primaryLabel = new Label("Dispozitiv Primar (Căști / Monitorizare):");
        ComboBox<String> primaryDeviceBox = new ComboBox<>(FXCollections.observableArrayList(availableDevices));
        if (configData.settings.getPrimaryDevice() != null && availableDevices.contains(configData.settings.getPrimaryDevice())) {
            primaryDeviceBox.setValue(configData.settings.getPrimaryDevice());
        }

        // Dropdown Dispozitiv Secundar (Microfon Virtual / VB-Cable)
        Label secondaryLabel = new Label("Dispozitiv Secundar (VB-Cable / Stream Aux):");
        ComboBox<String> secondaryDeviceBox = new ComboBox<>(FXCollections.observableArrayList(availableDevices));
        if (configData.settings.getSecondaryDevice() != null && availableDevices.contains(configData.settings.getSecondaryDevice())) {
            secondaryDeviceBox.setValue(configData.settings.getSecondaryDevice());
        }

        // Buton Salvare Setări
        Button saveSettingsButton = new Button("💾 Salvează Setările Audio");
        saveSettingsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 15px;");

        saveSettingsButton.setOnAction(e -> {
            String selectedPrimary = primaryDeviceBox.getValue();
            String selectedSecondary = secondaryDeviceBox.getValue();

            if (selectedPrimary != null && selectedSecondary != null) {
                configData.settings.setPrimaryDevice(selectedPrimary);
                configData.settings.setSecondaryDevice(selectedSecondary);
                ConfigManager.saveConfig(configData.settings, configData.sounds);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Setările audio au fost salvate cu succes!");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Te rugăm să selectezi ambele dispozitive!");
                alert.show();
            }
        });

        // --- BUTOANE DE CONTROALE AUDIO (PLAY & STOP) ---
        Button playTestButton = new Button("▶ Redă Sunet Test");
        playTestButton.setStyle("-fx-padding: 8px 15px;");

        playTestButton.setOnAction(e -> {
            String pDev = primaryDeviceBox.getValue();
            String sDev = secondaryDeviceBox.getValue();

            if (pDev != null && sDev != null) {
                System.out.println("[Test] Redare pe dispozitivele selectate:");
                System.out.println("  1. " + pDev);
                System.out.println("  2. " + sDev);
                AudioEngine.playSoundToTwoDevices("sounds/test.wav", pDev, sDev);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Selectează mai întâi ambele dispozitive din meniu!");
                alert.show();
            }
        });

        // BUTONUL STOP ALL SOUNDS (PANIC BUTTON)
        Button stopButton = new Button("⏹ Stop All Sounds");
        stopButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 15px;");
        stopButton.setOnAction(e -> AudioEngine.stopAllSounds());

        // Grupăm butoanele de test și oprire pe un rând orizontal (HBox)
        HBox actionButtons = new HBox(10, playTestButton, stopButton);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(12, titleLabel, primaryLabel, primaryDeviceBox, secondaryLabel, secondaryDeviceBox, saveSettingsButton, actionButtons);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);

        Scene scene = new Scene(root, 580, 420);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
