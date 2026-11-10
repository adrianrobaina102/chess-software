package software.ulpgc.chess.architecture.ports.out;

import software.ulpgc.chess.architecture.model.Square;

public interface PositionSerializer {
    String serialize(Square square);
}
