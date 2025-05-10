import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;
import java.util.List;

public class JShape extends Tetromino {
    private int rotation;

    public JShape(int row, int col, int rotation) {
        super(row, col);
        this.rotation = rotation % 4;
    }

    @Override
    public List<Point> getBlocks() {
        switch(rotation) {
            case 0: return Arrays.asList(new Point(0, -1), new Point(0, 0), new Point(0, 1), new Point(1, -1));
            case 1: return Arrays.asList(new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(-1, -1));
            case 2: return Arrays.asList(new Point(-1, 1), new Point(0, -1), new Point(0, 0), new Point(0, 1));
            default: return Arrays.asList(new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(1, 1));
        }
    }

    @Override
    public Tetromino rotateClockwise() {
        return new JShape(row, col, (rotation + 1) % 4);
    }

    @Override
    public Tetromino rotateCounterClockwise() {
        return new JShape(row, col, (rotation + 3) % 4);
    }

    @Override
    public Tetromino getRotated() {
        return rotateClockwise();
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

    @Override
    public Tetromino copy() {
        return new JShape(this.row, this.col, this.rotation);
    }
}
