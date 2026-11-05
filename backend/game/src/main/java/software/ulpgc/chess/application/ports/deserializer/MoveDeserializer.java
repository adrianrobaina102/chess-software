package software.ulpgc.chess.application.ports.deserializer;

import software.ulpgc.chess.application.domain.model.Move;

public interface MoveDeserializer {
    Move deserialize(String serializedMove);
}
