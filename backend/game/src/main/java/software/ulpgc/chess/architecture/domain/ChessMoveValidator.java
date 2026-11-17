package software.ulpgc.chess.architecture.domain;

import software.ulpgc.chess.architecture.model.board.Board;
import software.ulpgc.chess.architecture.model.board.Move;
import software.ulpgc.chess.architecture.model.board.Piece;
import software.ulpgc.chess.architecture.model.board.Square;

import java.util.List;

public class ChessMoveValidator implements MoveValidator {
    private final MoveValidator pseudoLegalValidator;
    private final CheckDetector checkDetector;

    public ChessMoveValidator() {
        this(new PseudoLegalMoveValidator());
    }

    public ChessMoveValidator(MoveValidator pseudoLegalValidator) {
        this(pseudoLegalValidator, new CheckDetector(pseudoLegalValidator));
    }

    public ChessMoveValidator(MoveValidator pseudoLegalValidator, CheckDetector checkDetector) {
        this.pseudoLegalValidator = pseudoLegalValidator;
        this.checkDetector = checkDetector;
    }

    @Override
    public List<Square> validMoves(Board board, Square from) {
        if (from == null || board.pieceAt(from) == null) return List.of();
        Piece piece = board.pieceAt(from);
        return pseudoLegalValidator.validMoves(board, from).stream()
                .filter(to -> !checkDetector.isKingInCheck(board.move(from, to), piece.color()))
                .toList();
    }

    @Override
    public boolean isValid(Board board, Move move) {
        return validMoves(board, move.from()).contains(move.to());
    }
}
