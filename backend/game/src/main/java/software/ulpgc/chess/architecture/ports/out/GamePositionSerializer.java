package software.ulpgc.chess.architecture.ports.out;

import software.ulpgc.chess.architecture.model.GamePosition;

public interface GamePositionSerializer {
    String  serialize(GamePosition gamePosition);
}
