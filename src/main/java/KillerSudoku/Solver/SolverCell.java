package KillerSudoku.Solver;

import KillerSudoku.Cage;
import KillerSudoku.Puzzle.Puzzle;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SolverCell {
    public int x, y;
    public ArrayList<Byte> possibilities; // alle huidige mogelijke waarden van deze cel

    public SolverCell(int x, int y){
        this.x = x;
        this.y = y;
        possibilities = new ArrayList<>();
        for (int i = 1; i <= 9; i++)
            possibilities.add((byte) i);
    }

    public int updatePossibilities(SolverCell[][] cells, Cage cage){ // Elimineert alle opties die niet langer geldig zijn
        int width = cells[0].length;
        int height = cells.length;
        for(int i = 0; i < width; i++) // horizontaal
            if(i != x)
                checkNeighbouringCell(cells[y][i]);
        for(int i = 0; i < height; i++) // verticaal
            if(i != y)
                checkNeighbouringCell(cells[i][x]);
        Point nonet = Puzzle.getNonet(x, y);
        for(int i = nonet.x; i < nonet.x + 3; i++){ // 3x3 groepen
            for(int j = nonet.y; j < nonet.y + 3; j++){
                if(i != x || j != y)
                    checkNeighbouringCell(cells[j][i]);
            }
        }
        for(Point p : cage.cells){ // Cellen in dezelfde cage
            if(p.x != x || p.y != y)
                checkNeighbouringCell(cells[p.y][p.x]);
        }
        HashSet<Byte> possibilitiesForCageSum = new HashSet<>();
        // Alle mogelijkheden die niet kunnen leiden tot de som van cages met een combinatie
        // van andere kooicellen worden geëlimineerd
        addPossibilitiesForCageSum(possibilitiesForCageSum, cage, cells, 0, 0);
        for(int i = possibilities.size() - 1; i >= 0; i--)
            if(!possibilitiesForCageSum.contains(possibilities.get(i)))
                possibilities.remove(i);

        return possibilities.size();
    }

    private void addPossibilitiesForCageSum(HashSet<Byte> possibilitiesForCageSum, Cage cage, SolverCell[][] cells, int curr, int currSum){
        if(curr == cage.cells.size()){
           // Recursie basis. Het nummer dat nodig is om cage te bereiken, wordt toegevoegd als het geldig is
            int num = cage.getSum() - currSum;
            if(num <= 9 && num >= 1)
                possibilitiesForCageSum.add((byte)num);
            return;
        }
        if(cage.cells.get(curr).x == x && cage.cells.get(curr).y == y) {
           // Als de huidige cel dezelfde is, worden ze overgeslagen omdat pas na deze methode
            // de mogelijkheden ervan worden gebruikt
            addPossibilitiesForCageSum(possibilitiesForCageSum, cage, cells, curr + 1, currSum);
            return;
        }
        List<Byte> neighbourPoss = cells[cage.cells.get(curr).y][cage.cells.get(curr).x].possibilities;
        for(int i = 0; i < neighbourPoss.size(); i++) // Elke combinatie van mogelijkheden wordt geobserveerd
            addPossibilitiesForCageSum(possibilitiesForCageSum, cage, cells, curr + 1, currSum + neighbourPoss.get(i));
    }

    private void checkNeighbouringCell(SolverCell c){ // Za celije u istoj horizontali, vertikali, nonetu ili kavezu
        if(c.possibilities.size() == 1)
            removePossibility(c.possibilities.get(0));
    }

    private void removePossibility(byte p){ // elimineer mogelijkheid
        for(int i = 0; i < possibilities.size(); i++)
            if(possibilities.get(i) == p) {
                possibilities.remove(i);
                return;
            }
    }
}
