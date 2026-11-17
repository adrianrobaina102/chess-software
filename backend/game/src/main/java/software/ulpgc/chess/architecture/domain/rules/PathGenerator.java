package software.ulpgc.chess.architecture.domain.rules;

import software.ulpgc.chess.architecture.model.Square;

import java.util.List;

public interface PathGenerator {
    List<Square> generatePath(Square from, int[] direction);
}
