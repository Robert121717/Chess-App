import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

public class CheckerBoard extends Pane {

    private final int tiles = 8;
    private final Tile[][] checkerBoard = new Tile[tiles][tiles];
    protected CheckerBoard() {

        for (int x = 0; x < tiles; x++) {
            for (int y = 0; y < tiles; y++) {
                checkerBoard[x][y] = new Tile(x, y);

                checkerBoard[x][y].setFill(getTileColor(x, y, false));
                getChildren().add(checkerBoard[x][y]);
            }
        }
    }

    private Color getTileColor(int x, int y, boolean switchColor) {
        boolean transparent;

        if (switchColor)
            transparent = (x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0);
        else
            transparent = (x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1);

        Color darkGray = Color.web("#ededed", 0.9);
        return transparent ? Color.TRANSPARENT : darkGray;
    }

    protected void switchTileColors() {

        for (int x = 0; x < tiles; ++x) {
            for (int y = 0; y < tiles; ++y) {
                checkerBoard[x][y].setFill(getTileColor(x, y, true));
            }
        }
    }

    protected void revertTileColors() {
        for (int x = 0; x < tiles; ++x) {
            for (int y = 0; y < tiles; ++y) {

                checkerBoard[x][y].setFill(getTileColor(x, y, false));
            }
        }
    }

    protected Tile[][] getTiles() {
        return checkerBoard;
    }
}
