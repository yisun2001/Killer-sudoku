package KillerSudoku.GUI;

import KillerSudoku.GUI.Menus.GameMenu;
import KillerSudoku.State.GameState;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class RootPane extends BorderPane { // Glavni GUI element koji sadrzi sve ostale
    public RootPane(Stage primaryStage){
        super();
        GameState gameState = new GameState();
        gameState.init(9, 9, 40, 0, 5); // Prvobitna igra koja automatski zapocinje

        GameCanvas gameCanvas = new GameCanvas(gameState); // Canvas


        Pane wrapperPane = getGamePane(gameCanvas);

        // Dodaju se svi ostali elementi
        setCenter(wrapperPane);
        setTop(getMenuBar(primaryStage, gameCanvas, gameState));
    }


    // Sadrzi sve menije
    private MenuBar getMenuBar(Stage primaryStage, GameCanvas gameCanvas, GameState gameState){
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(new GameMenu(gameCanvas, gameState));
        return menuBar;
    }

    private Pane getGamePane(GameCanvas gameCanvas){ // Koristi se da bi canvas uvjek automatski zauzimao najveci dio prozora
        Pane wrapperPane = new Pane(gameCanvas);
        gameCanvas.widthProperty().bind(wrapperPane.widthProperty());
        gameCanvas.heightProperty().bind(wrapperPane.heightProperty());

        return wrapperPane;
    }
}
