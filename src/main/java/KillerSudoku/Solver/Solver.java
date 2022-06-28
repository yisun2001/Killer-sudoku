package KillerSudoku.Solver;

import KillerSudoku.Logic.CageSum;


import java.awt.*;
import java.util.ArrayList;

public class Solver {
    public static int numOfSolutions(int[][] cellCageIndexes, CageSum[] cages){
        // Vindt of een bepaalde killer-sudoku 0, 1 of meer dan 1 oplossing heeft
        // moet minstens 1 solution hebben zodat de cages gegenereerd kunnen worden
        SolverCell[][] cells = new SolverCell[cellCageIndexes.length][cellCageIndexes[0].length];
        for(int i = 0; i < cells.length; i++)
            for(int j = 0; j < cells[i].length; j++)
                cells[i][j] = new SolverCell(j, i);
        int oldPossNum = -1;
        int newPossNum = updateCellPossibilities(cells, cellCageIndexes, cages);
        while(newPossNum != oldPossNum){ // Het gaat door alle cellen en herberekent hun mogelijkheden totdat ze niet langer kunnen worden verminderd
            oldPossNum = newPossNum;
            newPossNum = updateCellPossibilities(cells, cellCageIndexes, cages);
        }
        if(newPossNum == cells.length * cells[0].length)
            return 1; // 1 mogelijkheid gevonden
        ArrayList<SolverCell> undecided = new ArrayList<>();
        for(int i = 0; i < cells.length; i++)
            for(int j = 0; j < cells[i].length; j++)
                if(cells[i][j].possibilities.size() > 1) // als de cel mogelijk is
                    undecided.add(cells[i][j]); // voeg nummer toe aan undecided
        if(undecided.size() > 35) // Te veel om elke combinatie door te geven
            return 2; // Het exacte aantal is niet belangrijk, alleen dat het groter is dan 1
        byte[][] cellsCurr = new byte[cells.length][cells[0].length];
        for(int i = 0; i < cells.length; i++)
            for(int j = 0; j < cells[i].length; j++)
                if(cells[i][j].possibilities.size() == 1)
                    cellsCurr[i][j] = cells[i][j].possibilities.get(0);
        return numOfValidCombinations(cellsCurr, cellCageIndexes, cages, undecided, 0);
    }

    private static int numOfValidCombinations(byte[][] cells, int[][] cellCageIndexes, CageSum[] cages, ArrayList<SolverCell> undecided, int curr){
        //Controleert elke combinatie van getallen voor alle cellen die meer dan één optie hebben
        if(curr >= undecided.size()){
            for(int i = 0; i < undecided.size(); i++) {
                int x = undecided.get(i).x;
                int y = undecided.get(i).y;
                if (!isValid(cells, x, y, cages[cellCageIndexes[y][x]], true))
                    return 0;
            }
            return 1;
        }
        SolverCell c = undecided.get(curr);
        int num = 0;
        for(int i = 0; i < c.possibilities.size(); i++){
            cells[c.y][c.x] = c.possibilities.get(i);
            if(isValid(cells, c.x, c.y, cages[cellCageIndexes[c.y][c.x]], false))
                // Het laat de somcontrole helemaal tot het einde staan, omdat sommige cellen nog steeds leeg zijn
                num += numOfValidCombinations(cells, cellCageIndexes, cages, undecided, curr + 1);
            if(num >= 2)
                return 2;
        }
        cells[c.y][c.x] = 0; // keer terug naar 0 aan het einde
        return num;
    }

    private static boolean isValid(byte[][] cells, int x, int y, CageSum cage, boolean checkCageSum){
        // Controleert of de gegeven cel aan alle voorwaarden van het spel voldoet
        int width = cells[0].length;
        int height = cells.length;
        byte num = cells[y][x];
        for(int i = 0; i < width; i++)
            if(i != x && cells[y][i] == num)
                return false;
        for(int i = 0; i < height; i++)
            if(i != y && cells[i][x] == num)
                return false;

        if(cage == null)
            return true;
        for(int i = 0; i < cage.cells.size(); i++)
            if(cage.cells.get(i).x != x || cage.cells.get(i).y != y)
                if(cells[cage.cells.get(i).y][cage.cells.get(i).x] == num)
                    return false;
         if(checkCageSum){
             int sum = 0;
             for(Point p : cage.cells)
                 sum += cells[p.y][p.x];
             return sum == cage.getSum();
         }
        return true;
    }

    private static int updateCellPossibilities(SolverCell[][] cells, int[][] cellCageIndexes, CageSum[] cages){
        // Voor alle cellen berekent het de mogelijkheden en retourneert het de som van de mogelijkheden van alle cellen
        int totalPossNum = 0;
        for(int i = 0; i < cells.length; i++)
            for(int j = 0; j < cells[i].length; j++) {
                int n = cells[i][j].updatePossibilities(cells, cages[cellCageIndexes[i][j]]);
                if(n == 0)
                    return 0;
                totalPossNum += n;
            }
        return totalPossNum;
    }
}
