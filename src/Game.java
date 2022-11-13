import javafx.scene.layout.*;
import java.util.HashSet;

public class Game {

    private final GridPane chessBoard;
    private final CheckerBoard boardBackground;
    private final Tile[][] tiles;
    private Player npc;
    private Player user;
    private final String userColor;
    private final String npcColor;

    private Round currentRound;

    protected Game(GridPane chessBoard, CheckerBoard boardBackground, String userColor) {
        this.chessBoard = chessBoard;

        this.boardBackground = boardBackground;
        tiles = boardBackground.getTiles();

        this.userColor = userColor;
        npcColor = userColor.equals("white") ? "black" : "white";
    }

    protected HashSet<Piece> loadBoard() {

        chessBoard.getChildren().clear();
        chessBoard.add(boardBackground, 0, 0);

        HashSet<Piece> pieces = new HashSet<>();

        npc = new Player(tiles, npcColor, true);
        pieces.addAll(npc.loadPieces());

        user = new Player(tiles, userColor, false);
        pieces.addAll(user.loadPieces());

        fillUnoccupiedTiles();
        return pieces;
    }

    private void fillUnoccupiedTiles() {

        for (int col = 0; col < 8; ++col) {
            for (int row = 2; row < 7; ++row) {
                StackPane filler = new StackPane();

                tiles[col][row].setNode(filler);
                chessBoard.add(filler, col, row);
            }
        }
    }

    protected void newRound(Tile selectedTile) {

        highlightNode(selectedTile.getNode());
        HashSet<Tile> pathOptions = getPathOptions(selectedTile);

        for (Tile tile : pathOptions) {
            tile.setDestinationTile(true);

            StackPane node = tile.getNode();
            highlightNode(node);
        }
    }

    protected void cancelRound(Tile selectedTile) {
        selectedTile.getNode().setStyle("-fx-background-color: transparent;");

        for (Tile tile : currentRound.getDestinationTiles()) {
            tile.getNode().setStyle("-fx-background-color: transparent;");
        }
    }

    protected void finishRound(Tile selectedTile) {
        // todo maybe incorporate a translate transition here?

    }

    private HashSet<Tile> getPathOptions(Tile tile) {

        currentRound = new Round(tile);
        return currentRound.findPathOptions();
    }

    private void highlightNode(StackPane node) {
        node.setStyle("-fx-background-color: " +
                "radial-gradient(focus-distance 0% , center 50% 50% , radius 100% , " +
                "rgba(0, 0, 0, 0), " +
                "rgba(58, 175, 220, 0.88));");
    }

    private class Round {

        private final String pieceName;
        private final int originX;
        private final int originY;
        private final HashSet<int[]> possibleMoves = new HashSet<>();
        private final HashSet<Tile> destinationTiles = new HashSet<>();

        protected Round(Tile originTile) {
            pieceName = originTile.getPiece().getName();
            originX = originTile.getTileX();
            originY = originTile.getTileY();
        }

        protected HashSet<Tile> findPathOptions() {
            switch (pieceName) {
                case "king" -> getKingPaths();
                case "queen" -> getQueenPaths();
                case "bishop" -> getBishopPaths();
                case "knight" -> getKnightPaths();
                case "rook" -> getRookPaths();
                default -> getPawnPaths();
            };
            return toTileSet();
        }

        private void getKingPaths() {
            // todo check for self sabotaging move
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {

                    int xCoord = x + originX;
                    int yCoord = y + originY;

                    boolean newTile = x != originX && y != originY;
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
                            possibleMoves.add(new int[] {xCoord, yCoord});
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

                    boolean newTile = x != originX && y != originY;
                    boolean tileExists = originX + x >= 0 && originY + y >= 0;
                    boolean allowedMove = x != y && x != 0 && y != 0;

                    if (newTile && tileExists && allowedMove) {
                        boolean isOccupied = tiles[x][y].isOccupied();
                        if (!isOccupied) possibleMoves.add(new int[] {originX + x, originY + y});
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
                        possibleMoves.add(new int[]{originX, yCoord});
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
                            possibleMoves.add(new int[]{xCoord, yCoord});
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
                    possibleMoves.add(new int[] {col, row});
                }
                continueUp = false;
            } else {
                possibleMoves.add(new int[] {col, row});
            }
            return continueUp;
        }

        private void addHorizontalPaths() {
            // to the left of starting coordinate
            for (int col = originX - 1; 0 <= col; --col) {
                Tile possiblePath = tiles[col][originY];

                if (possiblePath.isOccupied()) {
                    if (!possiblePath.getPiece().isPlayer()) {
                        possibleMoves.add(new int[] {col, originY});
                    }
                    break;
                }
                possibleMoves.add(new int[] {col, originY});
            }
            // to the right of starting coordinate
            for (int col = originX + 1; col <= 7; ++col) {
                Tile possiblePath = tiles[col][originY];

                if (possiblePath.isOccupied()) {
                    if (!possiblePath.getPiece().isPlayer()) {
                        possibleMoves.add(new int[] {col, originY});
                    }
                    break;
                }
                possibleMoves.add(new int[] {col, originY});
            }
        }

        private void addVerticalPaths() {
            // below the starting coordinate
            for (int row = originY - 1; 0 <= row; --row) {
                Tile possiblePath = tiles[originX][row];

                if (possiblePath.isOccupied()) {
                    if (!possiblePath.getPiece().isPlayer()) {
                        possibleMoves.add(new int[] {originX, row});
                    }
                    break;
                }
                possibleMoves.add(new int[] {originX, row});
            }
            // above the starting coordinate
            for (int row = originY + 1; row <= 7; ++row) {
                Tile possiblePath = tiles[originX][row];

                if (possiblePath.isOccupied()) {
                    if (!possiblePath.getPiece().isPlayer()) {
                        possibleMoves.add(new int[] {originX, row});
                    }
                    break;
                }
                possibleMoves.add(new int[] {originX, row});
            }
        }

        private void addIfDiagonal(int x, int y) {
            if (x == originX || y == originY) return;

            boolean diagonal = Math.abs(x - originX) == Math.abs(y - originY);
            if (diagonal) possibleMoves.add(new int[] {x, y});
        }

        private HashSet<Tile> toTileSet() {

            for (int[] coordinate : possibleMoves) {
                int x = coordinate[0];
                int y = coordinate[1];

                destinationTiles.add(tiles[x][y]);
            }
            return destinationTiles;
        }

        protected HashSet<Tile> getDestinationTiles() {
            return destinationTiles;
        }
    }
}
