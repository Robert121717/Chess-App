import javafx.scene.layout.*;
import java.util.HashSet;

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

        StackPane placeHolder = getTilePlaceHolder(destinationTile);

        Tile originTile = currentMove.getOriginTile();
        StackPane selectedNode = originTile.getNode();

        updateChessBoard(destinationTile, originTile, selectedNode, placeHolder);
        updateTileData(destinationTile, originTile, selectedNode, placeHolder);

        currentMove.endMove();
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
        movedPiece.setLocation(destinationTile.getTileX(), destinationTile.getTileY());

        destinationTile.setPiece(movedPiece);
        destinationTile.setNode(selectedNode);

        originTile.setPiece(null);
        originTile.setNode(placeHolder);
    }
}
