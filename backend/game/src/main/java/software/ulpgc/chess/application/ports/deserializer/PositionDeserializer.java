package software.ulpgc.chess.application.ports.deserializer;

import software.ulpgc.chess.application.domain.model.Square;

public interface PositionDeserializer {
    Square deserialize(String notation);
}
