package software.ulpgc.chess.infrastructure.adapters.fen;

import software.ulpgc.chess.application.domain.model.Board;
import software.ulpgc.chess.application.domain.model.Piece;
import software.ulpgc.chess.application.domain.model.Square;
import software.ulpgc.chess.application.ports.serializer.BoardSerializer;

public class FenBoardSerializer implements BoardSerializer {

    @Override
    public String serialize(Board board) {
        StringBuilder fen = new StringBuilder();
        buildFen(board, fen);
        return fen.toString().substring(0, fen.length() - 1);
    }

    private void buildFen(Board board, StringBuilder fen) {
        for (int rank = 8; rank >= 1; rank--) {
            fen.append(buildFen(rank, board)).append('/');
        }
    }

    private String buildFen(int rank, Board board) {
        int emptyCount = 0;
        StringBuilder fen = new StringBuilder();
        for (int file = 1; file <= 8; file++) {
            String element = getSymbolAt(board, file, rank);
            if (element.equals("Vacio")) {
                emptyCount++;
            } else {
                if (emptyCount > 0) {
                    fen.append(emptyCount);
                    emptyCount = 0;
                }
                fen.append(element);
            }
        }
        if (emptyCount > 0)
            fen.append(emptyCount);
        return fen.toString();
    }

    public String getSymbolAt(Board board, int i, int j) {
        return getSymbolAt(board, Square.of(i, j));
    }

    private String getSymbolAt(Board board, Square square) {
        return symbolOf(board.pieces().get(square));
    }

    private String symbolOf(Piece piece) {
        return piece == null ? "Vacio" : String.valueOf(piece.getSymbol());
    }
}
