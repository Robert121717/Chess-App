import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Piece {

    private final String name;
    private final String color;
    private final StackPane node = new StackPane();
    private final double width;
    private final double height;
    private int xLocation;
    private int yLocation;
    private boolean active = true;
    private boolean isPlayer = false;
    private Tile tileOccupying;
    private int value;
    private boolean canCastle = true;


    protected Piece(String color, String name, double width, double height) {
        this.color = color;
        this.name = name;
        this.width = width;
        this.height = height;

        assignValue();
    }

    protected String getAlliance() {
        return color;
    }

    protected String getName() {
        return name;
    }

    protected double[] getDimensions() {
        return new double[] {width, height};
    }

    protected void setTileOccupying(Tile tileOccupying) {
        this.tileOccupying = tileOccupying;
    }

    protected Tile getTileOccupying() {
        return tileOccupying;
    }

    protected void setLocation(int x, int y) {
        xLocation = x;
        yLocation = y;
    }

    protected int[] getLocation() {
        return new int[] {xLocation, yLocation};
    }

    protected void createNode() {
        try {
            String path = "Images/" + color + " " + name;

            Image graphic = new Image(path + ".png");
            ImageView view = new ImageView(graphic);

            view.setFitWidth(width);
            view.setFitHeight(height);

            node.getChildren().add(view);
            node.setAlignment(Pos.CENTER);

        } catch (NullPointerException | IllegalArgumentException e) {
            System.out.println("A graphic for one of the pieces failed to load upon initialization." +
                    " Please review the images file.");
        }
    }

    protected StackPane getNode() {
        return node;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    protected boolean isActive() {
        return active;
    }

    protected void setPlayer() {
        isPlayer = true;
    }

    protected boolean isPlayer() {
        return isPlayer;
    }

    protected int getValue() {
        return value;
    }

    private void assignValue() {
        switch (name) {
            case "queen" -> value = 9;
            case "rook" -> value = 5;
            case "bishop", "knight" -> value = 3;
            case "pawn" -> value = 1;
            default -> value = 200;
        }
    }

    protected boolean canCastle() {
        return canCastle;
    }

    protected void disableCastle() {
        canCastle = false;
    }
}
