package KillerSudoku.Puzzle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class FillCell { // Wordt gebruikt om cellen met getallen te vullen zodat ze de regels van gewone sudoku volgen. Het gaat niet om cages
    public int x, y, cellInd;
    private ArrayList<Byte> possibilities;

    public FillCell(Point cell, int cellInd, byte[][] cells){
        this.x = cell.x;
        this.y = cell.y;
        this.cellInd = cellInd;
        possibilities = new ArrayList<>();
        setPossibilities(cells);
        Collections.shuffle(possibilities); //Om elke keer een nieuwe configuratie van nummers te krijgen
    }

    private void setPossibilities(byte[][] cells){ // Stelt de initiële mogelijkheden in door de regels van gewone sudoku te volgen
        for(int i = 1; i <= 9; i++)
            possibilities.add((byte)i);
        int width = cells[0].length;
        int height = cells.length;
        for(int i = 0; i < width; i++)
            if(i != x)
                removePossibility(cells[y][i]);
        for(int i = 0; i < height; i++)
            if(i != y)
                removePossibility(cells[i][x]);
        Point nonet = CageFunctions.getNonet(x, y);
        for(int i = nonet.x; i < nonet.x + 3; i++){
            for(int j = nonet.y; j < nonet.y + 3; j++){
                if(i != x || j != y)
                    removePossibility(cells[j][i]);
            }
        }
    }

    private void removePossibility(byte p){ // Elimineert de mogelijkheid
        if(p == 0)
            return;
        for(int i = 0; i < possibilities.size(); i++)
            if(possibilities.get(i) == p) {
                possibilities.remove(i);
                return;
            }
    }

    public void tryNextPossibility(byte[][] cells){
        cells[y][x] = possibilities.get(0);
        possibilities.remove(0);
    }

    public boolean hasNextPossibility(){
        return possibilities.size() != 0;
    }
}

