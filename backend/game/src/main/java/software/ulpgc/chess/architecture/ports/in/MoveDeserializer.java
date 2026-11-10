package software.ulpgc.chess.architecture.ports.in;

import software.ulpgc.chess.architecture.domain.model.Move;

public interface MoveDeserializer {
    Move deserialize(String serializedMove);
}
