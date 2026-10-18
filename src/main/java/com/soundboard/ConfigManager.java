package com.soundboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveSounds(List<SoundItem> soundList) {
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(soundList, writer);
            System.out.println("[ConfigManager] Setările au fost salvate în " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("[ConfigManager] Eroare la salvarea setărilor: " + e.getMessage());
        }
    }

    public static List<SoundItem> loadSounds() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(CONFIG_FILE)) {
            Type listType = new TypeToken<ArrayList<SoundItem>>(){}.getType();
            List<SoundItem> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("[ConfigManager] Eroare la încărcarea setărilor: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}