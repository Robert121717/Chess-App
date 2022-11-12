import javafx.scene.layout.GridPane;
import java.util.HashMap;

public class Player {

    private final HashMap<String, Piece> pieces = new HashMap<>();
    private final GridPane chessBoard;
    private final Tile[][] tiles;
    private final String color;
    private final boolean npc;
    private final int backRow;

    protected Player(GridPane chessBoard, Tile[][] tiles, String color, boolean npc) {
        this.chessBoard = chessBoard;
        this.tiles = tiles;

        this.color = color;
        this.npc = npc;

        backRow = npc ? 0 : 7;
    }

    protected void loadPieces() throws NullPointerException {
        loadRooks();
        loadKnights();
        loadBishops();
        loadRoyalty();
        loadPawns();
    }

    private void loadRooks() {
        final int width = 24;
        final int height = 35;
        final int row = npc ? 0 : 7;

        Piece leftRook = new Piece(color, "rook", width, height);
        Piece rightRook = new Piece(color, "rook", width, height);

        leftRook.setLocation(row + "0");
        rightRook.setLocation(row + "7");

        tiles[row][0].setPiece(leftRook);
        tiles[row][7].setPiece(rightRook);

        chessBoard.add(leftRook.getNode(), 0, row);
        chessBoard.add(rightRook.getNode(), 7, row);

        pieces.put(leftRook.getLocation(), leftRook);
        pieces.put(rightRook.getLocation(), rightRook);
    }

    private void loadKnights() {
        final int width = 40;
        final int height = 37;

        Piece leftKnight = new Piece(color, "knight", width, height);
        Piece rightKnight = new Piece(color, "knight", width, height);

        leftKnight.setKnight("left");
        rightKnight.setKnight("right");

        leftKnight.setLocation(backRow + "1");
        rightKnight.setLocation(backRow + "6");

        tiles[backRow][1].setPiece(leftKnight);
        tiles[backRow][6].setPiece(rightKnight);

        chessBoard.add(leftKnight.getNode(), 1, backRow);
        chessBoard.add(rightKnight.getNode(), 6, backRow);

        pieces.put(leftKnight.getLocation(), leftKnight);
        pieces.put(rightKnight.getLocation(), rightKnight);
    }

    private void loadBishops() {
        final int width = 38;
        final int height = 40;

        Piece leftBishop = new Piece(color, "bishop", width, height);
        Piece rightBishop = new Piece(color, "bishop", width, height);

        leftBishop.setLocation(backRow + "2");
        rightBishop.setLocation(backRow + "5");

        tiles[backRow][2].setPiece(leftBishop);
        tiles[backRow][5].setPiece(rightBishop);

        chessBoard.add(leftBishop.getNode(), 2, backRow);
        chessBoard.add(rightBishop.getNode(), 5, backRow);

        pieces.put(leftBishop.getLocation(), leftBishop);
        pieces.put(leftBishop.getLocation(), leftBishop);
    }

    private void loadRoyalty() {

        Piece queen = new Piece(color, "queen", 41, 40);
        Piece king = new Piece(color, "king", 43, 40);

        queen.setLocation(backRow + "3");
        king.setLocation(backRow + "4");

        tiles[backRow][3].setPiece(queen);
        tiles[backRow][4].setPiece(king);

        chessBoard.add(queen.getNode(), 3, backRow);
        chessBoard.add(king.getNode(), 4, backRow);

        pieces.put(queen.getLocation(), queen);
        pieces.put(king.getLocation(), king);
    }

    private void loadPawns() {
        final int frontRow = npc ? 1 : 6;

        for (int i = 0; i < 8; ++i) {

            Piece pawn = new Piece(color, "pawn", 21, 28);
            pawn.setLocation(frontRow + String.valueOf(i));

            tiles[i][frontRow].setPiece(pawn);
            chessBoard.add(pawn.getNode(), i, frontRow);
            pieces.put(pawn.getLocation(), pawn);
        }
    }
}
