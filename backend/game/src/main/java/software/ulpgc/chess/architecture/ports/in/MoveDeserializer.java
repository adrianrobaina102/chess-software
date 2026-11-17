package software.ulpgc.chess.architecture.ports.in;

import software.ulpgc.chess.architecture.model.board.Move;

public interface MoveDeserializer {
    Move deserialize(String serializedMove);
}
