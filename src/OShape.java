import java.util.Arrays;
import java.awt.Point;
import java.util.List;
import java.awt.Color;

public class OShape extends Tetromino {
    public OShape(int row, int col) {
        super(row, col);
    }

    @Override
    public List<Point> getBlocks() {
        return Arrays.asList(
            new Point(0, 0), new Point(0, 1),
            new Point(1, 0), new Point(1, 1)
        );
    }

    @Override
    public Tetromino rotateClockwise() {
        return this;  // O方塊旋轉後仍然是原來的形狀
    }

    @Override
    public Tetromino rotateCounterClockwise() {
        return this;  // 同上
    }

    @Override
    public Tetromino getRotated() {
        return this;  // O方塊旋轉後不變
    }
    
    @Override
    public Color getColor() {
        return Color.YELLOW; // O型方塊顏色設為黃色
    }
}
