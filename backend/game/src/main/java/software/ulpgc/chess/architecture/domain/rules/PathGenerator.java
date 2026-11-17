package software.ulpgc.chess.architecture.domain.rules;

import software.ulpgc.chess.architecture.model.board.Square;

import java.util.List;

public interface PathGenerator {
    List<Square> generatePath(Square from, int[] direction);
}
