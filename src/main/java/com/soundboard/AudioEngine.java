package com.soundboard;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa responsabila cu scanarea dispozitivelor audio si redarea simultana
 * a sunetelor pe doua iesiri distincte (ex: Casti + VB-Audio Virtual Cable).
 */
public class AudioEngine {

    // Volum implicit (1.0 = 100%, 0.5 = 50%, 0.0 = Mute)
    private static float primaryVolume = 1.0f;
    private static float secondaryVolume = 1.0f;

    // Lista liniilor audio active (utila pentru functia de STOP ALL)
    private static final List<SourceDataLine> activeLines = new ArrayList<>();

    /**
     * Returneaza o lista cu numele tuturor dispozitivelor audio de iesire (Output Mixers)
     */
    public static List<String> getAvailableOutputDevices() {
        List<String> devices = new ArrayList<>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

        for (Mixer.Info info : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(info);
            // Verificam daca dispozitivul suporta linii de iesire (SourceDataLine)
            Line.Info[] lineInfos = mixer.getSourceLineInfo();
            if (lineInfos.length > 0) {
                if (!devices.contains(info.getName())) {
                    devices.add(info.getName());
                }
            }
        }
        return devices;
    }

    /**
     * Reda un fisier audio (.wav) SIMULTAN pe doua dispozitive selectate.
     * Ruleaza pe un fir de executie (Thread) separat pentru a nu bloca GUI-ul.
     *
     * @param audioFilePath Calea catre fisierul .wav
     * @param primaryDeviceName Numele primului dispozitiv (ex: Casti)
     * @param secondaryDeviceName Numele celui de-al doilea dispozitiv (ex: VB-Cable)
     */
    public static void playSoundToTwoDevices(String audioFilePath, String primaryDeviceName, String secondaryDeviceName) {
        new Thread(() -> {
            File audioFile = new File(audioFilePath);
            if (!audioFile.exists()) {
                System.err.println("[AudioEngine] Fisierul nu exista: " + audioFilePath);
                return;
            }

            try (AudioInputStream stream1 = AudioSystem.getAudioInputStream(audioFile);
                 AudioInputStream stream2 = AudioSystem.getAudioInputStream(audioFile)) {

                AudioFormat format = stream1.getFormat();
                DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, format);

                // Gasim Mixerele corespunzatoare numelor selectate
                Mixer primaryMixer = getMixerByName(primaryDeviceName);
                Mixer secondaryMixer = getMixerByName(secondaryDeviceName);

                if (primaryMixer == null || secondaryMixer == null) {
                    System.err.println("[AudioEngine] Unul sau ambele dispozitive audio nu au fost gasite.");
                    return;
                }

                // Deschidem liniile de ieșire
                SourceDataLine line1 = (SourceDataLine) primaryMixer.getLine(lineInfo);
                SourceDataLine line2 = (SourceDataLine) secondaryMixer.getLine(lineInfo);

                line1.open(format);
                line2.open(format);

                // Setam volumul initial pe fiecare linie
                setLineVolume(line1, primaryVolume);
                setLineVolume(line2, secondaryVolume);

                line1.start();
                line2.start();

                // Înregistram liniile în lista de linii active (thread-safe)
                synchronized (activeLines) {
                    activeLines.add(line1);
                    activeLines.add(line2);
                }

                byte[] buffer = new byte[4096];
                int bytesRead;

                // Transmiterea pachetelor de octeti audio catre ambele ieșiri simultan
                while ((bytesRead = stream1.read(buffer)) != -1) {
                    line1.write(buffer, 0, bytesRead);
                    line2.write(buffer, 0, bytesRead);
                }

                // Așteptam finalizarea redarii
                line1.drain();
                line2.drain();

                // Închidem liniile curat
                closeLine(line1);
                closeLine(line2);

            } catch (UnsupportedAudioFileException e) {
                System.err.println("[AudioEngine] Format audio nesuportat! Asigura-te ca folosești fișiere .wav (PCM).");
            } catch (IOException | LineUnavailableException e) {
                System.err.println("[AudioEngine] Eroare la redarea audio: " + e.getMessage());
            }
        }).start();
    }

    /**
     * RF-05: Oprește instantaneu toate sunetele aflate în curs de redare.
     */
    public static void stopAllSounds() {
        synchronized (activeLines) {
            for (SourceDataLine line : activeLines) {
                if (line.isOpen()) {
                    line.stop();
                    line.flush();
                    line.close();
                }
            }
            activeLines.clear();
        }
        System.out.println("[AudioEngine] Toate sunetele au fost oprite.");
    }

    /**
     * RF-03: Setarea volumului (valoare intre 0.0 si 1.0)
     */
    public static void setPrimaryVolume(float volume) {
        primaryVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public static void setSecondaryVolume(float volume) {
        secondaryVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    // --- METODE AUXILIARE PRIVATE ---

    private static Mixer getMixerByName(String name) {
        if (name == null || name.isEmpty()) return null;
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            if (info.getName().equalsIgnoreCase(name)) {
                return AudioSystem.getMixer(info);
            }
        }
        return null;
    }

    private static void setLineVolume(SourceDataLine line, float volume) {
        if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            // Transformam valoarea liniara (0.0 - 1.0) in decibeli (dB)
            float dB = (float) (Math.log10(volume == 0 ? 0.0001 : volume) * 20.0);
            gainControl.setValue(Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB)));
        }
    }

    private static void closeLine(SourceDataLine line) {
        synchronized (activeLines) {
            activeLines.remove(line);
        }
        if (line.isOpen()) {
            line.close();
        }
    }
}