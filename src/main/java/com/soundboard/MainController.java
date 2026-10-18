package com.soundboard;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class MainController {

    @FXML private ComboBox<String> keyboardCombo;
    @FXML private ComboBox<String> primaryAudioCombo;
    @FXML private ComboBox<String> secondaryAudioCombo;

    @FXML private TilePane soundPadsContainer;

    private ConfigManager.AppConfigData configData;

    @FXML
    public void initialize() {
        // 1. Load existing JSON config via ConfigManager
        configData = ConfigManager.loadConfig();

        // 2. Fetch active audio outputs from AudioEngine
        List<String> availableDevices = AudioEngine.getAvailableOutputDevices();
        primaryAudioCombo.setItems(FXCollections.observableArrayList(availableDevices));
        secondaryAudioCombo.setItems(FXCollections.observableArrayList(availableDevices));

        // Restore saved settings
        if (configData.settings.getPrimaryDevice() != null && availableDevices.contains(configData.settings.getPrimaryDevice())) {
            primaryAudioCombo.setValue(configData.settings.getPrimaryDevice());
        }
        if (configData.settings.getSecondaryDevice() != null && availableDevices.contains(configData.settings.getSecondaryDevice())) {
            secondaryAudioCombo.setValue(configData.settings.getSecondaryDevice());
        }

        // Keyboard device selection stub
        keyboardCombo.getItems().addAll("Tastatură Principală (System Default)", "Tastatură Secundară (USB Input)");
        keyboardCombo.getSelectionModel().selectFirst();

        // Populate initial demo sound pads
        populateInitialPads();
    }

    @FXML
    private void handleSaveSettings(ActionEvent event) {
        String primary = primaryAudioCombo.getValue();
        String secondary = secondaryAudioCombo.getValue();

        if (primary != null && secondary != null) {
            configData.settings.setPrimaryDevice(primary);
            configData.settings.setSecondaryDevice(secondary);
            ConfigManager.saveConfig(configData.settings, configData.sounds);

            showAlert(Alert.AlertType.INFORMATION, "Salvat", "Setările audio au fost salvate cu succes!");
        } else {
            showAlert(Alert.AlertType.WARNING, "Atenție", "Te rugăm să selectezi ambele dispozitive audio!");
        }
    }

    @FXML
    private void handlePlayTestSound(ActionEvent event) {
        String primary = primaryAudioCombo.getValue();
        String secondary = secondaryAudioCombo.getValue();

        if (primary != null && secondary != null) {
            System.out.println("[Audio Engine Test] Redare sunet pe: " + primary + " și " + secondary);
            AudioEngine.playSoundToTwoDevices("sounds/test.wav", primary, secondary);
        } else {
            showAlert(Alert.AlertType.WARNING, "Atenție", "Selectează mai întâi ambele dispozitive din meniu!");
        }
    }

    @FXML
    private void handleStopAllSounds(ActionEvent event) {
        AudioEngine.stopAllSounds();
    }

    @FXML
    private void handleAddSoundPad(ActionEvent event) {
        soundPadsContainer.getChildren().add(createSoundPadCard("Sunet Nou", "Niciun fișier", "Nespecificat"));
    }

    private void populateInitialPads() {
        soundPadsContainer.getChildren().clear();
        soundPadsContainer.getChildren().add(createSoundPadCard("Efect 1", "sounds/test.wav", "NUMPAD 1"));
        soundPadsContainer.getChildren().add(createSoundPadCard("Efect 2", "Niciun fișier", "Nespecificat"));
    }

    private VBox createSoundPadCard(String title, String defaultPath, String defaultKey) {
        VBox pad = new VBox(8);
        pad.getStyleClass().add("sound-pad");
        pad.setAlignment(Pos.CENTER);

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("pad-title");

        Label pathLbl = new Label(defaultPath);
        pathLbl.getStyleClass().add("pad-path");

        Button chooseFileBtn = new Button("📂 Alege Sunet");
        chooseFileBtn.getStyleClass().add("btn-small");
        chooseFileBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fișiere Audio", "*.wav", "*.mp3"));
            File file = chooser.showOpenDialog(pad.getScene().getWindow());
            if (file != null) {
                pathLbl.setText(file.getName());
            }
        });

        Button bindKeyBtn = new Button("⌨ Bind Key");
        bindKeyBtn.getStyleClass().add("btn-bind");

        Label keyLbl = new Label("Tastă: " + defaultKey);
        keyLbl.getStyleClass().add("pad-keybind");

        bindKeyBtn.setOnAction(e -> {
            bindKeyBtn.setText("Apasă o tastă...");
            // Interfața cu ascultătorul de la tastatură (JNativeHook)
        });

        HBox controls = new HBox(8, chooseFileBtn, bindKeyBtn);
        controls.setAlignment(Pos.CENTER);

        pad.getChildren().addAll(titleLbl, pathLbl, controls, keyLbl);
        return pad;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}