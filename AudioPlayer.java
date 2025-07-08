import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private static AudioPlayer bgm; // Menangani pemutaran musik latar
    private Clip audioClip; // Menggunakan Clip untuk pemutaran audio

    // Konstruktor AudioPlayer untuk memuat file audio
    public AudioPlayer(String path) {
        try {
            // Memuat file audio ke dalam Clip
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(path));
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);  // Buka dan siapkan audio untuk dimainkan
        } catch (Exception e) {
            e.printStackTrace();  // Jika ada error, tampilkan stack trace
        }
    }

    // Mainkan BGM (Background Music)
    public static void playBGM(String path, float volume) {
        stopBGM(); // Hentikan BGM yang sebelumnya berjalan
        bgm = new AudioPlayer(path); // Inisialisasi AudioPlayer dengan file audio yang dipilih
        bgm.setVolume(volume); // Set volume yang diinginkan
        bgm.playLoop(); // Mainkan audio dalam loop terus menerus
    }

    // Mengatur volume audio
    public void setVolume(float volume) {
        // Implementasi volume di sini (misalnya menggunakan gain control jika perlu)
        if (audioClip != null) {
            FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(volume); // Menyesuaikan volume audio
        }
    }

    // Mainkan audio dalam loop
    public void playLoop() {
        if (audioClip != null) {
            audioClip.loop(Clip.LOOP_CONTINUOUSLY); // Memainkan audio dalam loop tanpa berhenti
        }
    }

    // Hentikan pemutaran audio
    public void stop() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop(); // Hentikan pemutaran audio jika sedang diputar
        }
    }

    // Hentikan BGM jika ada
    public static void stopBGM() {
        if (bgm != null) {
            bgm.stop(); // Hentikan audio yang sedang berjalan
            bgm = null; // Kosongkan referensi bgm
        }
    }

    // Mengembalikan referensi BGM
    public static AudioPlayer getBGM() {
        return bgm; // Mengembalikan referensi bgm, bisa digunakan jika perlu mengaksesnya
    }
}
