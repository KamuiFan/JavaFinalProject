import java.util.Arrays;
import java.awt.Point;
import java.util.List;
import java.awt.Color;

public class IShape extends Tetromino {
    private boolean vertical;

    public IShape(int row, int col, boolean vertical) {
        super(row, col);
        this.vertical = vertical;
    }

    @Override
    public List<Point> getBlocks() {
        return vertical ? Arrays.asList(
            new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(2, 0))
            : Arrays.asList(
            new Point(0, -1), new Point(0, 0), new Point(0, 1), new Point(0, 2));
    }

    @Override
    public Tetromino rotateClockwise() {
        return new IShape(row, col, !vertical);
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
        return Color.CYAN; // I型方塊顏色設為青色
    }
}
