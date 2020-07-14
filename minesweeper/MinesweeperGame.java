package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[i].length; j++) {
                setCellValue(i, j, "");
                gameField[j][i].isOpen = false;
                gameField[j][i].isFlag = false;
            }
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        List<GameObject> nList;
        for (int i = 0; i < SIDE; i++)
            for (int j = 0; j < SIDE; j++)
                if (!gameField[i][j].isMine) {
                    nList = getNeighbors(gameField[i][j]);
                    for (GameObject go : nList)
                        if (go.isMine)
                            gameField[i][j].countMineNeighbors++;
                }
    }

    private void openTile(int x, int y){
        if(!gameField[y][x].isOpen && !isGameStopped && !gameField[y][x].isFlag){
            List<GameObject> list = new ArrayList<>();
            gameField[y][x].isOpen = true;     //открывает клетку
            countClosedTiles--;
            setCellColor(x, y, Color.GREEN);
            if(gameField[y][x].isMine){    //если в клетке мина - отоброжаем её и вызываем gameOver
                //setCellValue(x, y, MINE);
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            }else{                        //если мины нет то:
                //если количество не открытых ячеек равно количеству мин на поле
                // и последняя открытая ячейка не является миной, то  - win!!! :
                if(countClosedTiles == countMinesOnField) win();        //код должен быть именно в блоке кода "без мин" !!!!!
                score += 5;
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);   //в клетке отоброжаем число мин по близости
                if(gameField[y][x].countMineNeighbors == 0) {   //если мин 0 , то:
                    setCellValue(x, y, "");    // то отоброжаем пустую строку
                    list = getNeighbors(gameField[y][x]);   //вызываем getNeighbors  (он возвращает соседние клетки)
                    for (GameObject object : list) {                        //
                        if(!object.isOpen) openTile(object.x, object.y);    // --- если клетка не открыта - вызываем openTile(с текущими координатами)
                    }
                }else{                                                      // иначе:
                    setCellNumber(x, y, gameField[y][x].countMineNeighbors);  //в клетке отоброжаем число мин по близости
                }
            }
            setScore(score);
        }
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
        } else {
            openTile(x, y);
        }
    }

    private void markTile(int x, int y) {
        if (!gameField[y][x].isOpen && ((countFlags > 0) || gameField[y][x].isFlag) && !isGameStopped) {
            if (!gameField[y][x].isFlag) {
                gameField[y][x].isFlag = true;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.DARKGREEN);
                countFlags--;
            } else {
                gameField[y][x].isFlag = false;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.GRAY);
                countFlags++;
            }
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
        restart();
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "Game over!", Color.WHITE, 100);
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "You win!", Color.WHITE, 100);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        countFlags = countMinesOnField;
        createGame();
    }
}