package com.soundboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Clasă wrapper pentru stocarea ambelor informații în JSON
    public static class AppConfigData {
        public AppSettings settings = new AppSettings();
        public java.util.List<SoundItem> sounds = new ArrayList<>();
    }

    public static void saveConfig(AppSettings settings, java.util.List<SoundItem> sounds) {
        AppConfigData data = new AppConfigData();
        data.settings = settings;
        data.sounds = sounds;

        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(data, writer);
            System.out.println("[ConfigManager] Configurația a fost salvată cu succes!");
        } catch (IOException e) {
            System.err.println("[ConfigManager] Eroare la salvarea fișierului config: " + e.getMessage());
        }
    }

    public static AppConfigData loadConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            return new AppConfigData();
        }

        try (Reader reader = new FileReader(CONFIG_FILE)) {
            AppConfigData data = gson.fromJson(reader, AppConfigData.class);
            return data != null ? data : new AppConfigData();
        } catch (IOException e) {
            System.err.println("[ConfigManager] Eroare la citirea fișierului config: " + e.getMessage());
            return new AppConfigData();
        }
    }
}