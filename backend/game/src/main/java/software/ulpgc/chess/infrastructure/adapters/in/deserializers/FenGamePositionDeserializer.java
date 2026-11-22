package software.ulpgc.chess.infrastructure.adapters.in.deserializers;

import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.ports.in.BoardDeserializer;
import software.ulpgc.chess.architecture.ports.in.GamePositionDeserializer;
import software.ulpgc.chess.architecture.ports.in.StateDeserializer;

public class FenGamePositionDeserializer implements GamePositionDeserializer {
    private final BoardDeserializer boardDeserializer;
    private final StateDeserializer stateDeserializer;

    public FenGamePositionDeserializer(BoardDeserializer boardDeserializer, StateDeserializer stateDeserializer) {
        this.boardDeserializer = boardDeserializer;
        this.stateDeserializer = stateDeserializer;
    }

    @Override
    public GamePosition deserialize(String fen) {
        return new GamePosition(
                boardDeserializer.deserialize(fen),
                stateDeserializer.deserialize(fen)
        );
    }
}
