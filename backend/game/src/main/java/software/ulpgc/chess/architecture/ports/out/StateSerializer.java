package software.ulpgc.chess.architecture.ports.out;

import software.ulpgc.chess.architecture.model.board.State;

public interface StateSerializer {
    String serialize(State state);
}
