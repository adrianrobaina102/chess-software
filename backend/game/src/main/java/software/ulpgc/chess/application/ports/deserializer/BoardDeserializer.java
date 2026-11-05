package software.ulpgc.chess.application.ports.deserializer;

import software.ulpgc.chess.application.domain.model.Board;

public interface BoardDeserializer {
    Board deserialize(String value);
}
