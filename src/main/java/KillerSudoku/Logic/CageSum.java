package KillerSudoku.Logic;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class CageSum implements Serializable { // genereer killer sudoku cages
    private int sum;
    private int x, y; // Een cel die de som van de cages weergeeft
    public byte colorInd; // cage kleur
    public ArrayList<Point> cells;

    public CageSum(){
        sum = 0;
        cells = new ArrayList<>();
    }

    public void addCell(int x, int y, byte cellNum){ // Voegt een nieuwe cel toe
        cells.add(new Point(x, y));
        sum += cellNum;
        calcPosition();
    }

    private void calcPosition(){ // Bereken op welke cel de som va de cage moet staan
        int bestInd = 0;
        for(int i = 1; i < cells.size(); i++){
            if(cells.get(i).y < cells.get(bestInd).y || (cells.get(i).y == cells.get(bestInd).y && cells.get(i).x < cells.get(bestInd).x))
                bestInd = i;
        }
        x = cells.get(bestInd).x;
        y = cells.get(bestInd).y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getSum(){
        return sum;
    }

    public boolean hasNum(byte num, byte[][] cells){
        for(Point p : this.cells)
            if(cells[p.y][p.x] == num)
                return true;
        return false;
    }
}
