package software.ulpgc.chess.architecture.model;

import java.util.Arrays;

public enum File {
    A('a'),B('b'),C('c'), D('d'),E('e'),F('f'),G('g'),H('h');
    private  final char symbol;
    File(char symbol) {
        this.symbol = symbol;
    }
    public char symbol() {
        return this.symbol;
    }
    public static File from(char symbol) {
        return Arrays.stream(File.values())
                .filter(f -> f.symbol == symbol)
                .findFirst()
                .orElse(null);
    }

    public static File getFile(char symbol) {
        return from(symbol);
    }
}
