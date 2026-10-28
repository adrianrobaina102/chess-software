package software.ulpgc.chess.application.ports.serializer;

import software.ulpgc.chess.application.domain.model.Square;

public interface PositionSerializer {
    String serialize(Square square);
}
