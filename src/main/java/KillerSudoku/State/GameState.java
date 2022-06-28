package KillerSudoku.State;

import KillerSudoku.Logic.CageFunctions;
import KillerSudoku.Logic.Utility;

import java.awt.*;
import java.io.*;

public class GameState implements Serializable { // Huidige spelstatus
    private PlayerCell[][] cells;
    public CageFunctions puzzle;
    private int width, height;
    public Point selection;
    public boolean successGenerate, usedSolutionMenuItem;

    public void init(int width, int height, int cageNum, int givenNum, float maxSec) { // Game-initialisatie
        this.width = width;
        this.height = height;

        PlayerCell[][] cells = new PlayerCell[height][width];
        CageFunctions puzzle = new CageFunctions(width, height, cageNum);
        successGenerate = puzzle.success; // Het controleert of alles met succes is gegenereerd
        if(!successGenerate)
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


    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }







}
