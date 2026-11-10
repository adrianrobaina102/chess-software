package software.ulpgc.chess.architecture.ports.in;

import software.ulpgc.chess.architecture.model.State;

public interface StateDeserializer {
    State deserialize(String value);
}
