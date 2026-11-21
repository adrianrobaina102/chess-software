package software.ulpgc.chess.architecture.domain;

import software.ulpgc.chess.architecture.model.board.Board;
import software.ulpgc.chess.architecture.model.board.Color;
import software.ulpgc.chess.architecture.model.board.Square;

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

    public Optional<Square> findKingSquare(Board board, Color color) {
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
