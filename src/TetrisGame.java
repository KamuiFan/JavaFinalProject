import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TetrisGame extends JFrame {
    public static final int ROWS = 20;
    public static final int COLS = 10;
    public static final int CELL_SIZE = 30;

    private GamePanel gamePanel;
    private Color[][] board = new Color[ROWS][COLS];
    private Tetromino currentPiece;
    private javax.swing.Timer timer; // Explicitly using javax.swing.Timer
    private int score = 0;
    private boolean gameOver = false;
    private Tetromino nextPiece;
    
    private Tetromino generateRandomPiece() {
    Tetromino[] pieces = new Tetromino[] {
        new IShape(0, 0, false),
        new OShape(0, 0),
        new TShape(0, 0, 0),
        new SShape(0, 0, false),
        new ZShape(0, 0, false),
        new JShape(0, 0, 0),
        new LShape(0, 0, 0)
    };
    return pieces[new Random().nextInt(pieces.length)];
   }


    public TetrisGame() {
        setTitle("Tetris");
        setSize(COLS * CELL_SIZE + 16, ROWS * CELL_SIZE + 39);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        JPanel rightPanel = new JPanel() {
           protected void paintComponent(Graphics g) {
               super.paintComponent(g);
               if (nextPiece != null) {
                  g.setColor(nextPiece.getColor());
                  for (Point p : nextPiece.getBlocks()) {
                     int px = (p.y + 1) * CELL_SIZE;
                     int py = (p.x + 1) * CELL_SIZE;
                     g.fillRect(px, py, CELL_SIZE, CELL_SIZE);
                  }
               }
            }
         };
         rightPanel.setPreferredSize(new Dimension(5 * CELL_SIZE, 5 * CELL_SIZE));

         setLayout(new BorderLayout());
         add(gamePanel, BorderLayout.CENTER);
         add(rightPanel, BorderLayout.EAST);


        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (gameOver) return;
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) movePiece(-1);
                else if (key == KeyEvent.VK_RIGHT) movePiece(1);
                else if (key == KeyEvent.VK_DOWN) movePieceDown();
                else if (key == KeyEvent.VK_UP) rotatePiece();
                gamePanel.repaint();
            }
        });

        spawnPiece();

        // Timer to move the piece down at a regular interval
        timer = new javax.swing.Timer(500, e -> {
            if (!gameOver) {
                movePieceDown();
                gamePanel.repaint();
                repaint();
            }
        });

        timer.start();
    }

    private void movePiece(int dx) {
        if (currentPiece == null) return;
        if (!collision(currentPiece, 0, dx)) currentPiece.col += dx;
    }

    private void movePieceDown() {
        if (currentPiece == null) return;
        if (!collision(currentPiece, 1, 0)) currentPiece.row++;
        else {
            addPieceToBoard(currentPiece);
            clearFullRows();
            spawnPiece();
        }
    }

    private void rotatePiece() {
        Tetromino rotated = currentPiece.getRotated();
        if (!collision(rotated, 0, 0)) currentPiece = rotated;
    }

    private boolean collision(Tetromino piece, int dRow, int dCol) {
        for (Point p : piece.getBlocks()) {
            int newRow = piece.row + p.x + dRow;
            int newCol = piece.col + p.y + dCol;
            if (newRow < 0 || newRow >= ROWS || newCol < 0 || newCol >= COLS) return true;
            if (board[newRow][newCol] != null) return true;
        }
        return false;
    }

    private void addPieceToBoard(Tetromino piece) {
        for (Point p : piece.getBlocks()) {
            int r = piece.row + p.x;
            int c = piece.col + p.y;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS) board[r][c] = piece.getColor();
        }
    }

    private void clearFullRows() {
    for (int r = ROWS - 1; r >= 0; r--) {
        boolean full = true;
        for (int c = 0; c < COLS; c++) {
            if (board[r][c] == null) {
                full = false;
                break;
            }
        }
        if (full) {
            for (int i = r; i > 0; i--) {
                board[i] = Arrays.copyOf(board[i - 1], COLS);
            }
            Arrays.fill(board[0], null);
            score++;
            r++; // 檢查新的一列
        }
    }
}


    private void spawnPiece() {
    if (nextPiece == null) {
        nextPiece = generateRandomPiece();
    }
    currentPiece = nextPiece;
    currentPiece.row = 0;
    currentPiece.col = COLS / 2;

    nextPiece = generateRandomPiece(); // 預先準備下一個

    if (collision(currentPiece, 0, 0)) {
        gameOver = true;
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over!\nScore: " + score);
    }
   }


    class GamePanel extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (board[r][c] != null) {
                        g.setColor(board[r][c]);
                        g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                     } else {
                        g.setColor(Color.LIGHT_GRAY);
                        g.drawRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                     }

                }
            }
            if (currentPiece != null) {
                g.setColor(currentPiece.getColor());
                for (Point p : currentPiece.getBlocks()) {
                    int r = currentPiece.row + p.x;
                    int c = currentPiece.col + p.y;
                    g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
            g.setColor(Color.BLACK);
            g.drawString("Score: " + score, 10, 20);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TetrisGame().setVisible(true));
    }
}
