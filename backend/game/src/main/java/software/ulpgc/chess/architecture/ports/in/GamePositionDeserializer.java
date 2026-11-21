package software.ulpgc.chess.architecture.ports.in;

import software.ulpgc.chess.architecture.model.GamePosition;

public interface GamePositionDeserializer {
    GamePosition deserialize(String serializedGamePosition);
}
