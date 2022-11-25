import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    private StackPane node;
    private Piece piece;
    private boolean containsPiece = false;
    private final int x;
    private final int y;
    private boolean selected = false;
    private boolean destinationTile = false;

    protected Tile(int x, int y, int tileSize) {

        super(x * tileSize, y * tileSize, tileSize, tileSize);
        this.x = x;
        this.y = y;
    }

    protected int getTileX() {
        return x;
    }

    protected int getTileY() {
        return y;
    }

    protected void setSelected(boolean selected) {
        this.selected = selected;
    }

    protected boolean isSelected() {
        return selected;
    }

    protected void setDestinationTile(boolean isDestinationTile) {
        this.destinationTile = isDestinationTile;
    }

    protected boolean isDestinationTile() {
        return destinationTile;
    }

    protected void setPiece(Piece piece) {
        this.piece = piece;
        containsPiece = piece != null;
    }

    protected Piece getPiece() { return piece; }

    protected boolean isOccupied() {
        return containsPiece;
    }

    protected void setNode(StackPane node) {
        this.node = node;
    }

    protected StackPane getNode() {
        return node;
    }
}
