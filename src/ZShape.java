import java.util.Arrays;
import java.awt.Point;
import java.util.List;
import java.awt.Color;

public class ZShape extends Tetromino {
    private boolean vertical;

    public ZShape(int row, int col, boolean vertical) {
        super(row, col); // 呼叫父類別的建構子
        this.vertical = vertical;
    }

    @Override
    public List<Point> getBlocks() {
    return vertical
        ? Arrays.asList(new Point(-1, 1), new Point(0, 1), new Point(0, 0), new Point(1, 0))
        : Arrays.asList(new Point(0, -1), new Point(0, 0), new Point(1, 0), new Point(1, 1));
   }  


    @Override
    public Tetromino rotateClockwise() {
        return new ZShape(row, col, !vertical);
    }

    @Override
    public Tetromino rotateCounterClockwise() {
        return rotateClockwise();
    }

    @Override
    public Tetromino getRotated() {
        return rotateClockwise(); // 可以選擇順時針或逆時針旋轉，這裡選擇順時針
    }
    
    @Override
    public Color getColor() {
        return Color.RED; // T型方塊顏色設為紫色
    }
}
