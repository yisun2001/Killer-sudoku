package KillerSudoku.GUI;

import KillerSudoku.GUI.Menus.GameMenu;
import KillerSudoku.State.GameState;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class RootPane extends BorderPane { // Het belangrijkste GUI-element dat al het andere bevat
    public RootPane(Stage primaryStage){
        super();
        GameState gameState = new GameState();
        gameState.init(9, 9, 40, 0);
        // Het originele spel dat automatisch start

        GameCanvas gameCanvas = new GameCanvas(gameState); // Canvas


        Pane wrapperPane = getGamePane(gameCanvas);

        // Alle andere elementen zijn toegevoegd
        setCenter(wrapperPane);
        setTop(getMenuBar(primaryStage, gameCanvas, gameState));
    }


    // Bevat alle menu's
    private MenuBar getMenuBar(Stage primaryStage, GameCanvas gameCanvas, GameState gameState){
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(new GameMenu(gameCanvas, gameState));
        return menuBar;
    }

    private Pane getGamePane(GameCanvas gameCanvas){
        // Het wordt zo gebruikt dat het canvas altijd automatisch het grootste deel van het venster inneemt
        Pane wrapperPane = new Pane(gameCanvas);
        gameCanvas.widthProperty().bind(wrapperPane.widthProperty());
        gameCanvas.heightProperty().bind(wrapperPane.heightProperty());

        return wrapperPane;
    }
}
