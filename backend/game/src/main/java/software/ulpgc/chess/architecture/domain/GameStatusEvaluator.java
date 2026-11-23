package software.ulpgc.chess.architecture.domain;

import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.GameStatus;
import software.ulpgc.chess.architecture.model.board.Board;
import software.ulpgc.chess.architecture.model.board.Color;


public class GameStatusEvaluator {
    private final MoveValidator moveValidator;
    private final CheckDetector checkDetector;

    public GameStatusEvaluator(MoveValidator moveValidator, CheckDetector checkDetector) {
        this.moveValidator = moveValidator;
        this.checkDetector = checkDetector;
    }
    public GameStatus evaluate(GamePosition position)
    {
        if (!hasAnyLegalMove(position))
            return isInCheck(position) ? GameStatus.Checkmate :  GameStatus.Stalemate;
        return isInCheck(position) ? GameStatus.Check : GameStatus.Playing;
    }
    public boolean isInCheck(GamePosition position)
    {
        return checkDetector.isKingInCheck(position.board(), position.state().side());
    }
    public boolean hasAnyLegalMove(GamePosition position)
    {
        return hasAnyLegalMove(position.board(), position.state().side());
    }
    private boolean hasAnyLegalMove(Board board, Color side)
    {
        return board.pieces().entrySet().stream()
                .filter(entry -> entry.getValue().color().equals(side))
                .anyMatch(entry -> ! moveValidator.validMoves(board, entry.getKey()).isEmpty() );

    }
}
