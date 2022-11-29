import java.util.HashSet;

public class Engine {

    private final Tree tree = new Tree();

    protected Tile[] executeMove(Tile[][] board, int depth) {

//        long start = System.currentTimeMillis();
        Move move = findBestMove(board, depth);
//        long end = System.currentTimeMillis() - start;
//
//        Tile originTile = move.originTile;
//        Tile destTile = move.destinationTile;
//
//        System.out.println("Done; took " + end + " milliseconds.\n" +
//                "Move | Source: " + originTile.getTileX() + ", " + originTile.getTileY() +
//                " - Dest: " + destTile.getTileX() + ", " + destTile.getTileY());

        return new Tile[] { move.originTile, move.destinationTile };
    }

    private Move findBestMove(Tile[][] gameBoard, int depth) {
        int highestVal = Integer.MIN_VALUE;
        int currentVal;
        Move bestMove = null;

        Tile[][] clonedBoard = ShadowBoard.cloneBoard(gameBoard);
        for (Move move : ShadowBoard.getNPCMoves(clonedBoard)) {

            Tile[][] updatedBoard = ShadowBoard.shadowExecute(move, clonedBoard);
            currentVal = tree.min(updatedBoard, depth - 1);

            if (highestVal <= currentVal) {
                highestVal = currentVal;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int evaluateBoard(Tile[][] board, int depth) {
        int val = 0;
        for (Tile[] col : board) {
            for (Tile tile : col) {
                if (tile.isOccupied() && !tile.getPiece().isPlayer())
                    val += tile.getPiece().getValue();
            }
        }
        return val;
    }

    private class Tree {

        private int min(Tile[][] board, int depth) {

            if (depth == 0 /*|| check */)
                return evaluateBoard(board, depth);

            int lowestVal = Integer.MAX_VALUE;
            for (Move move : ShadowBoard.getPlayerMoves(board)) {

                Tile[][] updatedBoard = ShadowBoard.shadowExecute(move, board);
                int currentVal = max(updatedBoard, depth - 1);

                if (currentVal < lowestVal) lowestVal = currentVal;
            }
            return lowestVal;
        }

        private int max(Tile[][] board, int depth) {

            if (depth == 0 /*|| check */)
                return evaluateBoard(board, depth);

            int highestVal = Integer.MIN_VALUE;
            for (Move move : ShadowBoard.getNPCMoves(board)) {

                Tile[][] updatedBoard = ShadowBoard.shadowExecute(move, board);
                int currentVal = min(updatedBoard, depth - 1);

                if (highestVal < currentVal) highestVal = currentVal;
            }
            return highestVal;
        }
    }

    private static class ShadowBoard {

        private static HashSet<Move> getNPCMoves(Tile[][] board) {
            HashSet<Move> possibleMoves = new HashSet<>();

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

        private static HashSet<Move> getPlayerMoves(Tile[][] board) {
            HashSet<Move> possibleMoves = new HashSet<>();

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

        private static Tile[][] shadowExecute(Move move, Tile[][] board) {

            Tile originTile = cloneTile(move.originTile);
            Tile destinationTile = cloneTile(move.destinationTile);

            Piece pieceMoved = originTile.getPiece();
            Piece pieceRemoved = destinationTile.getPiece();

            destinationTile.setPiece(pieceMoved);
            originTile.setPiece(null);
            if (pieceRemoved != null)
                pieceRemoved.setActive(false);

            Tile[][] updatedBoard = cloneBoard(board);

            int sourceX = originTile.getTileX(), sourceY = originTile.getTileY();
            int destX = destinationTile.getTileX(), destY = destinationTile.getTileY();

            updatedBoard[sourceX][sourceY] = destinationTile;
            updatedBoard[destX][destY] = originTile;

            return updatedBoard;
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
