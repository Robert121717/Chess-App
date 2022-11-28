import javafx.beans.value.ObservableValue;
import javafx.scene.layout.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Stack;

public class Game {

    private final GridPane chessBoard;
    private final Tile[][] tiles;
    private Player npc;
    private Player user;
    private final String userColor;
    private final String npcColor;

    private Move currentMove;

    protected Game(GridPane chessBoard, Tile[][] tiles, String userColor) {

        this.chessBoard = chessBoard;
        this.tiles = tiles;
        this.userColor = userColor;
        npcColor = userColor.equals("white") ? "black" : "white";
    }

    protected HashSet<Piece> loadPieces(CheckerBoard boardBackground) {
        chessBoard.getChildren().clear();
        chessBoard.add(boardBackground, 0, 0);

        currentMove = null;
        HashSet<Piece> pieces = new HashSet<>();

        npc = new Player(tiles, npcColor, true);
        pieces.addAll(npc.loadPieces());

        user = new Player(tiles, userColor, false);
        pieces.addAll(user.loadPieces());

        return pieces;
    }

    protected void newMove(Tile selectedTile) {
        if (currentMove != null && currentMove.isInProgress()) {
            currentMove.selectPaths(false);
        }
        currentMove = new Move(selectedTile, tiles);
        currentMove.addPathsForPiece(selectedTile.getPiece());

        if (currentMove.hasPaths()) currentMove.selectPaths(true);
        else currentMove.endMove();
    }

    protected void cancelMove() {
        currentMove.endMove();
    }

    protected Tile finishMove(Tile destinationTile) {

        boolean castled = isCastleMove(currentMove.getOriginTile(), destinationTile);

        StackPane placeHolder = getTilePlaceHolder(destinationTile);
        Tile originTile = currentMove.getOriginTile();
        StackPane selectedNode = originTile.getNode();

        updateChessBoard(destinationTile, originTile, placeHolder, selectedNode);
        updateTileData(destinationTile, originTile, placeHolder, selectedNode, castled);

        currentMove.endMove();
        System.out.println("GAME | Tile: " + originTile.hashCode() + ", Node: " + placeHolder.hashCode());
        System.out.println("GAME | Tile: " + destinationTile.hashCode() + ", Node: " + destinationTile.getNode().hashCode());
        return originTile;
    }

    private boolean isCastleMove(Tile originTile, Tile destinationTile) {

        if (destinationTile.isOccupied()) {
            Piece movedPiece = originTile.getPiece();
            Piece replacingPiece = destinationTile.getPiece();

            return movedPiece.isPlayer() && movedPiece.getName().equals("king")
                    && replacingPiece.isPlayer() && replacingPiece.getName().equals("rook");
        }
        return false;
    }

    private StackPane getTilePlaceHolder(Tile destinationTile) {

        if (destinationTile.isOccupied() && !destinationTile.getPiece().isPlayer()) {
            destinationTile.getPiece().setActive(false);
            return new StackPane();
        }
        return destinationTile.getNode();
    }

    private void updateChessBoard(Tile destinationTile, Tile originTile,
                                  StackPane placeHolder, StackPane selectedNode) {

        int originX = originTile.getTileX(), originY = originTile.getTileY();
        int destX = destinationTile.getTileX(), destY = destinationTile.getTileY();

        chessBoard.getChildren().remove(selectedNode);
        chessBoard.getChildren().remove(destinationTile.getNode());

        chessBoard.add(selectedNode, destX, destY);
        chessBoard.add(placeHolder, originX, originY);
    }

    private void updateTileData(Tile destinationTile, Tile originTile,
                                StackPane placeHolder, StackPane selectedNode, boolean castled) {

        Piece movedPiece = originTile.getPiece();
        movedPiece.setTileOccupying(destinationTile);
        movedPiece.setLocation(destinationTile.getTileX(), destinationTile.getTileY());

        Piece replacementPiece = null;
        if (castled) {
            replacementPiece = destinationTile.getPiece();
            replacementPiece.setTileOccupying(originTile);
            replacementPiece.setLocation(originTile.getTileX(), originTile.getTileY());
        }
        destinationTile.setPiece(movedPiece);
        destinationTile.setNode(selectedNode);

        originTile.setPiece(replacementPiece);
        originTile.setNode(placeHolder);
    }
}
