import java.util.HashSet;

class Move {

    private final Tile[][] board;
    private final HashSet<int[]> moveCoordinates = new HashSet<>();
    private final HashSet<Tile> destinationTiles = new HashSet<>();
    private final Tile originTile;
    private final int originX;
    private final int originY;
    private boolean inProgress;

    protected Move(Tile originTile, Tile[][] board) {
        this.board = board;

        this.originTile = originTile;
        originX = originTile.getTileX();
        originY = originTile.getTileY();

        inProgress = true;
    }

    protected Tile getOriginTile() {
        return originTile;
    }

    protected boolean isInProgress() {
        return inProgress;
    }

    protected void endMove() {
        inProgress = false;
        selectPaths(false);
    }

    protected void addPathsForPiece(Piece piece) {

        switch (piece.getName()) {
            case "king" -> addKingPaths();
            case "queen" -> addQueenPaths();
            case "bishop" -> addBishopPaths();
            case "knight" -> addKnightPaths();
            case "rook" -> addRookPaths();
            default -> addPawnPaths();
        }
        toTileSet(moveCoordinates);
    }

    private void toTileSet(HashSet<int[]> possibleMoves) {

        for (int[] coordinate : possibleMoves) {
            int x = coordinate[0];
            int y = coordinate[1];

            destinationTiles.add(board[x][y]);
        }
    }

    protected boolean hasPaths() {
        return !destinationTiles.isEmpty();
    }

    protected void selectPaths(boolean active) {

        String transparent = "-fx-background-color: transparent;";
        String highlight = "-fx-background-color: " +
                "radial-gradient(focus-distance 0% , center 50% 50% , radius 100% , " +
                "rgba(0, 0, 0, 0), " +
                "rgba(58, 175, 220, 0.88));";

        String style = active ? highlight : transparent;

        originTile.getNode().setStyle(style);
        originTile.setSelected(active);

        for (Tile tile : destinationTiles) {
            tile.setDestinationTile(active);
            tile.getNode().setStyle(style);
        }
    }

    private void addKingPaths() {
        // todo check for self sabotaging move, castling
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                Path path = new Path(originX, originY, x, y);

                if (path.isAvailable()) {
                    moveCoordinates.add(new int[] { path.getDestX(), path.getDestY() });
                }
            }
        }
        addCastlePaths();
    }

    private void addCastlePaths() {

        Path leftCastlePath = new Path(originX, originY, -4, 0);
        if (leftCastlePath.canCastle(false))
            moveCoordinates.add(new int[] { leftCastlePath.getDestX(), leftCastlePath.getDestY() });

        Path rightCastlePath = new Path(originX, originY, 3, 0);
        if (rightCastlePath.canCastle(true))
            moveCoordinates.add(new int[] { rightCastlePath.getDestX(), rightCastlePath.getDestY() });
    }

    private void addQueenPaths() {
        addDiagonalPaths();
        addHorizontalPaths();
        addVerticalPaths();
    }

    private void addBishopPaths() {
        addDiagonalPaths();
    }

    private void addKnightPaths() {

        for (int x = -2; x <= 2; ++x) {
            for (int y = -2; y <= 2; ++y) {
                Path path = new Path(originX, originY, x, y);

                if (path.isAvailable() && path.isKnightPath())
                    moveCoordinates.add(new int[] { path.getDestX(), path.getDestY() });
            }
        }
    }

    private void addRookPaths() {
        addHorizontalPaths();
        addVerticalPaths();
    }

    private void addPawnPaths() {
        // todo en passant

        // moving forward one or two spaces
        int iterations = originY == 6 ? 2 : 1;
        for (int row = 1; row <= iterations; ++row) {
            Path path = new Path(originX, originY, 0, -row);

            if (path.exists() && !path.isOccupied())
                moveCoordinates.add(new int[]{path.getDestX(), path.getDestY()});
        }
        // attacking opponent piece diagonally
        for (int col = -1; col <= 1; col += 2) {
            Path path = new Path(originX, originY, col, -1);

            if (path.exists() && path.hasOpponent())
                moveCoordinates.add(new int[] { path.getDestX(), path.getDestY() });
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

        Path path = new Path(originX, originY, col - originX, row - originY);
        if (path.isAvailable())
            moveCoordinates.add(new int[] { path.getDestX(), path.getDestY() });

        return path.exists() && !path.isOccupied();
    }

    private void addHorizontalPaths() {

        for (int col = originX - 1; 0 <= col; --col) {
            int deltaX = -1 * (originX  -col);

            Path path = new Path(originX, originY, deltaX, 0);
            if (path.isAvailable())
                moveCoordinates.add(new int[] { path.getDestX(), path.getDestY() });

            if (path.isOccupied()) break;
        }

        for (int col = originX + 1; col <= 7; ++col) {
            int deltaX = col - originX;

            Path path = new Path(originX, originY, deltaX, 0);
            if (path.isAvailable())
                moveCoordinates.add(new int[] { path.getDestX(), path.getDestY() });

            if (path.isOccupied()) break;
        }
    }

    private void addVerticalPaths() {

        for (int row = originY - 1; 0 <= row; --row) {
            int deltaY = -1 * (originY - row);

            Path path = new Path(originX, originY, 0, deltaY);
            if (path.isAvailable())
                moveCoordinates.add(new int[] { path.getDestX(), path.getDestY() });

            if (path.isOccupied()) break;
        }
        for (int row = originY + 1; row <= 7; ++row) {
            int deltaY = row - originY;

            Path path = new Path(originX, originY, 0, deltaY);
            if (path.isAvailable())
                moveCoordinates.add(new int[] { path.getDestX(), path.getDestY() });

            if (path.isOccupied()) break;
        }
    }

    private class Path {

        private final int deltaX;
        private final int deltaY;
        private final int destX;
        private final int destY;

        // todo second constructor with different object array, same name (should work)
        // can also check if one is null then use the other for helper methods or use instanceOf
        private Path(int sourceX, int sourceY, int deltaX, int deltaY) {
            this.deltaX = deltaX;
            this.deltaY = deltaY;

            destX = sourceX + deltaX;
            destY = sourceY + deltaY;
        }

        private boolean isOccupied() {
            return board[destX][destY].isOccupied();
        }

        private boolean hasOpponent() {
            return isOccupied() && !board[destX][destY].getPiece().isPlayer();
        }

        private boolean isAvailable() {
            if (!exists()) return false;

            if (board[destX][destY].isOccupied())
                return !board[destX][destY].getPiece().isPlayer();
            else
                return true;
        }

        private boolean exists() {
            boolean isNew = 0 != deltaX || 0 != deltaY;
            return isNew && 0 <= destX && destX <= 7 && 0 <= destY && destY <= 7;
        }

        private boolean isKnightPath() {
            return deltaX != deltaY && (Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) ||
                    (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 2);
        }

        private boolean canCastle(boolean toRight) {
            if (originX != 4 && originY != 0) return false;

            boolean vacantTileGap = true;
            int start = toRight ? originX + 1: destX + 1;
            int end = toRight ? destX : originX;

            for (int col = start; col < end; ++col) {
                if (board[col][destY].isOccupied()) {
                    vacantTileGap = false;
                    break;
                }
            }
            Tile destTile = board[destX][destY];
            boolean hasRook = destTile.isOccupied() &&
                    destTile.getPiece().isPlayer() &&
                    destTile.getPiece().getName().equals("rook");

            return vacantTileGap && hasRook;
        }

        private int getDestX() {
            return destX;
        }

        private int getDestY() {
            return destY;
        }
    }
}
