package software.ulpgc.chess.architecture.ports.process;

import software.ulpgc.chess.architecture.model.Board;
import software.ulpgc.chess.architecture.model.Move;
import software.ulpgc.chess.architecture.model.Square;

import java.util.List;

public interface MoveValidator {
    List<Square> validMoves(Board board, Square from);
    boolean isValid(Board board, Move move);
}
