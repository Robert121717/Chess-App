import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    private final int x;
    private final int y;
    private boolean containsPiece = false;
    private boolean selected = false;
    private boolean interaction = false;
    private boolean isDestinationTile = false;
    private Piece piece;

    protected Tile(int x, int y) {
//        super.setLayoutX(x * 55);
//        super.setLayoutY(y * 55);
//        super.setWidth(55);
//        super.setHeight(55);
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

    protected void allowInteraction(boolean allowed) {
        this.interaction = allowed;
    }

    protected boolean allowsInteraction() {
        return interaction;
    }

    protected void setDestinationTile(boolean isDestinationTile) {
        this.isDestinationTile = isDestinationTile;
    }

    protected boolean isDestinationTile() {
        return isDestinationTile;
    }

    protected void setPiece(Piece piece) {
        this.piece = piece;
        containsPiece = true;
    }

    protected Piece getPiece() { return piece; }

    protected void containsPiece(boolean containsPiece) {
        this.containsPiece = containsPiece;

        // piece will have been removed from tile if this false is passed in
        if (!containsPiece) piece = null;
    }

    protected boolean isOccupied() {
        return containsPiece;
    }
}
