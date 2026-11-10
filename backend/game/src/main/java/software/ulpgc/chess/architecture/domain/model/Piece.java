package software.ulpgc.chess.architecture.domain.model;

import java.util.Arrays;

import static software.ulpgc.chess.architecture.domain.model.Color.Black;
import static software.ulpgc.chess.architecture.domain.model.Color.White;

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
    public char symbol() {
        return this.symbol;
    }

    public char getSymbol() {
        return symbol();
    }

    public Color color()
    {
        return isWhite() ? White : Black;
    }

    public static Piece from(char symbol)
    {
        return Arrays.stream(Piece.values())
                .filter(p -> p.symbol == symbol)
                .findFirst()
                .orElse(null);
    }

    public static Piece getPiece(char symbol)
    {
        return from(symbol);
    }

    public boolean isPawn()
    {
        return this == WhitePawn || this == BlackPawn;
    }

    public boolean isRook()
    {
        return this == WhiteRook || this == BlackRook;
    }

    public boolean isQueen()
    {
        return this == WhiteQueen || this == BlackQueen;
    }

    public boolean isKnight()
    {
        return this == WhiteKnight || this == BlackKnight;
    }

    public boolean isBishop()
    {
        return this == WhiteBishop || this == BlackBishop;
    }

    public boolean isKing() {
        return this == WhiteKing || this == BlackKing;
    }

    public boolean isWhite()
    {
        return this == WhitePawn ||
                this == WhiteRook ||
                this == WhiteKnight ||
                this == WhiteQueen ||
                this == WhiteKing ||
                this == WhiteBishop;
    }
}
