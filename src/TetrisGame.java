import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class TetrisGame extends JFrame {
    public static final int ROWS = 60;
    public static final int COLS = 10;
    public static final int CELL_SIZE = 15;
    
    private GamePanel gamePanel;
    private int[][] board = new int[ROWS][COLS];
    private Piece currentPiece;
    private Timer timer;
    private int score = 0;
    private boolean gameOver = false;
    
    public TetrisGame() {
        setTitle("羅斯方塊");
        setSize(COLS * CELL_SIZE + 16, ROWS * CELL_SIZE + 39);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        gamePanel = new GamePanel();
        add(gamePanel);
        
        addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e) {
                if(gameOver) return;
                int key = e.getKeyCode();
                if(key == KeyEvent.VK_LEFT) {
                    movePiece(-1);
                } else if(key == KeyEvent.VK_RIGHT) {
                    movePiece(1);
                } else if(key == KeyEvent.VK_DOWN) {
                    movePieceDown();
                } else if(key == KeyEvent.VK_UP) {
                    rotatePieceClockwise();
                } else if(key == KeyEvent.VK_Z) {
                    rotatePieceCounterClockwise();
                }
                gamePanel.repaint();
            }
        });
        
        spawnPiece();
        
        timer = new Timer(500, new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if(!gameOver) {
                    movePieceDown();
                    gamePanel.repaint();
                }
            }
        });
        timer.start();
    }
    
    private void movePiece(int dx) {
        if(currentPiece == null) return;
        if(!collision(currentPiece, 0, dx)) {
            currentPiece.col += dx;
        }
    }
    
    private void movePieceDown() {
        if(currentPiece == null) return;
        if(!collision(currentPiece, 1, 0)) {
            currentPiece.row += 1;
        } else {
            addPieceToBoard(currentPiece);
            clearFullRows();
            spawnPiece();
        }
    }
    
    private void rotatePieceClockwise() {
        if(currentPiece == null) return;
        Piece rotated = currentPiece.getRotated(true);
        if(!collision(rotated, 0, 0)) {
            currentPiece = rotated;
        }
    }
    
    private void rotatePieceCounterClockwise() {
        if(currentPiece == null) return;
        Piece rotated = currentPiece.getRotated(false);
        if(!collision(rotated, 0, 0)) {
            currentPiece = rotated;
        }
    }
    
    private boolean collision(Piece piece, int deltaRow, int deltaCol) {
        for(Point p : piece.getBlocks()) {
            int newRow = piece.row + p.x + deltaRow;
            int newCol = piece.col + p.y + deltaCol;
            if(newRow < 0 || newRow >= ROWS || newCol < 0 || newCol >= COLS) {
                return true;
            }
            if(board[newRow][newCol] != 0) {
                return true;
            }
        }
        return false;
    }
    
    private void addPieceToBoard(Piece piece) {
        for(Point p : piece.getBlocks()) {
            int r = piece.row + p.x;
            int c = piece.col + p.y;
            if(r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                board[r][c] = 1;
            }
        }
    }
    
    private void clearFullRows() {
        for(int r = ROWS - 1; r >= 0; r--) {
            boolean full = true;
            for(int c = 0; c < COLS; c++) {
                if(board[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            if(full) {
                for(int c = 0; c < COLS; c++) {
                    board[r][c] = 0;
                }
                for(int i = r; i > 0; i--) {
                    board[i] = Arrays.copyOf(board[i-1], COLS);
                }
                Arrays.fill(board[0], 0);
                score++;
                r++; 
            }
        }
    }
    
    private void spawnPiece() {
        Random rand = new Random();
        int type = rand.nextInt(2);
        if(type == 0) {
            currentPiece = new Piece(0, COLS / 2 - 1, PieceType.SQUARE);
        } else {
            currentPiece = new Piece(0, COLS / 2 - 1, PieceType.BAR_HORIZONTAL);
        }
        if(collision(currentPiece, 0, 0)) {
            gameOver = true;
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!\nScore: " + score);
        }
    }
    
    class GamePanel extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for(int r = 0; r < ROWS; r++) {
                for(int c = 0; c < COLS; c++) {
                    if(board[r][c] != 0) {
                        g.setColor(Color.BLUE);
                        g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    } else {
                        g.setColor(Color.LIGHT_GRAY);
                        g.drawRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
            if(currentPiece != null) {
                g.setColor(Color.RED);
                for(Point p : currentPiece.getBlocks()) {
                    int r = currentPiece.row + p.x;
                    int c = currentPiece.col + p.y;
                    g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
            g.setColor(Color.BLACK);
            g.drawString("Score: " + score, 10, 20);
        }
    }
    
    enum PieceType {
        SQUARE,        
        BAR_HORIZONTAL, 
        BAR_VERTICAL    
    }
    
    class Piece {
        int row, col;  
        PieceType type;
        
        public Piece(int row, int col, PieceType type) {
            this.row = row;
            this.col = col;
            this.type = type;
        }
        
        public List<Point> getBlocks() {
            List<Point> blocks = new ArrayList<>();
            if(type == PieceType.SQUARE) {
                blocks.add(new Point(0, 0));
                blocks.add(new Point(0, 1));
                blocks.add(new Point(1, 0));
                blocks.add(new Point(1, 1));
            } else if(type == PieceType.BAR_HORIZONTAL) {
                blocks.add(new Point(0, -1));
                blocks.add(new Point(0, 0));
                blocks.add(new Point(0, 1));
                blocks.add(new Point(0, 2));
            } else if(type == PieceType.BAR_VERTICAL) {
                blocks.add(new Point(-1, 0));
                blocks.add(new Point(0, 0));
                blocks.add(new Point(1, 0));
                blocks.add(new Point(2, 0));
            }
            return blocks;
        }
        
        public Piece getRotated(boolean clockwise) {
            if(this.type == PieceType.SQUARE) {
                return new Piece(this.row, this.col, this.type);
            } else if(this.type == PieceType.BAR_HORIZONTAL) {
                return new Piece(this.row, this.col, PieceType.BAR_VERTICAL);
            } else if(this.type == PieceType.BAR_VERTICAL) {
                return new Piece(this.row, this.col, PieceType.BAR_HORIZONTAL);
            }
            return this;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TetrisGame().setVisible(true);
        });
    }
}
