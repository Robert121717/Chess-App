import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;

public class CheckerBoard extends Pane {

    private final int tiles = 8;
    private final Tile[][] checkerBoard = new Tile[tiles][tiles];
    private boolean hasDefaultTileScheme = true;

    private final Paint v1;
    private final Paint v2;

    protected CheckerBoard(Paint v1, Paint v2, int scale) {
        this.v1 = v1;
        this.v2 = v2;

        for (int x = 0; x < tiles; x++) {
            for (int y = 0; y < tiles; y++) {
                checkerBoard[x][y] = new Tile(x, y, scale);

                checkerBoard[x][y].setFill(getTileColor(x, y, false));
                getChildren().add(checkerBoard[x][y]);
            }
        }
    }

    private Paint getTileColor(int x, int y, boolean alternateColors) {
        boolean brighterFill;

        if (alternateColors)
            brighterFill = (x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0);
        else
            brighterFill = (x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1);

        return brighterFill ? v2 : v1;
    }

    protected void switchTileColors(boolean setToBlack) {
        this.hasDefaultTileScheme = setToBlack;

        for (int x = 0; x < tiles; ++x) {
            for (int y = 0; y < tiles; ++y) {
                checkerBoard[x][y].setFill(getTileColor(x, y, setToBlack));
            }
        }
    }

    protected Tile[][] getTiles() {
        return checkerBoard;
    }

    protected Paint[] getTheme() {
        return new Paint[] {v1, v2};
    }

    protected boolean isHasDefaultTileScheme() {
        return hasDefaultTileScheme;
    }
}
