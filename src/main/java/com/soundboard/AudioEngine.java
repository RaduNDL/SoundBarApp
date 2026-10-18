package com.soundboard;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

public class AudioEngine {

    /**
     * Scanează și afișează toate dispozitivele audio de ieșire din sistem
     */
    public static void listAudioDevices() {
        System.out.println("=== DISPOZITIVE AUDIO DETECTATE ===");
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

        for (int i = 0; i < mixerInfos.length; i++) {
            System.out.println("[" + i + "] " + mixerInfos[i].getName() + " - " + mixerInfos[i].getDescription());
        }
        System.out.println("===================================");
    }
}