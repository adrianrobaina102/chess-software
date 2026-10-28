package software.ulpgc.chess.infrastructure.adapters.fen;

import software.ulpgc.chess.application.domain.model.Board;
import software.ulpgc.chess.application.domain.model.Piece;
import software.ulpgc.chess.application.domain.model.Square;
import software.ulpgc.chess.application.ports.deserializer.BoardDeserializer;

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
        for (char order : rank) {
            if (Character.isLetter(order)) {
                map.put(Square.of(i++, j), Piece.getPiece(order));
            } else {
                i += Character.getNumericValue(order);
            }
        }
        return map;
    }

    private String getFenPart(String fen) {
        return fen.split(" ")[0];
    }
}
