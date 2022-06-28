package KillerSudoku.Logic;

import KillerSudoku.Solver.Solver;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class CageFunctions implements Serializable { // Ppresenteert de Killer Sudoku-configuratie in de oorspronkelijke staat en bevat methoden om deze te genereren
    // cages opstellen etc.
    private byte[][] cells;
    private int[][] cellCageIndexes; // De cage-index waartoe alle cellen behoren
    private int width, height;
    public CageSum[] cages;
    public boolean success;

    public CageFunctions(int width, int height, int cageNum){
        this.width = width;
        this.height = height;

        success = false;
        cells = new byte[height][width];
        fillIn(); // De nummers van alle cellen zijn ingesteld
        while (!success){ // Verschillende willekeurige kooien worden getest totdat een partitie met precies één oplossing is gevonden
            generateCages(cageNum);
            success =  Solver.numOfSolutions(cellCageIndexes, cages) == 1;
        }
    }

    private void fillIn(){
        //Backtracking-algoritme voor het instellen van alle celnummers.
        // Wanneer een conflict wordt aangetroffen (de cel heeft geen opties meer)
        // gaat het een stap terug via de geschiedenisstapel
        int width = cells[0].length;
        int height = cells.length;
        Stack<FillCells> history = new Stack<>(); // Om een stap terug kunnen doen
        List<Point> order = new ArrayList<>(); //Bepaalt de volgorde waarin de cellen worden gevuld. Op dit moment is het altijd dezelfde
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                order.add(new Point(x, y));
        while (history.size() <= order.size()) { // Het werkt totdat elke cel is gevuld
            if (history.size() == 0)
                history.push(new FillCells(order.get(0), 0, cells));
            FillCells currCell = history.peek();
            if (currCell.hasNextPossibility()) {
                currCell.tryNextPossibility(cells);
                if (history.size() < order.size())
                    history.push(new FillCells(order.get(currCell.cellInd + 1), currCell.cellInd + 1, cells));
                else
                    return; //Gevuld
            } else { // Hij doet een stap achteruit
                history.pop();
                cells[currCell.y][currCell.x] = 0;
            }
        }
    }

    private void generateCages(int num){
        cages = new CageSum[num];
        for (int i = 0; i < cages.length; i++)
            cages[i] = new CageSum();

        cellCageIndexes = new int[height][width];
        for (int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                cellCageIndexes[y][x] = -1;

        ArrayList<GrowCage> cageGrowers = new ArrayList<>(); // Lijst van cage-expansiecellen.

        for(int i = 0; i < cages.length; i++){ // De originele cellen van alle kooien worden willekeurig geselecteerd
            int x = 0, y = 0;
            while(cellCageIndexes[y][x] != -1){
                x = Utility.rand(width - 1);
                y = Utility.rand(height - 1);
            }
            cellCageIndexes[y][x] = i;
            cageGrowers.add(new GrowCage(x, y, i));
        }

        for(int i = 0; i < cageGrowers.size(); i++)
            cellCageIndexes[cageGrowers.get(i).y][cageGrowers.get(i).x] = -1;

        while (cageGrowers.size() > 0){
            int minInd = indOfMin(cageGrowers);
            GrowCage cg = cageGrowers.get(minInd);
            cageGrowers.remove(minInd);

            if(cellCageIndexes[cg.y][cg.x] == -1 && !cages[cg.cageInd].hasNum(cells[cg.y][cg.x], cells)) { // Als deze cel nog niet voorbij is
                cellCageIndexes[cg.y][cg.x] = cg.cageInd; // De hele kooi is toegevoegd
                cages[cg.cageInd].addCell(cg.x, cg.y, cells[cg.y][cg.x]);
                for (int x = -1; x <= 1; x++) // Alle celburen in cagegrowers worden toegevoegd om de kooi verder uit te breiden
                    for (int y = -1; y <= 1; y++)
                        if ((x == 0 || y == 0) && (x != 0 || y != 0) && cg.x + x >= 0 && cg.y + y >= 0 && cg.x + x < width && cg.y + y < height)
                            cageGrowers.add(new GrowCage(cg.x + x, cg.y + y, cg.cageInd));
            }
        }

        for (int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                if(cellCageIndexes[y][x] == -1){
                    generateCages(num);
                    return;
                }

        List<Integer> order = new ArrayList<>();
        for(int i = 0; i < cages.length; i++)
            order.add(i);
        colorCages(order); // dezelfde kleur geven aan de kooien zodat de gebruiker weet welke cellen erbij horen
    }

    private int indOfMin(ArrayList<GrowCage> cageGrowers){ //Retourneert de index van de cel die bij de kooi hoort met het kleinste huidige aantal cellen
        int minInd = 0;
        int min = cages[cageGrowers.get(0).cageInd].cells.size();
        for(int i = 1; i < cageGrowers.size(); i++) {
            int n = cages[cageGrowers.get(i).cageInd].cells.size();
            if (n < min || (n == min && Utility.rand(1) == 1)){
                min = n;
                minInd = i;
            }
        }
        return minInd;
    }


    private void colorCages(List<Integer> order){ // Willekeurige rijen kooikleuring worden getest totdat men ze niet kleurt, zodat aangrenzende kooien verschillende kleuren hebben
        for(int i = 0; i < cages.length; i++)
            cages[i].colorInd = 100;
        Collections.shuffle(order);
        for(int i = 0; i < order.size(); i++) {
            if (!tryColorCage(order.get(i))) {
                colorCages(order);
                return;
            }
        }
    }

    private boolean tryColorCage(int cageInd){ // kleur de kooi in de eerste kleur die zijn buren niet hebben
        for(int i = 0; i < 4; i++) {
            if(isColorFree((byte)i, cageInd)) {
                cages[cageInd].colorInd = (byte)i;
                return true;
            }
        }
        return false;
    }

    private boolean isColorFree(byte c, int cageInd){ // Retourneert waar als de buren van een bepaalde kooi de gegeven kleur niet gebruiken
        for(int i = 0; i < cages[cageInd].cells.size(); i++)
            if (!isColorFree(c, cages[cageInd].cells.get(i).x, cages[cageInd].cells.get(i).y, cageInd))
                return false;
        return true;
    }

    private boolean isColorFree(byte c, int x, int y, int cageInd){ // Geeft waar terug als de buren van een bepaalde cel een bepaalde kleur niet gebruiken, tenzij ze tot dezelfde kooi van die cel behoren
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if((i == 0 || j == 0) && (i != 0 || j != 0) && x + i >= 0 && y + j >= 0 && x + i < width && y + j < height){
                    if(cellCageIndexes[y + j][x + i] != cageInd)
                        if(cages[cellCageIndexes[y + j][x + i]].colorInd == c)
                            return false;
                }
            }
        }
        return true;
    }

    public CageSum cageOf(int cellX, int cellY){
        return cages[cellCageIndexes[cellY][cellX]];
    }

    public byte getNum(int x, int y){
        return cells[y][x];
    }

    public static Point getNonet(int cellX, int cellY){ // Pomocna metoda za vracaje poziciju top-left celije noneta kojem pripada data celija
        return new Point((cellX / 3) * 3, (cellY / 3) * 3);
    }
}
