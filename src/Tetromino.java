import java.awt.Color;
import java.awt.Point;
import java.util.List;

public abstract class Tetromino {

    public int row, col;

    public Tetromino(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public abstract List<Point> getBlocks();

    public abstract Tetromino getRotated();

    public abstract Color getColor();

    // 加上這兩個旋轉方法讓子類可 @Override
    public abstract Tetromino rotateClockwise();

    public abstract Tetromino rotateCounterClockwise();
    
    // 加入 copy() 方法的抽象定義
    public abstract Tetromino copy();
}
