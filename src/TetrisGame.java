
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.prefs.Preferences;
import javax.swing.*;

public class TetrisGame extends JFrame {

    public static final int ROWS = 20;
    public static final int COLS = 10;
    public static final int CELL_SIZE = 30;

    private GamePanel gamePanel;
    public Color[][] board = new Color[ROWS][COLS];
    public  Tetromino currentPiece;
    private javax.swing.Timer timer;
    public  int score = 0;
    private boolean gameOver = false;
    private Tetromino nextPiece;
    private NextPanel nextPanel;
    private boolean paused = false;
    public  int linesCleared = 0;             // 累計消除行數
    public  int level = 0;                    // 目前等級
    private final int baseDelay = 500;        // Level 1 時的初始 delay
    private final int delayStep = 50;         // 每升一級，delay 減少多少毫秒
    private List<Tetromino> pieceBag = new ArrayList<>();
    // 長按聲效控制
    private javax.swing.Timer longPressTimer = null;
    private boolean longPressSoundPlayed = false;
    private final int longPressThreshold = 500;  // 長按判定時間 (ms)

    private boolean pendingSpawn = false;
    private Preferences prefs = Preferences.userNodeForPackage(TetrisGame.class);
    private int highScore = 0;
    private int highestLevel = 0;
    

    public TetrisGame() {
        setTitle("Tetris");
        setSize(COLS * CELL_SIZE + 16 + 6 * CELL_SIZE, ROWS * CELL_SIZE + 39);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        highScore = prefs.getInt("high_score", 0);
        highestLevel = prefs.getInt("highest_level", 0);
        gamePanel = new GamePanel(this);
        nextPanel = new NextPanel();
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(nextPanel, BorderLayout.EAST);
        add(mainPanel);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver) {
                    return;
                }
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_P) {
                    paused = !paused;
                    if (paused) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                    repaintAll();
                    return;
                }
                if (paused) {
                    return;
                }

                if (key == KeyEvent.VK_DOWN) {
                    // 每次按下，立即下移一格
                    movePieceDown();
                    // 長按聲效偵測
                    if (!longPressSoundPlayed && longPressTimer == null) {
                        longPressTimer = new javax.swing.Timer(longPressThreshold, ev -> {
                            SoundManager.playSoundEffect("Sound Effects/player_sending_blocks.wav");
                            longPressSoundPlayed = true;
                            longPressTimer.stop();
                            longPressTimer = null;
                        });
                        longPressTimer.setRepeats(false);
                        longPressTimer.start();
                    }
                } else if (key == KeyEvent.VK_LEFT) {
                    movePiece(-1);
                } else if (key == KeyEvent.VK_RIGHT) {
                    movePiece(1);
                } else if (key == KeyEvent.VK_UP) {
                    rotatePiece();
                } else if (key == KeyEvent.VK_SPACE) {
                    while (movePieceDown()) {
                        // 持續向下移，直到無法再下移
                    }
                }
                repaintAll();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    // 取消長按偵測，不持續播放聲音
                    if (longPressTimer != null) {
                        longPressTimer.stop();
                        longPressTimer = null;
                    }
                    longPressSoundPlayed = false;
                }
            }
        });

        spawnPiece();
        timer = new javax.swing.Timer(calculateDelay(), e -> {
            if (gameOver || paused) {
                return;
            }

            // 如果正在等待延遲 spawn，就不要移動
            if (pendingSpawn) {
                return;
            }

            movePieceDown();
            repaintAll();
        });
        SoundManager.playBackgroundMusic("Sound Effects/bgm.wav");
        timer.start();
        
    }

    private void repaintAll() {
        gamePanel.repaint();
        nextPanel.repaint();
    }

    private int calculateDelay() {
        int d = baseDelay - (level - 1) * delayStep;
        return Math.max(50, d);   // 最低不要低於 50ms
    }

    private void movePiece(int dx) {
        if (currentPiece == null) {
            return;
        }
        if (!collision(currentPiece, 0, dx)) {
            currentPiece.col += dx;
            SoundManager.playSoundEffect("Sound Effects/move_piece.wav");
            //System.out.println("左右移動");
        }
    }

    private boolean movePieceDown() {
        if (currentPiece == null || pendingSpawn) {
            return false;
        }
        if (!collision(currentPiece, 1, 0)) {
            currentPiece.row++;
            return true;
        } else {
            addPieceToBoard(currentPiece);
            SoundManager.playSoundEffect("Sound Effects/piece_landed.wav");
            clearFullRows();

            pendingSpawn = true;  // 表示準備生成新方塊，但還沒生成

            new javax.swing.Timer(300, e2 -> {
                spawnPiece();
                pendingSpawn = false;  // 重設旗標
                gamePanel.repaint();
                nextPanel.repaint();
            }) {
                {
                    setRepeats(false);
                    start();
                }
            };

            return false;
        }
    }

    private void rotatePiece() {
        Tetromino rotated = currentPiece.getRotated();

        // 嘗試這些橫向偏移量來"踢牆"
        int[] kicks = {0, -1, 1, -2, 2};

        for (int dx : kicks) {
            rotated.col = currentPiece.col + dx;
            rotated.row = currentPiece.row;  // 確保 row 沒改
            if (!collision(rotated, 0, 0)) {
                currentPiece = rotated;
                SoundManager.playSoundEffect("Sound Effects/rotate_piece.wav");
                //System.out.println("旋轉方塊 (kick: " + dx + ")");
                return;
            }
        }

        // 所有補償都失敗，不旋轉
    }

    public Tetromino getGhostPiece(Tetromino piece) {
        Tetromino ghost = piece.copy();
        while (!collision(ghost, 1, 0)) {
            ghost.row++;
        }
        return ghost;
    }

    private boolean collision(Tetromino piece, int dRow, int dCol) {
        for (Point p : piece.getBlocks()) {
            int newRow = piece.row + p.x + dRow;
            int newCol = piece.col + p.y + dCol;
            if (newRow < 0 || newRow >= ROWS || newCol < 0 || newCol >= COLS) {
                return true;
            }
            if (board[newRow][newCol] != null) {
                return true;
            }
        }
        return false;
    }

    private void addPieceToBoard(Tetromino piece) {
        for (Point p : piece.getBlocks()) {
            int r = piece.row + p.x;
            int c = piece.col + p.y;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                board[r][c] = piece.getColor();
            }
        }
    }

    private void clearFullRows() {
        List<Integer> fullRows = new ArrayList<>();
        // 找出所有已填滿的列
        for (int r = 0; r < ROWS; r++) {
            boolean full = true;
            for (int c = 0; c < COLS; c++) {
                if (board[r][c] == null) {
                    full = false;
                    break;
                }
            }
            if (full) {
                fullRows.add(r);
            }
        }
        if (fullRows.isEmpty()) {
            return;
        }

        // 備份要消除的那幾列，用於閃爍還原
        final Map<Integer, Color[]> rowBackup = new HashMap<>();
        for (int r : fullRows) {
            rowBackup.put(r, Arrays.copyOf(board[r], COLS));
        }
        final int[] flashCount = {0};

        // 閃爍定時器
        javax.swing.Timer flashTimer = new javax.swing.Timer(100, null);
        flashTimer.addActionListener(e -> {
            // 閃爍：淘汰列交替清空／還原
            for (int r : fullRows) {
                for (int c = 0; c < COLS; c++) {
                    board[r][c] = (flashCount[0] % 2 == 0) ? null : rowBackup.get(r)[c];
                }
            }
            gamePanel.repaint();
            flashCount[0]++;

            // 閃爍結束後真正消除、下移、加分與升級
            if (flashCount[0] >= 4) {
                flashTimer.stop();

                // 真正消除：高列往下搬移
                for (int r : fullRows) {
                    for (int i = r; i > 0; i--) {
                        board[i] = Arrays.copyOf(board[i - 1], COLS);
                    }
                    Arrays.fill(board[0], null);
                }

                // 計算並加分
                switch (fullRows.size()) {
                    case 1:
                        score += 40 * (level + 1);
                        SoundManager.playSoundEffect("Sound Effects/line_clear.wav");
                        System.out.println(40 * (level + 1));
                        break;
                    case 2:
                        score += 100 * (level + 1);
                        SoundManager.playSoundEffect("Sound Effects/line_clear.wav");
                        System.out.println(100 * (level + 1));
                        break;
                    case 3:
                        score += 300 * (level + 1);
                        SoundManager.playSoundEffect("Sound Effects/line_clear.wav");
                        System.out.println(300 * (level + 1));
                        break;
                    case 4:
                        score += 1200 * (level + 1);
                        SoundManager.playSoundEffect("Sound Effects/tetris_4_lines.wav");
                        System.out.println(1200 * (level + 1));
                        break;
                }

                // **1. 累計消除行數並計算新等級**
                linesCleared += fullRows.size();
                int newLevel = (linesCleared / 10);
                if (newLevel > level) {
                    level = newLevel;
                    // **2. 更新自動下落速度**
                    timer.setDelay(calculateDelay());
                }

                gamePanel.repaint();
            }
        });
        flashTimer.setRepeats(true);
        flashTimer.start();
    }

    private void spawnPiece() {
        if (nextPiece == null) {
            nextPiece = getNextPieceFromBag();
        }
        currentPiece = nextPiece;
        currentPiece.row = 0;
        currentPiece.col = COLS / 2;
        nextPiece = getNextPieceFromBag();
        if (collision(currentPiece, 0, 0)) {
            gameOver = true;
            timer.stop();
            SoundManager.stopBackgroundMusic();
            SoundManager.playSoundEffect("Sound Effects/death.wav");
            if (score > highScore) {
                highScore = score;
                prefs.putInt("high_score", highScore);
            }

            if (level > highestLevel) {
                highestLevel = level;
                prefs.putInt("highest_level", highestLevel);
            }

            String message = String.format(
                "Game Over\nYour Score: %d\nYour Level: %d\n\nHigh Score: %d\nHighest Level: %d",
                score, level, highScore, highestLevel
            );
            JOptionPane.showMessageDialog(this, message);

            int option = JOptionPane.showConfirmDialog(this,
                    "Game Over!\nDo you want to restart?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                restartGame();
            }
        }

    }

    private Tetromino getNextPieceFromBag() {
        if (pieceBag.isEmpty()) {
            Tetromino[] all = new Tetromino[]{
                new IShape(0, 0, false), new OShape(0, 0), new TShape(0, 0, 0),
                new SShape(0, 0, false), new ZShape(0, 0, false),
                new JShape(0, 0, 0), new LShape(0, 0, 0)
            };
            pieceBag.addAll(Arrays.asList(all));
            // 洗牌
            Random rand = new Random();
            for (int i = pieceBag.size() - 1; i > 0; i--) {
                int j = rand.nextInt(i + 1);
                Tetromino temp = pieceBag.get(i);
                pieceBag.set(i, pieceBag.get(j));
                pieceBag.set(j, temp);
            }
        }
        Tetromino next = pieceBag.remove(0);
        return next;
    }

    private void restartGame() {
        // 清空遊戲資料
        for (int r = 0; r < ROWS; r++) {
            Arrays.fill(board[r], null);
        }
        score = 0;
        level = 0;
        linesCleared = 0;
        gameOver = false;
        paused = false;
        pieceBag.clear();
        nextPiece = null;
        pendingSpawn = false;

        // 重設計時器延遲與開始
        timer.setDelay(calculateDelay());
        timer.start();

        // 播放背景音樂
        SoundManager.playBackgroundMusic("Sound Effects/bgm.wav");

        // 生成新方塊
        spawnPiece();
        repaintAll();
    }

    class NextPanel extends JPanel {

        public NextPanel() {
            setPreferredSize(new Dimension(5 * CELL_SIZE, 5 * CELL_SIZE));
            setBorder(BorderFactory.createLineBorder(Color.WHITE));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(Color.WHITE);
            g.drawString("Next:", 10, 20);
            if (paused) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString("PAUSED", 10, 60);
                return;
            }
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
