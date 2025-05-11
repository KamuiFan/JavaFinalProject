
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private final TetrisGame game;

    public GamePanel(TetrisGame game) {
        this.game = game;
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 畫棋盤
        for (int r = 0; r < TetrisGame.ROWS; r++) {
            for (int c = 0; c < TetrisGame.COLS; c++) {
                if (game.board[r][c] != null) {
                    g.setColor(game.board[r][c]);
                    g.fillRect(c * TetrisGame.CELL_SIZE, r * TetrisGame.CELL_SIZE, TetrisGame.CELL_SIZE, TetrisGame.CELL_SIZE);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(c * TetrisGame.CELL_SIZE, r * TetrisGame.CELL_SIZE, TetrisGame.CELL_SIZE, TetrisGame.CELL_SIZE);
                }
            }
        }

        if (game.currentPiece != null) {
            // 畫影子
            Tetromino ghost = game.getGhostPiece(game.currentPiece);
            g.setColor(new Color(200, 200, 200, 150));
            for (Point p : ghost.getBlocks()) {
                int r = ghost.row + p.x;
                int c = ghost.col + p.y;
                g.fillRect(c * TetrisGame.CELL_SIZE, r * TetrisGame.CELL_SIZE, TetrisGame.CELL_SIZE, TetrisGame.CELL_SIZE);
            }

            // 畫目前方塊
            g.setColor(game.currentPiece.getColor());
            for (Point p : game.currentPiece.getBlocks()) {
                int r = game.currentPiece.row + p.x;
                int c = game.currentPiece.col + p.y;
                g.fillRect(c * TetrisGame.CELL_SIZE, r * TetrisGame.CELL_SIZE, TetrisGame.CELL_SIZE, TetrisGame.CELL_SIZE);
            }
        }

        // 顯示分數、等級、行數
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + game.score, 10, 20);
        g.drawString("Level: " + game.level, 10, 40);
        g.drawString("Lines: " + game.linesCleared, 10, 60);
    }
}
