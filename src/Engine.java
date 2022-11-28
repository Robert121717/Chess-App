import java.util.HashSet;

public class Engine {

    private final Tile[][] tiles;
    private final Player npc;

    protected Engine(Tile[][] tiles, Player npc) {
        this.tiles = tiles;
        this.npc = npc;
    }

    protected int evaluateBoard(Tile[][] board, int depth) {
        return 0;
    }

    private class Tree {

        int currentVal;

        private int min(Tile[][] board, int depth) {
            if (depth == 0 /*|| check */) {
                return evaluateBoard(board, depth);
            }
            int lowestVal = Integer.MAX_VALUE;


            return 0;
        }

        private int max(Tile[][] board, int depth) {
            if (depth == 0 /*|| check */) {
                return evaluateBoard(board, depth);
            }
            int highestVal = Integer.MIN_VALUE;
            for (Move move : npc.getPossibleMoves(board)) {

            }

            return 0;
        }

    }

    private static class ShadowMove {



        protected static HashSet<Tile> getPossibleMoves(Tile[][] board) {
//            HashSet<Piece>
            return null;
        }

        private static HashSet<Piece> getPieces(Tile[][] board) {
            HashSet<Piece> pieces = new HashSet<>();

            for (Tile[] col : board) {
                for (Tile tile : col) {
                    Piece piece = tile.getPiece();
                    if (!piece.isPlayer()) pieces.add(piece);
                }
            }
            return pieces;
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

        protected static Tile[][] shadowExecute(Tile originTile, Tile destinationTile) {

            Tile originTileClone = cloneTile(originTile);
            Tile destTileClone = cloneTile(destinationTile);

            Piece pieceMoved = originTileClone.getPiece();
            Piece pieceRemoved = destTileClone.getPiece();

            destTileClone.setPiece(pieceMoved);
            originTileClone.setPiece(null);
            if (pieceRemoved != null)
                pieceRemoved.setActive(false);

//            Tile[][] boardClone = cloneBoard();

            int sourceX = originTile.getTileX(), sourceY = originTile.getTileY();
            int destX = destinationTile.getTileX(), destY = destinationTile.getTileY();

//            boardClone[sourceX][sourceY] = originTileClone;
//            boardClone[destX][destY] = destTileClone;

//            return boardClone;
            return null;
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

            Piece clone = new Piece(source.getColor(), source.getName(), dimensions[0], dimensions[1]);
            clone.setLocation(location[0], location[1]);
            clone.setTileOccupying(tileOccupying);
            clone.setActive(source.isActive());

            return clone;
        }
    }
}
