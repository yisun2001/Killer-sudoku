package KillerSudoku.GUI.Menus;

import KillerSudoku.GUI.GameCanvas;
import KillerSudoku.State.GameState;
import KillerSudoku.Logic.Utility;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class GameMenu extends Menu {
    public GameMenu(GameCanvas gameCanvas, GameState gameState){
        setText("Game");

        MenuItem showSolutionItem = new MenuItem("Show Solution");
        showSolutionItem.setOnAction(e -> {
            gameState.solutionChosen = true;
            for(int i = 0; i < gameState.getWidth(); i++)
                for(int j = 0; j < gameState.getHeight(); j++)
                    gameState.getCell(i, j).setNum(gameState.puzzle.getNum(i, j));
            gameCanvas.draw();
        });

        Menu newGameSubNenu = new Menu("New Killer Sudoku");

        MenuItem easyItem = new MenuItem("With help");
        easyItem.setOnAction(e -> {
            gameState.init(gameState.getWidth(), gameState.getHeight(), 40, Utility.rand(10, 15));
            gameCanvas.draw();
        });

        MenuItem hardItem = new MenuItem("Blank");
        hardItem.setOnAction(e -> {
            gameState.init(gameState.getWidth(), gameState.getHeight(), 40, 0);
            gameCanvas.draw();
        });

        newGameSubNenu.getItems().addAll(easyItem, hardItem);



        getItems().addAll(newGameSubNenu, showSolutionItem);
    }
}
