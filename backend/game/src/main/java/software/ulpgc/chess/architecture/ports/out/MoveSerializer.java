package software.ulpgc.chess.architecture.ports.out;

import software.ulpgc.chess.architecture.domain.model.Move;

public interface MoveSerializer {
    String serialize(Move move);
}
