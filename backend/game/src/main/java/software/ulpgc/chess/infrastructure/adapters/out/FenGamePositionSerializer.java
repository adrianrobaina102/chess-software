package software.ulpgc.chess.infrastructure.adapters.out;

import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.ports.out.BoardSerializer;
import software.ulpgc.chess.architecture.ports.out.GamePositionSerializer;
import software.ulpgc.chess.architecture.ports.out.StateSerializer;

public class FenGamePositionSerializer implements GamePositionSerializer {
    private final BoardSerializer boardSerializer;
    private final StateSerializer stateSerializer;

    public FenGamePositionSerializer(BoardSerializer boardSerializer, StateSerializer stateSerializer) {
        this.boardSerializer = boardSerializer;
        this.stateSerializer = stateSerializer;
    }

    @Override
    public String serialize(GamePosition gamePosition) {
        return boardSerializer.serialize(gamePosition.board())
                + " "
                + stateSerializer.serialize(gamePosition.state());
    }
}
