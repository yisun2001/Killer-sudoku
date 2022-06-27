package KillerSudoku.GameState;

import KillerSudoku.Puzzle.Puzzle;
import KillerSudoku.Utility;

import java.awt.*;
import java.io.*;

public class GameState implements Serializable { // Huidige spelstatus
    private PlayerCell[][] cells;
    public Puzzle puzzle;
    private int width, height;
    public Point selection;
    public boolean success, usedSolutionMenuItem;

    public void init(int width, int height, int cageNum, int givenNum, float maxSec) { // Game-initialisatie
        this.width = width;
        this.height = height;

        PlayerCell[][] cells = new PlayerCell[height][width];
        Puzzle puzzle = new Puzzle(width, height, cageNum, maxSec);
        success = puzzle.success; // Het controleert of alles met succes is gegenereerd
        if(!success)
            return;
        this.cells = cells;
        this.puzzle = puzzle;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                cells[y][x] = new PlayerCell();
        selection = null;
        usedSolutionMenuItem = false;

        for(int i = 0; i < givenNum; i++){
            int x = Utility.rand(width - 1);
            int y = Utility.rand(height - 1);
            if(cells[y][x].getNum() == 0)
                cells[y][x].setNum(puzzle.getNum(x, y));
            else
                i--;
        }
    }

    public PlayerCell getCell(int x, int y){
        return cells[y][x];
    }

    public PlayerCell getSelected(){
        return cells[selection.y][selection.x];
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getCorrect(){ // Broj celija kojih je korisnik uspjesno odredio
        int correct = 0;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                if(cells[y][x].getState() == PlayerCell.State.FINALIZED)
                    if(cells[y][x].getNum() == puzzle.getNum(x, y))
                        correct++;
        return correct;
    }



    public boolean hasWon(){
        return getCorrect() == width * height;
    }


}
