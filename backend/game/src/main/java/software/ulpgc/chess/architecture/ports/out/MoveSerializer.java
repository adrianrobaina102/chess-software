package software.ulpgc.chess.architecture.ports.out;

import software.ulpgc.chess.architecture.model.board.Move;

public interface MoveSerializer {
    String serialize(Move move);
}
