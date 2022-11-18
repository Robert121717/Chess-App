import java.util.HashSet;

public class Player {

    private final HashSet<Piece> pieces = new HashSet<>();
    private final Tile[][] tiles;
    private final String color;
    private final boolean npc;
    private final int backRow;

    protected Player(Tile[][] tiles, String color, boolean npc) {
        this.tiles = tiles;
        this.color = color;
        this.npc = npc;

        backRow = npc ? 0 : 7;
    }

    protected HashSet<Piece> loadPieces() {
        try {
            loadRooks();
            loadKnights();
            loadBishops();
            loadRoyalty();
            loadPawns();

        } catch (NullPointerException | IllegalArgumentException e) {
            String source = npc ? "NPC" : "player";
            System.out.println("Failed to load graphics for one or more " + source + " pieces. " +
                    "Please review none are missing from the Images folder.");
        }

        return pieces;
    }

    private void loadRooks() {
        final int width = 24;
        final int height = 35;

        for (int col = 0; col <= 7; col += 7) {
            Piece rook = new Piece(color, "rook", width, height);
            if (!npc) rook.setPlayer();

            rook.setLocation(col, backRow);
            rook.setTileOccupying(tiles[col][backRow]);

            tiles[col][backRow].setPiece(rook);
            tiles[col][backRow].setNode(rook.getNode());

            pieces.add(rook);
        }
    }

    private void loadKnights() {
        final int width = 40;
        final int height = 37;

        for (int col = 1; col <= 6; col += 5) {
            Piece knight = new Piece(color, "knight", width, height);
            if (!npc) knight.setPlayer();

            knight.setLocation(col, backRow);
            knight.setTileOccupying(tiles[col][backRow]);

            tiles[col][backRow].setPiece(knight);
            tiles[col][backRow].setNode(knight.getNode());

            pieces.add(knight);
        }
    }

    private void loadBishops() {
        final int width = 38;
        final int height = 40;

        for (int col = 2; col <= 5; col += 3) {
            Piece bishop = new Piece(color, "bishop", width, height);
            if (!npc) bishop.setPlayer();

            bishop.setLocation(col, backRow);
            bishop.setTileOccupying(tiles[col][backRow]);

            tiles[col][backRow].setPiece(bishop);
            tiles[col][backRow].setNode(bishop.getNode());

            pieces.add(bishop);
        }
    }

    private void loadRoyalty() {

        Piece queen = new Piece(color, "queen", 41, 40);
        queen.setLocation(3, backRow);
        queen.setTileOccupying(tiles[3][backRow]);

        tiles[3][backRow].setPiece(queen);
        tiles[3][backRow].setNode(queen.getNode());
        pieces.add(queen);

        Piece king = new Piece(color, "king", 43, 40);
        king.setLocation(4, backRow);
        king.setTileOccupying(tiles[4][backRow]);

        tiles[4][backRow].setPiece(king);
        tiles[4][backRow].setNode(king.getNode());
        pieces.add(king);

        if (!npc) {
            queen.setPlayer();
            king.setPlayer();
        }
    }

    private void loadPawns() {
        final int frontRow = npc ? 1 : 6;

        for (int col = 0; col <= 7; ++col) {
            Piece pawn = new Piece(color, "pawn", 21, 20);
            if (!npc) pawn.setPlayer();

            pawn.setLocation(col, frontRow);
            pawn.setTileOccupying(tiles[col][frontRow]);

            tiles[col][frontRow].setPiece(pawn);
            tiles[col][frontRow].setNode(pawn.getNode());

            pieces.add(pawn);
        }
    }
}
