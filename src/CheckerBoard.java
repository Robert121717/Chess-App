import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;

public class CheckerBoard extends Pane {

    private final int tiles = 8;
    private final Tile[][] checkerBoard = new Tile[tiles][tiles];
    private boolean hasDefaultTileScheme = true;

    private Paint v1;
    private Paint v2;

    protected CheckerBoard(Paint v1, Paint v2, int scale) {
        this.v1 = v1;
        this.v2 = v2;

        for (int x = 0; x < tiles; x++) {
            for (int y = 0; y < tiles; y++) {

                checkerBoard[x][y] = new Tile(x, y, scale);
                Paint tileColor = getTileColor(x, y, "white");

                checkerBoard[x][y].setFill(tileColor);
                checkerBoard[x][y].setStroke(tileColor);
                checkerBoard[x][y].setStrokeType(StrokeType.INSIDE);

                getChildren().add(checkerBoard[x][y]);
            }
        }
    }

    private Paint getTileColor(int x, int y, String userColor) {
        boolean tileColor;

        if (userColor.equals("black"))
            tileColor = (x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0);
        else
            tileColor = (x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1);

        return tileColor ? v2 : v1;
    }


    protected void setUserAlliance(String userAlliance) {
        hasDefaultTileScheme = userAlliance.equals("white");

        for (int x = 0; x < tiles; ++x) {
            for (int y = 0; y < tiles; ++y) {

                Paint tileColor = getTileColor(x, y, userAlliance);
                checkerBoard[x][y].setFill(tileColor);
                checkerBoard[x][y].setStroke(tileColor);
            }
        }
    }

    protected void changeTileColor(Paint v1, Paint v2, String userAlliance) {
        this.v1 = v1;
        this.v2 = v2;

        for (int x = 0; x < tiles; ++x) {
            for (int y = 0; y < tiles; ++y) {
                Paint tileColor = getTileColor(x, y, userAlliance);
                checkerBoard[x][y].setFill(tileColor);
                checkerBoard[x][y].setStroke(tileColor);
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
