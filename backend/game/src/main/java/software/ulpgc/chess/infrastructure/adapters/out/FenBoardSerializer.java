package software.ulpgc.chess.infrastructure.adapters.out;

import software.ulpgc.chess.architecture.model.Board;
import software.ulpgc.chess.architecture.model.Piece;
import software.ulpgc.chess.architecture.model.Square;
import software.ulpgc.chess.architecture.ports.out.BoardSerializer;

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
            Piece piece = board.pieces().get(Square.of(file, rank));
            if (piece == null) {
                emptyCount++;
            } else {
                if (emptyCount > 0) {
                    fen.append(emptyCount);
                    emptyCount = 0;
                }
                fen.append(piece.getSymbol());
            }
        }
        if (emptyCount > 0)
            fen.append(emptyCount);
        return fen.toString();
    }

    public String getSymbolAt(Board board, int file, int rank) {
        return getSymbolAt(board, Square.of(file, rank));
    }

    private String getSymbolAt(Board board, Square square) {
        Piece piece = board.pieces().get(square);
        return piece == null ? "" : String.valueOf(piece.getSymbol());
    }
}
