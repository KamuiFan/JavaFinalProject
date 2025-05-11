
import java.io.File;
import javax.sound.sampled.*;

public class TestWav {

    public static void main(String[] args) throws Exception {
        File file = new File("Sound Effects/bgm.wav"); // 把檔名改成你的音效路徑
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
        Thread.sleep(clip.getMicrosecondLength() / 1000); // 等待播放完成
    }
}
