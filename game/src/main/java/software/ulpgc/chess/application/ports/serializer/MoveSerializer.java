package software.ulpgc.chess.application.ports.serializer;

import software.ulpgc.chess.application.domain.model.Move;

public interface MoveSerializer {
    String serialize(Move move);
}
