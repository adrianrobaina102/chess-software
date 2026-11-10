package software.ulpgc.chess.infrastructure.adapters.in;

import software.ulpgc.chess.architecture.model.Board;
import software.ulpgc.chess.architecture.model.Piece;
import software.ulpgc.chess.architecture.model.Square;
import software.ulpgc.chess.architecture.ports.in.BoardDeserializer;

import java.util.HashMap;
import java.util.Map;

public class FenBoardDeserializer implements BoardDeserializer {
    @Override
    public Board deserialize(String fen) {
        return deserialize(getFenPart(fen).split("/"));
    }

    private Board deserialize(String[] values) {
        return new Board(piecesIn(values));
    }

    private Map<Square, Piece> piecesIn(String[] values) {
        Map<Square, Piece> map = new HashMap<>();
        int j = 8;
        for (String value : values)
            map.putAll(piecesIn(j--, value.toCharArray()));
        return map;
    }

    private Map<Square, Piece> piecesIn(int j, char[] rank) {
        int i = 1;
        Map<Square, Piece> map = new HashMap<>();
        for (char symbol : rank) {
            if (Character.isLetter(symbol)) {
                map.put(Square.of(i++, j), Piece.getPiece(symbol));
            } else {
                i += Character.getNumericValue(symbol);
            }
        }
        return map;
    }

    private String getFenPart(String fen) {
        return fen.split(" ")[0];
    }
}
