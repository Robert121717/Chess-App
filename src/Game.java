import javafx.scene.layout.*;

import java.util.*;

public class Game {

    private final GridPane chessBoard;
    private final Tile[][] board;
    private Player npc;
    private Player user;
    private final String userColor;
    private final String npcColor;

    private Round currentRound;

    protected Game(GridPane chessBoard, Tile[][] board, String userColor) {

        this.chessBoard = chessBoard;
        this.board = board;
        this.userColor = userColor;
        npcColor = userColor.equals("white") ? "black" : "white";
    }

    protected HashSet<Piece> loadPieces(CheckerBoard boardBackground) {
        chessBoard.getChildren().clear();
        chessBoard.add(boardBackground, 0, 0);

        currentRound = null;
        HashSet<Piece> pieces = new HashSet<>();

        npc = new Player(board, npcColor, true);
        pieces.addAll(npc.loadPieces());

        user = new Player(board, userColor, false);
        pieces.addAll(user.loadPieces());

        return pieces;
    }

    protected void makeNPCMove(Tile originTile, Tile destinationTile) {

        StackPane placeHolder = getTilePlaceHolder(destinationTile, true);

        updateChessBoard(originTile, destinationTile, placeHolder);
        updateTileData(originTile, destinationTile, placeHolder);
    }

    protected void newMove(Tile selectedTile) {
        if (currentRound != null && currentRound.isInProgress()) {
            currentRound.selectPaths(false);
        }
        currentRound = new Round(selectedTile, board, false);
        currentRound.addPathsForPiece(selectedTile.getPiece());

        if (currentRound.hasPaths()) currentRound.selectPaths(true);
        else currentRound.endRound();
    }

    protected void cancelMove() {
        currentRound.endRound();
    }

    protected List<Tile> finishMove(Tile destinationTile) {
        List<Tile> modifiedTiles = new ArrayList<>();

        if (isCastleMove(destinationTile)) {
            boolean toRight = getCastleDirection(destinationTile);
            modifiedTiles.addAll(castle(toRight));
        }
        Tile originTile = currentRound.getOriginTile();
        StackPane placeHolder = getTilePlaceHolder(destinationTile, false);

        updateChessBoard(originTile, destinationTile, placeHolder);
        updateTileData(originTile, destinationTile, placeHolder);

        currentRound.endRound();

        modifiedTiles.add(originTile);
        modifiedTiles.add(destinationTile);

        return modifiedTiles;
    }

    private List<Tile> castle(boolean toRight) {
        Tile kingTile = currentRound.getOriginTile();

        int kingX = kingTile.getTileX(), kingY = kingTile.getTileY();
        int rookOriginX = toRight ? kingX + 3 : kingX - 4;
        int rookDestX = toRight ? kingX + 1 : kingX - 1;

        Tile rookTile = board[rookOriginX][kingY];
        Tile destinationTile = board[rookDestX][kingY];
        StackPane placeHolder = destinationTile.getNode();

        updateChessBoard(rookTile, destinationTile, placeHolder);
        updateTileData(rookTile, destinationTile, placeHolder);

        currentRound.addDestinationTile(rookTile);
        return Arrays.asList(rookTile, destinationTile);
    }

    private boolean getCastleDirection(Tile destinationTile) {
        Tile originTile = currentRound.getOriginTile();

        int originX = originTile.getTileX();
        int destX = destinationTile.getTileX();

        return originX - destX <= 0;
    }

    private boolean isCastleMove(Tile destinationTile) {

        Tile originTile = currentRound.getOriginTile();
        int originX = originTile.getTileX(), originY = originTile.getTileY();

        Piece movedPiece = originTile.getPiece();
        if (movedPiece.isPlayer() && !movedPiece.getName().equals("king"))
            return false;

        else if (originX != 4 || originY != 7)
            return false;

        int destX = destinationTile.getTileX();
        return Math.abs(destX - originX) == 2;
    }

    private StackPane getTilePlaceHolder(Tile destinationTile, boolean npcMove) {

        if (destinationTile.isOccupied()) {
            if (npcMove && destinationTile.getPiece().isPlayer())
                destinationTile.getPiece().setActive(false);

            else if (!npcMove && !destinationTile.getPiece().isPlayer())
                destinationTile.getPiece().setActive(false);

            return new StackPane();
        }
        return destinationTile.getNode();
    }

    private void updateChessBoard(Tile originTile, Tile destinationTile, StackPane placeHolder) {
        StackPane selectedNode = originTile.getNode();

        int originX = originTile.getTileX(), originY = originTile.getTileY();
        int destX = destinationTile.getTileX(), destY = destinationTile.getTileY();

        chessBoard.getChildren().remove(selectedNode);
        chessBoard.getChildren().remove(destinationTile.getNode());

        chessBoard.add(selectedNode, destX, destY);
        chessBoard.add(placeHolder, originX, originY);
    }

    private void updateTileData(Tile originTile, Tile destinationTile, StackPane placeHolder) {

        Piece movedPiece = originTile.getPiece();
        updateMovedPiece(movedPiece, destinationTile);

        destinationTile.setPiece(movedPiece);
        destinationTile.setNode(originTile.getNode());

        originTile.setPiece(null);
        originTile.setNode(placeHolder);
    }

    private void updateMovedPiece(Piece movedPiece, Tile destinationTile) {

        movedPiece.setTileOccupying(destinationTile);
        movedPiece.setLocation(destinationTile.getTileX(), destinationTile.getTileY());
        movedPiece.disableCastle();
    }
}
