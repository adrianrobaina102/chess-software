package software.ulpgc.chess.architecture.rules;

import software.ulpgc.chess.architecture.model.Square;

import java.util.List;

public interface PieceRule {
    List<List<Square>> possibleMoves(Square from);
}
