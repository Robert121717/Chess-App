import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashSet;

public class Piece {

    private final String color;
    private final String name;
    private String location;
    private final double width;
    private final double height;
    private String knightDirection = "";
    private boolean active = true;
    private int value;


    protected Piece(String color, String name, double width, double height) {
        this.color = color;
        this.name = name;

        this.width = width;
        this.height = height;

        assignValue();
    }

    protected String getName() {
        return name;
    }

    protected void setLocation(String location) {
        this.location = location;
    }

    protected String getLocation() {
        return location;
    }

    protected void setKnight(String direction) {
        knightDirection = direction;
    }

    protected StackPane getNode() throws NullPointerException {
        String path = "Images/" + color + " " + name;

        if (!knightDirection.isEmpty()) {
            path += " " + knightDirection;
        }
        Image graphic = new Image(path + ".png");
        ImageView view = new ImageView(graphic);

        view.setFitWidth(width);
        view.setFitHeight(height);

        StackPane node = new StackPane(view);
        node.setAlignment(Pos.CENTER);

        return node;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    protected boolean isActive() {
        return active;
    }

    protected int getValue() {
        return value;
    }

    private void assignValue() { // TODO ?
        switch (name) {
            case "queen" -> value = 9;
            case "rook" -> value = 5;
            case "bishop", "knight" -> value = 3;
            case "pawn" -> value = 1;
            default -> value = 200;
        }
    }
}
