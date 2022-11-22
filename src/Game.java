import javafx.scene.layout.*;
import java.util.HashSet;

public class Game {

    private final GridPane chessBoard;
    private final Tile[][] tiles;
    private Player npc;
    private Player user;
    private final String userColor;
    private final String npcColor;

    private Round currentRound;

    protected Game(GridPane chessBoard, Tile[][] tiles, String userColor) {

        this.chessBoard = chessBoard;
        this.tiles = tiles;
        this.userColor = userColor;
        npcColor = userColor.equals("white") ? "black" : "white";
    }

    protected HashSet<Piece> loadPieces(CheckerBoard boardBackground) {
        chessBoard.getChildren().clear();
        chessBoard.add(boardBackground, 0, 0);

        currentRound = null;
        HashSet<Piece> pieces = new HashSet<>();

        npc = new Player(tiles, npcColor, true);
        pieces.addAll(npc.loadPieces());

        user = new Player(tiles, userColor, false);
        pieces.addAll(user.loadPieces());

        return pieces;
    }

    protected void newRound(Tile selectedTile) {
        if (currentRound != null && currentRound.isInProgress()) {
            currentRound.selectTiles(false);
        }
        currentRound = new Round(selectedTile);
        currentRound.createPathOptions();

        if (currentRound.hasPaths())
            currentRound.selectTiles(true);
        else currentRound.endRound();
    }

    protected void cancelRound() {
        currentRound.endRound();
    }

    protected Tile finishRound(Tile destinationTile) {

        StackPane placeHolder = getTilePlaceHolder(destinationTile);

        Tile originTile = currentRound.getOriginTile();
        StackPane selectedNode = originTile.getNode();

        updateChessBoard(destinationTile, originTile, selectedNode, placeHolder);
        updateTileData(destinationTile, originTile, selectedNode, placeHolder);

        currentRound.endRound();
        return originTile;
    }

    private StackPane getTilePlaceHolder(Tile destinationTile) {
        StackPane placeHolder;

        if (destinationTile.isOccupied()) {
            destinationTile.getPiece().setActive(false);
            placeHolder = new StackPane();

        } else {
            placeHolder = destinationTile.getNode();
        }
        return placeHolder;
    }

    private void updateChessBoard(Tile destinationTile, Tile originTile,
                                  StackPane selectedNode, StackPane placeHolder) {

        int originX = originTile.getTileX(), originY = originTile.getTileY();
        int destX = destinationTile.getTileX(), destY = destinationTile.getTileY();

        chessBoard.getChildren().remove(selectedNode);
        chessBoard.getChildren().remove(destinationTile.getNode());

        chessBoard.add(selectedNode, destX, destY);
        chessBoard.add(placeHolder, originX, originY);
    }

    private void updateTileData(Tile destinationTile, Tile originTile,
                                StackPane selectedNode, StackPane placeHolder) {

        Piece movedPiece = originTile.getPiece();
        movedPiece.setTileOccupying(destinationTile);

        destinationTile.setPiece(movedPiece);
        destinationTile.setNode(selectedNode);

        originTile.setPiece(null);
        originTile.setNode(placeHolder);
    }

    private class Round {

        private final HashSet<Tile> destinationTiles = new HashSet<>();
        private final Tile originTile;
        private boolean inProgress;

        protected Round(Tile originTile) {
            this.originTile = originTile;

            originTile.setSelected(true);
            inProgress = true;
        }

        protected Tile getOriginTile() {
            return originTile;
        }

        protected boolean isInProgress() {
            return inProgress;
        }

        protected void endRound() {
            inProgress = false;
            selectTiles(false);
        }

        protected void createPathOptions() {

            int originX = originTile.getTileX();
            int originY = originTile.getTileY();

            Piece.Path path = new Piece.Path(tiles, originX, originY);
            toTileSet(path.getPathOptions(originTile.getPiece().getName()));
        }

        private void toTileSet(HashSet<int[]> possibleMoves) {

            for (int[] coordinate : possibleMoves) {
                int x = coordinate[0];
                int y = coordinate[1];

                destinationTiles.add(tiles[x][y]);
            }
        }

        protected void selectTiles(boolean selected) {

            String transparent = "-fx-background-color: transparent;";
            String highlight = "-fx-background-color: " +
                    "radial-gradient(focus-distance 0% , center 50% 50% , radius 100% , " +
                    "rgba(0, 0, 0, 0), " +
                    "rgba(58, 175, 220, 0.88));";

            String style = selected ? highlight : transparent;

            originTile.getNode().setStyle(style);
            if (!selected) originTile.setSelected(false);

            for (Tile tile : destinationTiles) {
                tile.setDestinationTile(selected);
                tile.getNode().setStyle(style);
            }
        }

        protected boolean hasPaths() {
            return !destinationTiles.isEmpty();
        }
    }
}
