package software.ulpgc.chess.architecture.ports.in;

import software.ulpgc.chess.architecture.model.Board;

public interface BoardDeserializer {
    Board deserialize(String value);
}
