package software.ulpgc.chess.architecture.ports.out;

import software.ulpgc.chess.architecture.domain.model.Board;

public interface BoardSerializer {
    String serialize(Board board);
}
