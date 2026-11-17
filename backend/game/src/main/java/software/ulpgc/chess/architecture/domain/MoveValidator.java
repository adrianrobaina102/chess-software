package software.ulpgc.chess.architecture.domain;

import software.ulpgc.chess.architecture.model.board.Board;
import software.ulpgc.chess.architecture.model.board.Move;
import software.ulpgc.chess.architecture.model.board.Square;

import java.util.List;

public interface MoveValidator {
    List<Square> validMoves(Board board, Square from);
    boolean isValid(Board board, Move move);
}
