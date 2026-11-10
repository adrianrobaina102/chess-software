package software.ulpgc.chess.infrastructure.adapters.in;

import software.ulpgc.chess.architecture.model.Move;
import software.ulpgc.chess.architecture.ports.in.MoveDeserializer;

public class UciMoveDeserializer implements MoveDeserializer {
    @Override
    public Move deserialize(String serializedMove) {
        return deserialize(from(serializedMove), to(serializedMove));
    }

    private Move deserialize(String from, String to) {
        AlgebraicPositionDeserializer positionDeserializer = new AlgebraicPositionDeserializer();
        return new Move(positionDeserializer.deserialize(from), positionDeserializer.deserialize(to));
    }

    private String from(String serializedMove) {
        return serializedMove.substring(0, 2);
    }

    private String to(String serializedMove) {
        return serializedMove.substring(2, 4);
    }
}
