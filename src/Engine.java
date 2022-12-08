import java.util.ArrayList;
import java.util.List;

public class Engine {

    private final Player npc;
    private final Player user;

    protected Engine(Player npc, Player user) {
        this.npc = npc;
        this.user = user;
    }

    // todo check for pawn going to end of board
    protected Tile[] findBestMove(Tile[][] gameBoard, int depth) {
        int highestVal = Integer.MIN_VALUE;
        int currentVal;
        Move bestMove = null;

        Tile[][] clonedBoard = Board.cloneBoard(gameBoard);
//        print(Board.getNPCMoves(clonedBoard));
        for (Move move : Board.getNPCMoves(clonedBoard)) {

            Tile[][] updatedBoard = Board.executeMove(move, clonedBoard);
            currentVal = max(updatedBoard, depth - 1);

            if (highestVal <= currentVal) {
                highestVal = currentVal;
                bestMove = move;
            }
        }
        if (bestMove == null) return null;
        return new Tile[] { bestMove.originTile, bestMove.destinationTile };
    }

    private void print(List<Move> moves) {
        for (Move move : moves) {
            Tile originTile = move.originTile;
            Tile destTile = move.destinationTile;

            System.out.println("Potential Move | (" + originTile.getTileX() + ", " + originTile.getTileY() +
                    ") to (" + destTile.getTileX() + ", " + destTile.getTileY() + ")");
        }
    }

    private void print(Move move) {
        Tile destTile = move.destinationTile;

        if (destTile.isOccupied() && !destTile.getPiece().isPlayer())
            System.out.println("Attack | (" + move.originTile.getTileX() + ", " + move.originTile.getTileY() +
                    ") to (" + destTile.getTileX() + ", " + destTile.getTileY() + ")");
    }

    private int evaluateBoard(Tile[][] board, int depth) {

        int pieceVal = getPieceValues(board);
        int availableMoves = Board.getNPCMoves(board).size();

        if (depth != 0) pieceVal *= depth;

        return pieceVal + availableMoves;
    }

    private int getPieceValues(Tile[][] board) {
        int npcVal = 0, inactiveVal = 0;

        for (Tile[] col : board) {
            for (Tile tile : col) {
                if (tile.isOccupied()) {
                    Piece occupyingPiece = tile.getPiece();

                    if (occupyingPiece.isActive() && !occupyingPiece.isPlayer()) {
                        npcVal += occupyingPiece.getValue();

                    } else if (!occupyingPiece.isActive() && occupyingPiece.isPlayer())
                        inactiveVal += occupyingPiece.getValue();
                }
            }
        }
        return inactiveVal != 0 ? inactiveVal * npcVal : npcVal;
    }

    private int min(Tile[][] board, int depth) {

        if (depth == 0)
            return evaluateBoard(board, depth);

        int lowestVal = Integer.MAX_VALUE;
        for (Move move : Board.getPlayerMoves(board)) {

            Tile[][] updatedBoard = Board.executeMove(move, board);
            int currentVal = max(updatedBoard, depth - 1);

            if (currentVal < lowestVal) lowestVal = currentVal;
            Board.resetBoard(move, board);
        }
        return lowestVal;
    }

    private int max(Tile[][] board, int depth) {

        if (depth == 0 || userInCheck(board))
            return evaluateBoard(board, depth);

        int highestVal = Integer.MIN_VALUE;
        for (Move move : Board.getNPCMoves(board)) {

            Tile[][] updatedBoard = Board.executeMove(move, board);
            int currentVal = min(updatedBoard, depth - 1);

            if (highestVal < currentVal) highestVal = currentVal;
            Board.resetBoard(move, board);
        }
        return highestVal;
    }

    private boolean userInCheck(Tile[][] board) {
        return Board.hasCheck(board, user) || Board.hasCheckMate(board, user) || Board.hasStaleMate(board, user);
    }

    private static class Board {

        private static List<Move> getNPCMoves(Tile[][] board) {
            List<Move> possibleMoves = new ArrayList<>();

            for (Tile[] col : board) {
                for (Tile tile : col) {

                    if (tile.isOccupied() && !tile.getPiece().isPlayer()) {
                        Round round = new Round(tile, board, true);
                        round.addPathsForPiece(tile.getPiece());

                        for (Tile destTile : round.getDestinationTiles()) {
                            possibleMoves.add(new Move(tile, destTile));
                        }
                    }
                }
            }
            return possibleMoves;
        }

        private static List<Move> getPlayerMoves(Tile[][] board) {
            List<Move> possibleMoves = new ArrayList<>();

            for (Tile[] col : board) {
                for (Tile tile : col) {

                    if (tile.isOccupied() && tile.getPiece().isPlayer()) {
                        Round round = new Round(tile, board, false);
                        round.addPathsForPiece(tile.getPiece());

                        for (Tile destTile : round.getDestinationTiles()) {
                            possibleMoves.add(new Move(tile, destTile));
                        }
                    }
                }
            }
            return possibleMoves;
        }

        protected static boolean hasCheck(Tile[][] board, Player user) {

            return false;
        }

        protected static boolean hasCheckMate(Tile[][] board, Player user) {

            return false;
        }

        protected static boolean hasStaleMate(Tile[][] board, Player user) {

            return  false;
        }

        private static Tile cloneTile(Tile source) {

            int x = source.getTileX(), y = source.getTileY();
            Tile clone = new Tile(x, y, 55);

            if (source.isOccupied()) {
                Piece originalPiece = source.getPiece();
                clone.setPiece(clonePiece(originalPiece, source));
            }
            return clone;
        }

        private static Piece clonePiece(Piece source, Tile tileOccupying) {

            double[] dimensions = source.getDimensions();
            int[] location = source.getLocation();

            Piece clone = new Piece(source.getAlliance(), source.getName(), dimensions[0], dimensions[1]);
            clone.setLocation(location[0], location[1]);
            clone.setTileOccupying(tileOccupying);
            clone.setActive(source.isActive());
            if (source.isPlayer()) clone.setPlayer();

            return clone;
        }

        private static Tile[][] executeMove(Move move, Tile[][] board) {

            Tile originTile = cloneTile(move.originTile);
            Tile destinationTile = cloneTile(move.destinationTile);

            Piece pieceMoved = originTile.getPiece();
            Piece pieceRemoved = destinationTile.getPiece();

            destinationTile.setPiece(pieceMoved);
            originTile.setPiece(null);
            if (pieceRemoved != null) pieceRemoved.setActive(false);

            int sourceX = originTile.getTileX(), sourceY = originTile.getTileY();
            int destX = destinationTile.getTileX(), destY = destinationTile.getTileY();

            board[sourceX][sourceY] = destinationTile;
            board[destX][destY] = originTile;

            return board;
        }

        private static void resetBoard(Move move, Tile[][] board) {

            Tile originTile = move.originTile;
            Tile destTile = move.destinationTile;

            int originX = originTile.getTileX(), originY = originTile.getTileY();
            int destX = destTile.getTileX(), destY = destTile.getTileY();

            board[originX][originY] = originTile;
            board[destX][destY] = originTile;
        }

        private static Tile[][] cloneBoard(Tile[][] board) {
            Tile[][] clone = new Tile[8][8];

            for (int x = 0; x < 8; ++x) {
                for (int y = 0; y < 8; ++y) {
                    clone[x][y] = cloneTile(board[x][y]);
                }
            }
            return clone;
        }
    }

    private record Move(Tile originTile, Tile destinationTile) {}
}
