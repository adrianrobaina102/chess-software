package software.ulpgc.chess.application.domain.model;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static software.ulpgc.chess.application.domain.model.Square.*;
import static software.ulpgc.chess.application.domain.model.Piece.*;
public final class Board {
    private final Map<Square, Piece> pieces;
    public Board(Map<Square, Piece> pieces) {
        this.pieces = Map.copyOf(pieces);
    }
    public static Board initial(){

        return new Board(initialMap());
    }
    private static Map<Square, Piece> initialMap()
    {
        return Map.ofEntries(
                Map.entry(at("a1"), WhiteRook),
                Map.entry(at("b1"), WhiteKnight),
                Map.entry(at("c1"), WhiteBishop),
                Map.entry(at("d1"), WhiteQueen),
                Map.entry(at("e1"), WhiteKing),
                Map.entry(at("f1"), WhiteBishop),
                Map.entry(at("g1"), WhiteKnight),
                Map.entry(at("h1"), WhiteRook),

                Map.entry(at("a2"), WhitePawn),
                Map.entry(at("b2"), WhitePawn),
                Map.entry(at("c2"), WhitePawn),
                Map.entry(at("d2"), WhitePawn),
                Map.entry(at("e2"), WhitePawn),
                Map.entry(at("f2"), WhitePawn),
                Map.entry(at("g2"), WhitePawn),
                Map.entry(at("h2"), WhitePawn),

                Map.entry(at("a7"), BlackPawn),
                Map.entry(at("b7"), BlackPawn),
                Map.entry(at("c7"), BlackPawn),
                Map.entry(at("d7"), BlackPawn),
                Map.entry(at("e7"), BlackPawn),
                Map.entry(at("f7"), BlackPawn),
                Map.entry(at("g7"), BlackPawn),
                Map.entry(at("h7"), BlackPawn),

                Map.entry(at("a8"), BlackRook),
                Map.entry(at("b8"), BlackKnight),
                Map.entry(at("c8"), BlackBishop),
                Map.entry(at("d8"), BlackQueen),
                Map.entry(at("e8"), BlackKing),
                Map.entry(at("f8"), BlackBishop),
                Map.entry(at("g8"), BlackKnight),
                Map.entry(at("h8"), BlackRook)
        );
    }
    public Board move(Square from, Square to){
        if (!pieces.containsKey(from)){return this;}
        Map<Square, Piece> newMap = new HashMap<>(this.pieces);
        newMap.put(to, newMap.remove(from));
        return new Board(newMap);
    }
    public Board move(Move move){
        return move(move.from(), move.to());
    }
    public Piece pieceAt(File file, Rank rank) {
        return pieceAt(new Square(file, rank));
    }

    public Piece pieceAt(String square) {
        return pieceAt(at(square));
    }

    Piece pieceAt(Square square) {
        return pieces.get(square);
    }

    public Map<Square, Piece> pieces() {
        return pieces;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Board) obj;
        return Objects.equals(this.pieces, that.pieces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieces);
    }

    @Override
    public String toString() {
        return "Board[" +
                "pieces=" + pieces + ']';
    }


}
