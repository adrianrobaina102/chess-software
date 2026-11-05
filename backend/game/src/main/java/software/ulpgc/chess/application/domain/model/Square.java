package software.ulpgc.chess.application.domain.model;

public record Square (File file, Rank rank) {
    public static Square at (String square){
        validateFormat(square);

        File file = parseFile(square);
        Rank rank = parseRank(square);

        validateBounds(file, rank, square);

        return new Square(file, rank);
    }

    private static void validateFormat(String square) {
        if (square == null || square.length() != 2) {
            throw new ParseException("Formato de coordenada inválido: " + square);
        }
    }

    private static File parseFile(String square) {
        return File.getFile(square.charAt(0));
    }

    private static Rank parseRank(String square) {
        return Rank.getRank(Character.getNumericValue(square.charAt(1)));
    }

    private static void validateBounds(File file, Rank rank, String square) {
        if (file == null || rank == null) {
            throw new ParseException("Coordenada fuera del tablero: " + square);
        }
    }

    public static Square of(int i, int j) {
        return new Square(File.values()[i-1], Rank.values()[j-1]);
    }

    private  static class ParseException extends RuntimeException {
        public ParseException (String message){
            super(message);
        }
    }
    @Override
    public String toString() {return (file.toString()  + rank.symbol()).toLowerCase(); }
}
