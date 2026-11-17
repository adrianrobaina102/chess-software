package software.ulpgc.chess.architecture.ports.out;

import software.ulpgc.chess.architecture.model.board.Board;

public interface BoardSerializer {
    String serialize(Board board);
}
