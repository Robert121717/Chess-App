import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    private StackPane node;
    private final int x;
    private final int y;
    private boolean containsPiece = false;
    private boolean selected = false;
    private boolean destinationTile = false;
    private Piece piece;

    protected Tile(int x, int y) {
        super(x * 55, y * 55, 55, 55);
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
        containsPiece = true;
    }

    protected Piece getPiece() { return piece; }

    protected void setNode(StackPane node) {
        this.node = node;
    }

    protected StackPane getNode() {
        return node;
    }

    protected void containsPiece(boolean containsPiece) {
        this.containsPiece = containsPiece;

        // piece will have been removed from tile if this false is passed in
        if (!containsPiece) piece = null;
    }

    protected boolean isOccupied() {
        return containsPiece;
    }
}
