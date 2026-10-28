package software.ulpgc.chess.application.domain.model;

import java.util.Arrays;

import static software.ulpgc.chess.application.domain.model.Color.Black;
import static software.ulpgc.chess.application.domain.model.Color.White;

public enum Piece {
    WhitePawn('P'),
    WhiteRook('R'),
    WhiteKnight('N'),
    WhiteBishop('B'),
    WhiteQueen('Q'),
    WhiteKing('K'),

    BlackPawn('p'),
    BlackRook('r'),
    BlackKnight('n'),
    BlackBishop('b'),
    BlackQueen('q'),
    BlackKing('k');
    private final char symbol;
    Piece(char symbol) {
        this.symbol = symbol;
    }
    public char getSymbol() {return this.symbol;}
    Color color()
    {
        return isWhite() ? White : Black;
    }
    public static Piece getPiece(char symbol)
    {
        return Arrays.stream(Piece.values())
                .filter(p -> p.symbol == symbol)
                .findFirst()
                .orElse(null);
    }
    boolean isPawn()
    {
        return this == WhitePawn || this == BlackPawn;
    }
    boolean isRook()
    {
        return this == WhiteRook || this == BlackRook;
    }
    boolean isQueen()
    {
        return this == WhiteQueen || this == BlackQueen;
    }
    boolean isKnight()
    {
        return this == WhiteKnight || this == BlackKnight;
    }
    boolean isBishop()
    {
        return this == WhiteBishop || this == BlackBishop;
    }
    boolean isKing() {
        return this == WhiteKing || this == BlackKing;
    }
    private boolean isWhite()
    {
        return this == WhitePawn ||
                this == WhiteRook ||
                this == WhiteKnight ||
                this == WhiteQueen ||
                this == WhiteKing ||
                this == WhiteBishop;
    }
}
