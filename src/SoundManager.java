import java.io.File;
import javax.sound.sampled.*;

public class SoundManager {
    private static Clip backgroundClip;

    /** 播放背景音樂（會自動 loop） */
    public static void playBackgroundMusic(String path) {
        stopBackgroundMusic();
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(ais);
            FloatControl volume = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-20f); // 音量大小，可視需求調整
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 停止背景音樂 */
    public static void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }

    /** 播放一次性音效 */
    public static void playSoundEffect(String path) {
    new Thread(() -> {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(-10f); // 可視需求調整，單位為 dB
            }

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start(); // 開新執行緒，避免阻塞
}

}
