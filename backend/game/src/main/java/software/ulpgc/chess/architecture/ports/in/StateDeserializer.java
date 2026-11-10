package software.ulpgc.chess.architecture.ports.in;

import software.ulpgc.chess.architecture.domain.model.State;

public interface StateDeserializer {
    State deserialize(String value);
}
