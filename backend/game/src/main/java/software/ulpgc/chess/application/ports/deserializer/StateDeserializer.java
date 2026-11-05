package software.ulpgc.chess.application.ports.deserializer;

import software.ulpgc.chess.application.domain.model.State;

public interface StateDeserializer {
    State deserialize(String value);
}
