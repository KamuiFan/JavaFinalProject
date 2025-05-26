 # 2025 Java俄羅斯方塊
- 學號：B11207034
- 姓名：范雲翔
- 工作說明：整體遊戲製作
    - 方塊墜落、旋轉
    - 鬼影
    - 消除
    - 預覽窗格
    - 分數機制
    - 難度機制
  ## 遊戲說明
- **共7種方塊**
- **加入鬼影**
- 操作說明：
1. 移動方塊：`←`、`→`
2. 旋轉方塊：`↑`
3. 軟降：`↓`
4. 硬降：`space`
5. 暫停/繼續：`P`
- 下墜速度機制：每消除10行方塊，提升一個等級。
- 分數機制：每消除1行→(當前等級+1)*40分  
           每消除2行→(當前等級+1)*100分  
           每消除3行→(當前等級+1)*300分  
           每消除4行→(當前等級+1)*1200分  
## 執行檔說明
點擊執行TetrisGame.jar即可開始遊玩
## 影片、簡報
[影片連結](https://youtu.be/cxrXp0DB0IQ)  
[簡報連結](https://www.canva.com/design/DAGoenGEhiU/hIwzYMSE3-XANpvIaQGJ2Q/edit?utm_content=DAGoenGEhiU&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)
# 流程圖
```mermaid
flowchart TD
    A["main()"] --> B["建立 TetrisGame 視窗"]
    B --> C["初始化遊戲資料與介面"]
    C --> D["啟動 timer 與背景音樂"]
    D --> E["等待玩家操作"]

    E -->|"鍵盤事件"| F{"按鍵類型"}
    F -->|"DOWN"| G["movePieceDown()"]
    F -->|"LEFT/RIGHT"| H["movePiece(-1/1)"]
    F -->|"UP"| I["rotatePiece()"]
    F -->|"SPACE"| J["快速下落"]
    F -->|"P"| K["暫停/繼續"]
    F -->|"其他"| E

    G --> L{"是否碰撞"}
    L -->|"否"| M["方塊下移"]
    L -->|"是"| N["addPieceToBoard()"]
    N --> O["clearFullRows()"]
    O --> P{"有消行?"}
    P -->|"有"| Q["加分/升級/播放音效"]
    P -->|"無"| R["生成新方塊"]
    Q --> R
    R --> S{"遊戲結束?"}
    S -->|"否"| E
    S -->|"是"| T["停止 timer 與音樂"]
    T --> U["顯示 Game Over 視窗"]
    U -->|"Restart"| C
    U -->|"結束"| V["程式結束"]

    H --> E
    I --> E
    J --> E
    K --> E

```
# UML類別圖
```mermaid
classDiagram
    class TetrisGame {
        - GamePanel gamePanel
        - Color[][] board
        - Tetromino currentPiece
        - javax.swing.Timer timer
        - int score
        - boolean gameOver
        - Tetromino nextPiece
        - NextPanel nextPanel
        - boolean paused
        - int linesCleared
        - int level
        - int baseDelay
        - int delayStep
        - List~Tetromino~ pieceBag
        - javax.swing.Timer longPressTimer
        - boolean longPressSoundPlayed
        - int longPressThreshold
        - boolean pendingSpawn
        - Preferences prefs
        - int highScore
        - int highestLevel
        + TetrisGame()
        + main(String[] args)
        - repaintAll()
        - calculateDelay()
        - movePiece(int dx)
        - boolean movePieceDown()
        - rotatePiece()
        - Tetromino getGhostPiece(Tetromino piece)
        - boolean collision(Tetromino piece, int dRow, int dCol)
        - addPieceToBoard(Tetromino piece)
        - clearFullRows()
        - spawnPiece()
        - Tetromino getNextPieceFromBag()
        - restartGame()
    }

    class NextPanel {
        + NextPanel()
        + paintComponent(Graphics g)
    }

    TetrisGame "1" *-- "1" NextPanel
    TetrisGame "1" *-- "1" GamePanel
    TetrisGame "1" o-- "1" Tetromino
    TetrisGame "1" o-- "1" Preferences
    TetrisGame "1" o-- "1" javax.swing.Timer

    class Tetromino {
        +int row
        +int col
        +Color color
        +getBlocks() List~Point~
        +getColor() Color
        +getRotated() Tetromino
        +copy() Tetromino
    }

    class IShape {
        +IShape(int row, int col, boolean vertical)
    }
    class OShape {
        +OShape(int row, int col)
    }
    class TShape {
        +TShape(int row, int col, int rotation)
    }
    class SShape {
        +SShape(int row, int col, boolean vertical)
    }
    class ZShape {
        +ZShape(int row, int col, boolean vertical)
    }
    class JShape {
        +JShape(int row, int col, int rotation)
    }
    class LShape {
        +LShape(int row, int col, int rotation)
    }

    Tetromino <|-- IShape
    Tetromino <|-- OShape
    Tetromino <|-- TShape
    Tetromino <|-- SShape
    Tetromino <|-- ZShape
    Tetromino <|-- JShape
    Tetromino <|-- LShape

    class GamePanel
    class Preferences
    class javax.swing.Timer
```
# 時序圖
```mermaid
sequenceDiagram
    participant User
    participant TetrisGame
    participant Timer
    participant SoundManager
    participant GamePanel
    participant NextPanel

    User->>TetrisGame: main()
    TetrisGame->>TetrisGame: 初始化資料、UI、計時器
    TetrisGame->>SoundManager: playBackgroundMusic("/Sound Effects/bgm.wav")
    TetrisGame->>Timer: start()

    loop 遊戲進行中
        Timer->>TetrisGame: timer 觸發 (movePieceDown)
        TetrisGame->>TetrisGame: movePieceDown()
        alt 無碰撞
            TetrisGame->>TetrisGame: currentPiece.row++
        else 有碰撞
            TetrisGame->>TetrisGame: addPieceToBoard()
            TetrisGame->>SoundManager: playSoundEffect("/Sound Effects/piece_landed.wav")
            TetrisGame->>TetrisGame: clearFullRows()
            alt 有消行
                TetrisGame->>SoundManager: playSoundEffect("/Sound Effects/line_clear.wav")
            end
            TetrisGame->>TetrisGame: spawnPiece()
            alt 遊戲結束
                TetrisGame->>SoundManager: stopBackgroundMusic()
                TetrisGame->>SoundManager: playSoundEffect("/Sound Effects/death.wav")
            end
        end
        TetrisGame->>GamePanel: repaint()
        TetrisGame->>NextPanel: repaint()
    end

    User->>TetrisGame: 按鍵 (上下左右/空白/P)
    TetrisGame->>TetrisGame: 處理鍵盤事件
    alt 需要音效
        TetrisGame->>SoundManager: playSoundEffect(...)
    end
    TetrisGame->>GamePanel: repaint()
    TetrisGame->>NextPanel: repaint()
```
