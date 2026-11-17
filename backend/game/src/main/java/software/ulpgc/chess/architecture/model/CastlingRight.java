package software.ulpgc.chess.architecture.model;


import java.util.Arrays;

public enum CastlingRight {
    WhiteKingSide('K'),
    WhiteQueenSide('Q'),
    BlackKingSide('k'),
    BlackQueenSide('q');
    private final char symbol;

    CastlingRight(char symbol) {
        this.symbol = symbol;
    }
    public char symbol() {
        return this.symbol;
    }
    public static CastlingRight from(char symbol) {
        return Arrays.stream(values())
                .filter(right -> right.symbol() == symbol)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid castling symbol: " + symbol));

    }
}
