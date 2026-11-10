package software.ulpgc.chess.architecture.ports.in;

import software.ulpgc.chess.architecture.domain.model.Square;

public interface PositionDeserializer {
    Square deserialize(String notation);
}
