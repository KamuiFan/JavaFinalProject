import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;
import java.util.List;

public class SShape extends Tetromino {
    private boolean vertical;

    public SShape(int row, int col, boolean vertical) {
        super(row, col); // 呼叫父類別的建構子
        this.vertical = vertical;
    }

    @Override
    public List<Point> getBlocks() {
        return vertical
            ? Arrays.asList(new Point(-1, 0), new Point(0, 0), new Point(0, 1), new Point(1, 1))
            : Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(1, -1), new Point(1, 0));
    }

    @Override
    public Tetromino rotateClockwise() {
        return new SShape(row, col, !vertical);
    }

    @Override
    public Tetromino rotateCounterClockwise() {
        return rotateClockwise();
    }

    @Override
    public Tetromino getRotated() {
        return rotateClockwise();
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }

    @Override
    public Tetromino copy() {
        return new SShape(row, col, vertical); // 返回當前物件的副本
    }
}
