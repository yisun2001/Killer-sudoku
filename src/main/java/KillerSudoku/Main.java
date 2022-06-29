package KillerSudoku;

import KillerSudoku.GUI.RootPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
    }

    private void initStage(Stage stage){ //
        stage.setResizable(true);
        stage.setTitle("Killer Sudoku Solver by Yi & Bart");
        stage.setScene(new Scene(new RootPane(stage), 800, 800));
        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
        stage.show();
    }
}