import javafx.beans.value.ObservableValue;
import javafx.scene.layout.*;
import java.util.HashSet;
import java.util.List;
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

    protected Tile[] finishMove(Tile destinationTile) {

        StackPane placeHolder = getTilePlaceHolder(destinationTile);
        updateChessBoard(destinationTile, placeHolder);

        boolean castled = isCastleMove(destinationTile);
        updateTileData(destinationTile, placeHolder, castled);

        currentMove.endMove();
        return new Tile[] { currentMove.getOriginTile(), destinationTile };
    }

    private boolean isCastleMove(Tile destinationTile) {

        if (destinationTile.isOccupied()) {
            Piece movedPiece = currentMove.getOriginTile().getPiece();
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

    private void updateChessBoard(Tile destinationTile, StackPane placeHolder) {

        Tile originTile = currentMove.getOriginTile();
        StackPane selectedNode = originTile.getNode();

        int originX = originTile.getTileX(), originY = originTile.getTileY();
        int destX = destinationTile.getTileX(), destY = destinationTile.getTileY();

        chessBoard.getChildren().remove(selectedNode);
        chessBoard.getChildren().remove(destinationTile.getNode());

        chessBoard.add(selectedNode, destX, destY);
        chessBoard.add(placeHolder, originX, originY);
    }

    private void updateTileData(Tile destinationTile, StackPane placeHolder, boolean castled) {

        Tile originTile = currentMove.getOriginTile();

        Piece replacementPiece = null;
        if (castled) {
            replacementPiece = destinationTile.getPiece();
            updateCastledPiece(replacementPiece, originTile);
        }
        Piece movedPiece = originTile.getPiece();
        updateMovedPiece(movedPiece, destinationTile);

        destinationTile.setPiece(movedPiece);
        destinationTile.setNode(originTile.getNode());

        originTile.setPiece(replacementPiece);
        originTile.setNode(placeHolder);
    }

    private void updateMovedPiece(Piece movedPiece, Tile destinationTile) {

        movedPiece.setTileOccupying(destinationTile);
        movedPiece.setLocation(destinationTile.getTileX(), destinationTile.getTileY());
        movedPiece.disableCastle();
    }

    private void updateCastledPiece(Piece castledPiece, Tile destinationTile) {

        castledPiece.setTileOccupying(destinationTile);
        castledPiece.setLocation(destinationTile.getTileX(), destinationTile.getTileY());
        castledPiece.disableCastle();
    }
}
