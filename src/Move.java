import java.util.HashSet;

class Move {

    private final Tile[][] tiles;
    private final HashSet<int[]> moveCoordinates = new HashSet<>();
    private final HashSet<Tile> destinationTiles = new HashSet<>();
    private final Tile originTile;
    private final int originX;
    private final int originY;
    private boolean inProgress;

    protected Move(Tile originTile, Tile[][] tiles) {
        this.tiles = tiles;

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

    protected HashSet<Tile> getDestinationTiles() {
        return destinationTiles;
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

            destinationTiles.add(tiles[x][y]);
        }
    }

    protected boolean hasPaths() {
        return !destinationTiles.isEmpty();
    }

    protected void selectPaths(boolean highlightTiles) {

        String transparent = "-fx-background-color: transparent;";
        String highlight = "-fx-background-color: " +
                "radial-gradient(focus-distance 0% , center 50% 50% , radius 100% , " +
                "rgba(0, 0, 0, 0), " +
                "rgba(58, 175, 220, 0.88));";

        String style = highlightTiles ? highlight : transparent;

        originTile.getNode().setStyle(style);
        originTile.setSelected(highlightTiles);

        for (Tile tile : destinationTiles) {
            tile.setDestinationTile(highlightTiles);
            tile.getNode().setStyle(style);
        }
    }

    private void addKingPaths() {
        // todo check for self sabotaging move
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {

                int xCoord = x + originX;
                int yCoord = y + originY;

                boolean newTile = x != 0 || y != 0;
                boolean tileExists = 0 <= xCoord && xCoord <= 7 && 0 <= yCoord && yCoord <= 7;

                if (newTile && tileExists) {
                    Tile possiblePath = tiles[xCoord][yCoord];

                    boolean isOccupied = possiblePath.isOccupied();
                    boolean isAvailable = false;

                    if (isOccupied) {
                        Piece neighboringPiece = possiblePath.getPiece();
                        if (neighboringPiece.getName().equals("rook") && neighboringPiece.isPlayer()) {
                            isAvailable = true;

                        } else if (!neighboringPiece.isPlayer()) {
                            isAvailable = true;
                        }
                    }

                    if (!isOccupied || isAvailable)
                        moveCoordinates.add(new int[] {xCoord, yCoord});
                }
            }
        }
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
                int xCoord = x + originX;
                int yCoord = y + originY;

                boolean newTile = x != 0 || y != 0;
                boolean tileExists = 0 <= xCoord && xCoord <= 7 && 0 <= yCoord && yCoord <= 7;
                boolean allowedMove = x != y && (Math.abs(x) == 2 && Math.abs(y) == 1) ||
                        (Math.abs(x) == 1 && Math.abs(y) == 2);

                if (newTile && tileExists && allowedMove) {
                    Tile possiblePath = tiles[xCoord][yCoord];

                    if (!possiblePath.isOccupied() ||
                            (possiblePath.isOccupied() && !possiblePath.getPiece().isPlayer())) {
                        moveCoordinates.add(new int[] {xCoord, yCoord});
                    }
                }
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
            int yCoord = originY - row;

            boolean tileExists = 0 <= yCoord;
            if (tileExists) {
                boolean isOccupied = tiles[originX][yCoord].isOccupied();

                if (!isOccupied)
                    moveCoordinates.add(new int[]{originX, yCoord});
            }
        }
        // overtaking opponent piece diagonally
        int yCoord = originY + 1;
        for (int col = -1; col <= 1; col += 2) {
            int xCoord = originX + col;

            boolean tileExists = 0 <= xCoord && xCoord <= 7 && 0 <= yCoord && yCoord <= 7;
            if (tileExists) {
                Tile possiblePath = tiles[xCoord][yCoord];
                if (possiblePath.isOccupied()) {
                    Piece occupyingPiece = possiblePath.getPiece();

                    if (!occupyingPiece.isPlayer())
                        moveCoordinates.add(new int[]{xCoord, yCoord});
                }
            }
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
        boolean continueUp = true;

        boolean tileExists = 0 <= row && row <= 7;
        if (!tileExists) return false;

        Tile possiblePath = tiles[col][row];
        if (possiblePath.isOccupied()) {
            if (!possiblePath.getPiece().isPlayer()) {
                moveCoordinates.add(new int[] {col, row});
            }
            continueUp = false;
        } else {
            moveCoordinates.add(new int[] {col, row});
        }
        return continueUp;
    }

    private void addHorizontalPaths() {
        // to the left of starting coordinate
        for (int col = originX - 1; 0 <= col; --col) {
            Tile possiblePath = tiles[col][originY];

            if (possiblePath.isOccupied()) {
                if (!possiblePath.getPiece().isPlayer()) {
                    moveCoordinates.add(new int[] {col, originY});
                }
                break;
            }
            moveCoordinates.add(new int[] {col, originY});
        }
        // to the right of starting coordinate
        for (int col = originX + 1; col <= 7; ++col) {
            Tile possiblePath = tiles[col][originY];

            if (possiblePath.isOccupied()) {
                if (!possiblePath.getPiece().isPlayer()) {
                    moveCoordinates.add(new int[] {col, originY});
                }
                break;
            }
            moveCoordinates.add(new int[] {col, originY});
        }
    }

    private void addVerticalPaths() {
        // below the starting coordinate
        for (int row = originY - 1; 0 <= row; --row) {
            Tile possiblePath = tiles[originX][row];

            if (possiblePath.isOccupied()) {
                if (!possiblePath.getPiece().isPlayer()) {
                    moveCoordinates.add(new int[] {originX, row});
                }
                break;
            }
            moveCoordinates.add(new int[] {originX, row});
        }
        // above the starting coordinate
        for (int row = originY + 1; row <= 7; ++row) {
            Tile possiblePath = tiles[originX][row];

            if (possiblePath.isOccupied()) {
                if (!possiblePath.getPiece().isPlayer()) {
                    moveCoordinates.add(new int[] {originX, row});
                }
                break;
            }
            moveCoordinates.add(new int[] {originX, row});
        }
    }
}
