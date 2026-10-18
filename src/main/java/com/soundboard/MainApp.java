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

import java.net.URL;
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
        Label titleLabel = new Label("⚙ Setări Dispozitive Audio");
        titleLabel.getStyleClass().add("header-label");

        // Dropdown Dispozitiv Primar (Căști)
        Label primaryLabel = new Label("Dispozitiv Primar (Căști / Monitorizare):");
        primaryLabel.getStyleClass().add("form-label");

        ComboBox<String> primaryDeviceBox = new ComboBox<>(FXCollections.observableArrayList(availableDevices));
        primaryDeviceBox.getStyleClass().add("combo-box");
        primaryDeviceBox.setPromptText("Selectează Căști...");
        primaryDeviceBox.setMaxWidth(Double.MAX_VALUE);
        if (configData.settings.getPrimaryDevice() != null && availableDevices.contains(configData.settings.getPrimaryDevice())) {
            primaryDeviceBox.setValue(configData.settings.getPrimaryDevice());
        }

        // Dropdown Dispozitiv Secundar (Microfon Virtual / VB-Cable)
        Label secondaryLabel = new Label("Dispozitiv Secundar (VB-Cable / Stream Aux):");
        secondaryLabel.getStyleClass().add("form-label");

        ComboBox<String> secondaryDeviceBox = new ComboBox<>(FXCollections.observableArrayList(availableDevices));
        secondaryDeviceBox.getStyleClass().add("combo-box");
        secondaryDeviceBox.setPromptText("Selectează VB-Cable...");
        secondaryDeviceBox.setMaxWidth(Double.MAX_VALUE);
        if (configData.settings.getSecondaryDevice() != null && availableDevices.contains(configData.settings.getSecondaryDevice())) {
            secondaryDeviceBox.setValue(configData.settings.getSecondaryDevice());
        }

        // Buton Salvare Setări
        Button saveSettingsButton = new Button("💾 Salvează Setările Audio");
        saveSettingsButton.getStyleClass().add("btn-success");

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
        playTestButton.getStyleClass().add("btn-secondary");

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
        stopButton.getStyleClass().add("btn-danger");
        stopButton.setOnAction(e -> AudioEngine.stopAllSounds());

        // Grupăm butoanele de test și oprire pe un rând orizontal (HBox)
        HBox actionButtons = new HBox(12, playTestButton, stopButton);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        // Container stilizat ca card/panel
        VBox settingsCard = new VBox(12,
                titleLabel,
                primaryLabel,
                primaryDeviceBox,
                secondaryLabel,
                secondaryDeviceBox,
                saveSettingsButton,
                actionButtons
        );
        settingsCard.getStyleClass().add("panel");
        settingsCard.setPadding(new Insets(20));

        // Container principal al ferestrei
        VBox root = new VBox(settingsCard);
        root.getStyleClass().add("main-container");
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        Scene scene = new Scene(root, 600, 480);

        // Atasare CSS stylesheet din resurse
        URL cssResource = getClass().getResource("/css/styles.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        } else {
            System.err.println("[Atenție] Nu s-a găsit fișierul /css/styles.css în resurse.");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}