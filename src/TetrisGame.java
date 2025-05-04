import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

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
    private NextPanel nextPanel;  // 用來顯示下一個方塊
    private boolean paused = false;

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
        setSize(COLS * CELL_SIZE + 16 + 6 * CELL_SIZE, ROWS * CELL_SIZE + 39);  // 增加右邊預覽區域的寬度
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        nextPanel = new NextPanel(); // 初始化下一個方塊面板

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(nextPanel, BorderLayout.EAST); // 右邊顯示下一個方塊

        add(mainPanel);

        addKeyListener(new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
              if (gameOver) return;
      
              int key = e.getKeyCode();
              if (key == KeyEvent.VK_SPACE) {
                  paused = !paused;
                  if (paused) {
                      timer.stop();
                      JOptionPane.showMessageDialog(TetrisGame.this, "遊戲已暫停，按空白鍵繼續");
                  } else {
                      timer.start();
                  }
                  return;
              }
      
              if (paused) return;
      
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
                nextPanel.repaint();  // 更新下一個方塊的顯示
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
    List<Integer> fullRows = new ArrayList<>();

    for (int r = 0; r < ROWS; r++) {
        boolean full = true;
        for (int c = 0; c < COLS; c++) {
            if (board[r][c] == null) {
                full = false;
                break;
            }
        }
        if (full) fullRows.add(r);
    }

    if (fullRows.isEmpty()) return;

    // 閃爍兩次（清空 -> 還原 -> 清空 -> 還原）
    final int[] flashCount = {0};
    final Map<Integer, Color[]> rowBackup = new HashMap<>();
    for (int r : fullRows) {
        rowBackup.put(r, Arrays.copyOf(board[r], COLS));
    }

    javax.swing.Timer flashTimer = new javax.swing.Timer(100, null);
    flashTimer.addActionListener(e -> {
        for (int r : fullRows) {
            for (int c = 0; c < COLS; c++) {
                if (flashCount[0] % 2 == 0) {
                    board[r][c] = null; // 清空
                } else {
                    board[r][c] = rowBackup.get(r)[c]; // 還原
                }
            }
        }
        gamePanel.repaint();
        flashCount[0]++;

        if (flashCount[0] >= 4) {
            flashTimer.stop();

            // 真正消除行
            for (int r : fullRows) {
                for (int i = r; i > 0; i--) {
                    board[i] = Arrays.copyOf(board[i - 1], COLS);
                }
                Arrays.fill(board[0], null);
            }

            // 加分
            switch (fullRows.size()) {
                case 1: score += 40; break;
                case 2: score += 100; break;
                case 3: score += 300; break;
                case 4: score += 1200; break;
            }

            gamePanel.repaint();
        }
    });

    flashTimer.start();
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

    // 遊戲畫面
    class GamePanel extends JPanel {
        public GamePanel() {
            setBackground(Color.BLACK); // 設置背景顏色為黑色
        }

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

            // 設定放大字體
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));  // 放大字體大小
            g.drawString("Score: " + score, 10, 20);
        }
    }

    // 下一個方塊面板
    class NextPanel extends JPanel {
        public NextPanel() {
            setPreferredSize(new Dimension(5 * CELL_SIZE, 5 * CELL_SIZE));
            setBorder(BorderFactory.createLineBorder(Color.WHITE)); // 設置邊框
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16)); // 放大字體大小
            g.drawString("Next:", 10, 20);

            if (nextPiece != null) {
                g.setColor(nextPiece.getColor());
                for (Point p : nextPiece.getBlocks()) {
                    int px = (p.y + 1) * CELL_SIZE;
                    int py = (p.x + 1) * CELL_SIZE;
                    g.fillRect(px, py, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TetrisGame().setVisible(true));
    }
}
