import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.HashSet;

public class Game {

    private final GridPane chessBoard;
    private final Tile[][] tiles;
    private Player npc;
    private Player user;
    private final String userColor;
    private final String npcColor;

    protected Game(GridPane chessBoard, Tile[][] tiles, String userColor) {
        this.chessBoard = chessBoard;
        this.tiles = tiles;

        this.userColor = userColor;
        npcColor = userColor.equals("white") ? "black" : "white";

        loadBoard();
    }

    private void loadBoard() {
        try {
            npc = new Player(chessBoard, tiles, npcColor, true);
            npc.loadPieces();

            user = new Player(chessBoard, tiles, userColor, false);
            user.loadPieces();

            allowInteraction();

        } catch (NullPointerException exc) {
            System.out.println("A graphic for one of the pieces failed to load upon initialization." +
                    " Please review the images file.");
        }
    }

    protected void newMove(Tile selectedTile) {
        HashSet<Tile> possibleMoves = getPossibleMoves(selectedTile);

        System.out.println("reached newMove() in Game class");

        for (Tile tile : possibleMoves) {
            tile.setDestinationTile(true);      // to recognize when user finalizes move
            tile.setFill(Color.web("rgba(58,175,220,0.7)"));
//            tile.setStyle("-fx-background-color: rgba(58, 175, 220, 0.7)");
        }

    }

    private void newNpcMove() {

    }

    private HashSet<Tile> getPossibleMoves(Tile tile) {
        Piece selectedPiece = tile.getPiece();
        Round round = new Round(selectedPiece.getName(), tile);

        return round.getMoves(false);
    }

    private void selectPossibleMoves(Tile[] tiles, boolean enable) {

    }

    private void finalizeMove(Tile originTile, Tile[] possibleMoves, Tile chosenTile) {

    }

    private void allowInteraction() {

        for (int y = 0; y < 8; ++y) {
            if (y < 2 || y > 5) {
                for (int x = 0; x < 8; ++x) {
                    tiles[x][y].allowInteraction(true);
                }
            }
        }
    }

    private class Round {

        private final String pieceName;
        private final int originX;
        private final int originY;
        private final HashSet<int[]> possibleMoves = new HashSet<>();

        protected Round(String pieceName, Tile originTile) {
            this.pieceName = pieceName;
            originX = originTile.getTileX();
            originY = originTile.getTileY();
        }

        protected HashSet<Tile> getMoves(boolean npc) {
            switch (pieceName) {
                case "king" -> getKingMoves();
                case "queen" -> getQueenMoves();
                case "bishop" -> getBishopMoves();
                case "knight" -> getKnightMoves();
                case "rook" -> getRookMoves();
                default -> getPawnMoves(npc);
            };
            return toTileSet();
        }

        private void getKingMoves() {

            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {

                    boolean newTile = x != originX && y != originY;
                    boolean tileExists = originX + x >= 0 && originY  + y >= 0;

                    if (newTile && tileExists) {
                        boolean isOccupied = tiles[x][y].isOccupied();
                        if (!isOccupied) possibleMoves.add(new int[] {originX + x, originY + y});
                    }
                }
            }
        }

        private void getQueenMoves() {

            for (int x = 0; x < 8; ++x) {
                for (int y = 0; y < 8; ++y) {

                    if (!tiles[x][y].isOccupied()) {
                        boolean verticalMove = x == originX && y != originY;
                        if (verticalMove) possibleMoves.add(new int[] {x, originY});

                        addIfDiagonal(x, y);
                    }
                }
                boolean horizontalMove = x != originX && !tiles[x][originY].isOccupied();
                if (horizontalMove) possibleMoves.add(new int[]{x, originY});
            }
        }

        private void getBishopMoves() {

            for (int x = 0; x < 8; ++ x) {
                for (int y = 0; y < 8; ++y) {

                    if (!tiles[x][y].isOccupied()) {
                        addIfDiagonal(x, y);
                    }
                }
            }
        }

        private void getKnightMoves() {

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

        private void getRookMoves() {

            for (int x = 0; x < 8; ++x) {
                if (!tiles[x][originY].isOccupied()) {
                    possibleMoves.add(new int[] {originX + x, originY});
                }
            }
            for (int y = 0; y < 8; ++y) {
                if (!tiles[originX][y].isOccupied()) {
                    possibleMoves.add(new int[] {originX, originY + y});
                }
            }
        }

        // TODO will npc ever be 'true'?
        private void getPawnMoves(boolean npc) {

            int moveDirection = npc ? -1 : 1;
            possibleMoves.add(new int[] {originX, originY + moveDirection});

            // starting row
            if (originY == 1 || originY == 6) {
                possibleMoves.add(new int[] {originX, originY + (moveDirection * 2)});
            }
        }

        private void addIfDiagonal(int x, int y) {
            if (x == originX || y == originY) return;

            boolean diagonal = Math.abs(x - originX) == Math.abs(y - originY);
            if (diagonal) possibleMoves.add(new int[] {x, y});
        }

        private HashSet<Tile> toTileSet() {
            HashSet<Tile> possibleTiles = new HashSet<>();

            for (int[] coordinate : possibleMoves) {
                int x = coordinate[0];
                int y = coordinate[1];
                possibleTiles.add(tiles[x][y]);
            }
            return possibleTiles;
        }
    }
}
