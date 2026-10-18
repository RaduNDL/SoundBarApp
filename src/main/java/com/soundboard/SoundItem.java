package com.soundboard;

public class SoundItem {
    private String id;
    private String name;
    private String filePath;
    private String hotkey; // Pentru comanda CTRL + "TASTA"

    public SoundItem(String id, String name, String filePath, String hotkey) {
        this.id = id;
        this.name = name;
        this.filePath = filePath;
        this.hotkey = hotkey;
    }
    // Getters si Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getFilePath() { return filePath; }
    public String getHotkey() { return hotkey; }

    public void setHotkey(String hotkey) { this.hotkey = hotkey; }
}
