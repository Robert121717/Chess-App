import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.util.HashSet;

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


    protected Piece(String color, String name, double width, double height) {
        this.color = color;
        this.name = name;
        this.width = width;
        this.height = height;

        createNode();
        assignValue();
    }

    protected String getName() {
        return name;
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

    private void createNode() {
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

    private void assignValue() { // TODO ?
        switch (name) {
            case "queen" -> value = 9;
            case "rook" -> value = 5;
            case "bishop", "knight" -> value = 3;
            case "pawn" -> value = 1;
            default -> value = 200;
        }
    }

    protected static class Path {

        private final int originX;
        private final int originY;
        private final Tile[][] tiles;
        private final HashSet<int[]> possiblePaths = new HashSet<>();

        protected Path(Tile[][] tiles, int originX, int originY) {
            this.tiles = tiles;
            this.originX = originX;
            this.originY = originY;
        }

        protected HashSet<int[]> getPathOptions(String name) {

            switch (name) {
                case "king" -> getKingPaths();
                case "queen" -> getQueenPaths();
                case "bishop" -> getBishopPaths();
                case "knight" -> getKnightPaths();
                case "rook" -> getRookPaths();
                default -> getPawnPaths();
            }
            return possiblePaths;
        }

        private void getKingPaths() {
            // todo check for self sabotaging move
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {

                    int xCoord = x + originX;
                    int yCoord = y + originY;

                    boolean newTile = x != 0 && y != 0;
                    boolean tileExists = 0 <= xCoord && xCoord <= 7 && 0 <= yCoord && yCoord <= 7;

                    if (newTile && tileExists) {
                        Tile possiblePath = tiles[xCoord][yCoord];

                        boolean isOccupied = possiblePath.isOccupied();
                        boolean isAvailable = false;

                        if (isOccupied) {
                            Piece neighboringPiece = possiblePath.getPiece();
                            if (neighboringPiece.getName().equals("rook") && neighboringPiece.isPlayer()) {
                                isAvailable = true;

                            } else if (!neighboringPiece.isPlayer()) {
                                isAvailable = true;
                            }
                        }

                        if (!isOccupied || isAvailable)
                            possiblePaths.add(new int[] {xCoord, yCoord});
                    }
                }
            }
        }

        private void getQueenPaths() {
            addDiagonalPaths();
            addHorizontalPaths();
            addVerticalPaths();
        }

        private void getBishopPaths() {
            addDiagonalPaths();
        }

        private void getKnightPaths() {

            for (int x = -2; x <= 2; ++x) {
                for (int y = -2; y <= 2; ++y) {
                    int xCoord = x + originX;
                    int yCoord = y + originY;

                    boolean newTile = x != 0 && y != 0;
                    boolean tileExists = 0 <= xCoord && xCoord <= 7 && 0 <= yCoord && yCoord <= 7;
                    boolean allowedMove = x != y && (Math.abs(x) == 2 && Math.abs(y) == 1) ||
                            (Math.abs(x) == 1 && Math.abs(y) == 2);

                    if (newTile && tileExists && allowedMove) {
                        Tile possiblePath = tiles[xCoord][yCoord];

                        if (!possiblePath.isOccupied() ||
                                (possiblePath.isOccupied() && !possiblePath.getPiece().isPlayer())) {
                            possiblePaths.add(new int[] {xCoord, yCoord});
                        }
                    }
                }
            }
        }

        private void getRookPaths() {
            addHorizontalPaths();
            addVerticalPaths();
        }

        private void getPawnPaths() {
            // todo en passant

            // moving forward one or two spaces
            int iterations = originY == 6 ? 2 : 1;
            for (int row = 1; row <= iterations; ++row) {
                int yCoord = originY - row;

                boolean tileExists = 0 <= yCoord;
                if (tileExists) {
                    boolean isOccupied = tiles[originX][yCoord].isOccupied();

                    if (!isOccupied)
                        possiblePaths.add(new int[]{originX, yCoord});
                }
            }
            // overtaking opponent piece diagonally
            int yCoord = originY + 1;
            for (int col = -1; col <= 1; col += 2) {
                int xCoord = originX + col;

                boolean tileExists = 0 <= xCoord && xCoord <= 7 && 0 <= yCoord && yCoord <= 7;
                if (tileExists) {
                    Tile possiblePath = tiles[xCoord][yCoord];
                    if (possiblePath.isOccupied()) {
                        Piece occupyingPiece = possiblePath.getPiece();

                        if (!occupyingPiece.isPlayer())
                            possiblePaths.add(new int[]{xCoord, yCoord});
                    }
                }
            }
        }

        private void addDiagonalPaths() {

            boolean continueDown = true;
            boolean continueUp = true;
            // adds diagonal paths to the left of the original coordinate
            for (int col = originX - 1; 0 <= col; --col) {
                int i = originX - 1 - col;

                if (continueDown) continueDown = testDiagonalPath(col, originY + 1 + i);
                if (continueUp) continueUp = testDiagonalPath(col, originY - 1 - i);
            }

            continueDown = true;
            continueUp = true;
            // adds diagonal paths to the right of original coordinate
            for (int i = 0; i <= 6 - originX; ++i) {
                int col = originX + 1 + i;

                if (continueDown) continueDown = testDiagonalPath(col, originY + 1 + i);
                if (continueUp) continueUp = testDiagonalPath(col, originY - 1 - i);
            }
        }

        private boolean testDiagonalPath(int col, int row) {
            boolean continueUp = true;

            boolean tileExists = 0 <= row && row <= 7;
            if (!tileExists) return false;

            Tile possiblePath = tiles[col][row];
            if (possiblePath.isOccupied()) {
                if (!possiblePath.getPiece().isPlayer()) {
                    possiblePaths.add(new int[] {col, row});
                }
                continueUp = false;
            } else {
                possiblePaths.add(new int[] {col, row});
            }
            return continueUp;
        }

        private void addHorizontalPaths() {
            // to the left of starting coordinate
            for (int col = originX - 1; 0 <= col; --col) {
                Tile possiblePath = tiles[col][originY];

                if (possiblePath.isOccupied()) {
                    if (!possiblePath.getPiece().isPlayer()) {
                        possiblePaths.add(new int[] {col, originY});
                    }
                    break;
                }
                possiblePaths.add(new int[] {col, originY});
            }
            // to the right of starting coordinate
            for (int col = originX + 1; col <= 7; ++col) {
                Tile possiblePath = tiles[col][originY];

                if (possiblePath.isOccupied()) {
                    if (!possiblePath.getPiece().isPlayer()) {
                        possiblePaths.add(new int[] {col, originY});
                    }
                    break;
                }
                possiblePaths.add(new int[] {col, originY});
            }
        }

        private void addVerticalPaths() {
            // below the starting coordinate
            for (int row = originY - 1; 0 <= row; --row) {
                Tile possiblePath = tiles[originX][row];

                if (possiblePath.isOccupied()) {
                    if (!possiblePath.getPiece().isPlayer()) {
                        possiblePaths.add(new int[] {originX, row});
                    }
                    break;
                }
                possiblePaths.add(new int[] {originX, row});
            }
            // above the starting coordinate
            for (int row = originY + 1; row <= 7; ++row) {
                Tile possiblePath = tiles[originX][row];

                if (possiblePath.isOccupied()) {
                    if (!possiblePath.getPiece().isPlayer()) {
                        possiblePaths.add(new int[] {originX, row});
                    }
                    break;
                }
                possiblePaths.add(new int[] {originX, row});
            }
        }
    }
}
