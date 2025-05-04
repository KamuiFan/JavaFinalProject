import java.util.Arrays;
import java.awt.Point;
import java.util.List;
import java.awt.Color;

public class TShape extends Tetromino {
    private int rotation; // 0, 1, 2, 3

    public TShape(int row, int col, int rotation) {
        super(row, col);  // 呼叫父類別的建構子
        this.rotation = rotation % 4;
    }

    @Override
    public List<Point> getBlocks() {
        switch(rotation) {
            case 0: 
                return Arrays.asList(new Point(0, -1), new Point(0, 0), new Point(0, 1), new Point(1, 0));
            case 1: 
                return Arrays.asList(new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(0, 1));
            case 2: 
                return Arrays.asList(new Point(0, -1), new Point(0, 0), new Point(0, 1), new Point(-1, 0));
            default: 
                return Arrays.asList(new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(0, -1));
        }
    }

    @Override
    public Tetromino rotateClockwise() {
        return new TShape(row, col, rotation + 1);
    }

    @Override
    public Tetromino rotateCounterClockwise() {
        return new TShape(row, col, rotation + 3);
    }

    @Override
    public Tetromino getRotated() {
        return rotateClockwise(); // 可以選擇旋轉順時針或逆時針，這裡選擇順時針
    }
    
    @Override
    public Color getColor() {
        return Color.MAGENTA; // T型方塊顏色設為紫色
    }
}
