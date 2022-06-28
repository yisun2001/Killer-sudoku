package KillerSudoku.GUI;

import KillerSudoku.Logic.CageSum;
import KillerSudoku.State.GameState;
import KillerSudoku.State.PlayerCell;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.awt.*;

public class GameCanvas extends Canvas {
    private static final int BOARD_X = 0, BOARD_Y = 0;

    private static final int BOARD_BORDER_THICKNESS = 10, NONET_BORDER_THICKNESS = 5, CELL_BORDER_THICKNESS = 1;

    private static final int CELL_SIZE = 80; // Paginalengte voor cellen

    // Kleuren die de achtergrond, randen en andere elementen vullen
    private static final Color BACKGROUND_COLOR = Color.WHITE, TEXT_COLOR = Color.BLACK, BORDER_COLOR = Color.BLACK;

    // 4 kleuren om de cage te differentieren
    private static final Color[] CAGE_COLORS = new Color[]{Color.rgb(255, 253, 152), Color.rgb(207, 231, 153), Color.rgb(203, 232, 250), Color.rgb(248, 207, 223), Color.rgb(248, 207, 223)};

    // Lettergroottes
    private static final double MAIN_NUM_FONT_SIZE = 50;
    private static final double CANDIDATE_FONT_SIZE = 25;
    private static final double CAGE_SUM_FONT_SIZE = 15;

    // Lettertypen
    private static Font MAIN_NUM_FONT;
    private static Font CANDIDATE_FONT;
    private static Font CAGE_SUM_FONT;

    private GameState gameState; // Een verwijzing naar de staat van het spel dat dit object vertegenwoordigt
    private GraphicsContext g;



    private double ratio;
    private double offset;

    public GameCanvas(GameState gameState){
        super();

        this.gameState = gameState;

        ratio = 1;

        g = getGraphicsContext2D();
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);

        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());

        setFocusTraversable(true);


    }


    private Point cellInnerPos(int indX, int indY){ //linkerbovenkant van het celinterieur (omdat de cellen de randen delen)
        int nonetBorderOnXNum = indX / 3;
        int nonetBorderOnYNum = indY / 3;
        return new Point(CELL_SIZE * indX + CELL_BORDER_THICKNESS * (indX - nonetBorderOnXNum) + BOARD_BORDER_THICKNESS + NONET_BORDER_THICKNESS * nonetBorderOnXNum,
                CELL_SIZE * indY + CELL_BORDER_THICKNESS * (indY - nonetBorderOnYNum) + BOARD_BORDER_THICKNESS + NONET_BORDER_THICKNESS * nonetBorderOnYNum);
    }

    private void adjustForCanvasSize(){ //De verhouding en offset worden berekend met behulp van de afmetingen van het canvas en de lettergroottes worden aangepast aan de verhouding
        ratio = Math.min(getWidth(), getHeight()) / 755.0;
        offset = (Math.max(getWidth(), getHeight()) - Math.min(getWidth(), getHeight())) / 2;

        MAIN_NUM_FONT =  Font.font("Ariel", FontWeight.BOLD, MAIN_NUM_FONT_SIZE * ratio);
        CANDIDATE_FONT =  Font.font("Ariel", FontWeight.SEMI_BOLD, CANDIDATE_FONT_SIZE * ratio);
        CAGE_SUM_FONT =  Font.font("Ariel", FontWeight.BOLD, CAGE_SUM_FONT_SIZE * ratio);
    }

    public void draw(){
        adjustForCanvasSize();

        g.setFill(BACKGROUND_COLOR); //achtergrond kleur
        g.fillRect(0, 0, getWidth(), getHeight());
        drawCells();
        drawBorders();
        drawCageSums();

        if(gameState.usedSolutionMenuItem){ // als je hebt gewonnen
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Puzzle Solved");
            alert.setHeaderText("Puzzle solved!");
            alert.showAndWait();
            gameState.selection = null;
        }
    }

    private void drawCageSums(){ // Sommen van kooien worden getrokken
        if(gameState.puzzle.cages.length == 1)
            return;
        for(CageSum c : gameState.puzzle.cages){
            Point p = cellInnerPos(c.getX(), c.getY());
            drawNum(c.getSum(), p.x, p.y, CAGE_SUM_FONT, CAGE_COLORS[c.colorInd].invert(), false);
        }
    }



    private void drawCells(){ // Alle cellen zijn getekend
        for(int y = 0; y < gameState.getHeight(); y++){
            for(int x = 0; x < gameState.getWidth(); x++){
                Point pos = cellInnerPos(x, y);
                PlayerCell c = gameState.getCell(x, y);
                Point p = cellInnerPos(x, y);
                if(gameState.puzzle.cages.length == 1)
                    g.setFill(Color.WHITE);
                else
                    g.setFill(CAGE_COLORS[gameState.puzzle.cageOf(x, y).colorInd]);
                fillRect(p.x, p.y, CELL_SIZE, CELL_SIZE);
                switch (c.getState()){
                    case CANDIDATES:
                        for(int cx = 0; cx < 3; cx++){
                            for(int cy = 0; cy < 3; cy++){
                                byte i = (byte)(cy * 3 + cx + 1);
                                if(c.isCandidate(i))
                                    drawNum(i, pos.x + CELL_SIZE / 4 * (cx + 1), pos.y + CELL_SIZE / 4 * (cy + 1), CANDIDATE_FONT);
                            }
                        }
                        break;
                    case FINALIZED:
                        drawNum(c.getNum(), pos.x + CELL_SIZE / 2, pos.y + CELL_SIZE / 2, MAIN_NUM_FONT);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void drawBorders(){ // Alle randen zijn getekend
        int boardBorderWidth = cellInnerPos(gameState.getWidth() - 1, 0).x + CELL_SIZE + BOARD_BORDER_THICKNESS - BOARD_X;
        int boardBorderHeight = cellInnerPos(0, gameState.getHeight() - 1).y + CELL_SIZE + BOARD_BORDER_THICKNESS - BOARD_Y;

        drawBorder(BOARD_X, BOARD_Y, boardBorderWidth, boardBorderHeight, BOARD_BORDER_THICKNESS, BORDER_COLOR);
        for(int y = 0; y < gameState.getHeight() / 3; y++){
            for(int x = 0; x < gameState.getWidth() / 3; x++){
                Point topLeft = cellInnerPos(x * 3, y * 3);
                Point botRight = cellInnerPos(x * 3 + 2, y * 3 + 2);

                int bx = topLeft.x - NONET_BORDER_THICKNESS;
                int by = topLeft.y - NONET_BORDER_THICKNESS;
                int bw = botRight.x + CELL_SIZE + NONET_BORDER_THICKNESS - bx;
                int bh = botRight.y + CELL_SIZE + NONET_BORDER_THICKNESS - by;

                drawBorder(bx, by, bw, bh, NONET_BORDER_THICKNESS, BORDER_COLOR);
            }
        }

        for(int y = 0; y < gameState.getHeight(); y++){
            for(int x = 0; x < gameState.getWidth(); x++){
                Point pos = cellInnerPos(x, y);
                drawBorder(pos.x, pos.y, CELL_SIZE + CELL_BORDER_THICKNESS, CELL_SIZE + CELL_BORDER_THICKNESS, CELL_BORDER_THICKNESS, BORDER_COLOR);
            }
        }

    }

    private void drawNum(int num, int x, int y, Font font){ // Kandidaten en celnummers tekenen
        drawNum(num, x, y, font, TEXT_COLOR, true);
    }

    private void drawNum(int num, int x, int y, Font font, Color color, boolean isCentered){ // Een meer algemene methode voor het tekenen van nummers. Het wordt ook gebruikt voor cage sommen
        if(isCentered){
            g.setTextAlign(TextAlignment.CENTER);
            g.setTextBaseline(VPos.CENTER);
        }else{
            g.setTextAlign(TextAlignment.LEFT);
            g.setTextBaseline(VPos.TOP);
            x += 2;
            y -= 2;
        }
        g.setFont(font);
        g.setFill(color);
        fillText(num + "", x, y);
    }

    private void drawBorder(int x, int y, int width, int height, int thickness, Color color){ // Een rand tekenen
        g.setFill(color);

        fillRect(x, y, width, thickness);
        fillRect(x, y, thickness, height);
        fillRect(x,y + height - thickness, width, thickness);
        fillRect(x + width - thickness, y, thickness, height);
    }

    private void fillRect(double x, double y, double w, double h){ // binnenkant van rand
        if(getWidth() > getHeight())
            g.fillRect(offset + x * ratio, y * ratio, w * ratio, h * ratio);
        else
            g.fillRect(x * ratio, offset + y * ratio, w * ratio, h * ratio);
    }

    private void fillText(String text, double x, double y){ // Om tekst te tekenen (momenteel wordt alleen de nummertekenmethode gebruikt)
        if(getWidth() > getHeight())
            g.fillText(text, offset + x * ratio, y * ratio);
        else
            g.fillText(text, x * ratio, offset + y * ratio);
    }


}
