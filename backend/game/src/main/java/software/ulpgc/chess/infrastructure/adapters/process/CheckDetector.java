package software.ulpgc.chess.infrastructure.adapters.process;

import software.ulpgc.chess.architecture.model.Board;
import software.ulpgc.chess.architecture.model.Color;
import software.ulpgc.chess.architecture.model.Square;
import software.ulpgc.chess.architecture.ports.process.MoveValidator;

import java.util.Map;
import java.util.Optional;

public class CheckDetector {
    private final MoveValidator pseudoLegalValidator;

    public CheckDetector(MoveValidator pseudoLegalValidator) {
        this.pseudoLegalValidator = pseudoLegalValidator;
    }

    public boolean isKingInCheck(Board board, Color color) {
        return findKingSquare(board, color)
                .map(kingSquare -> isSquareAttacked(board, kingSquare, color))
                .orElse(false);
    }

    private Optional<Square> findKingSquare(Board board, Color color) {
        return board.pieces().entrySet().stream()
                .filter(entry -> entry.getValue().color() == color && entry.getValue().isKing())
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private boolean isSquareAttacked(Board board, Square target, Color defenderColor) {
        return board.pieces().entrySet().stream()
                .filter(entry -> entry.getValue().color() != defenderColor)
                .anyMatch(entry -> pseudoLegalValidator.validMoves(board, entry.getKey()).contains(target));
    }
}
