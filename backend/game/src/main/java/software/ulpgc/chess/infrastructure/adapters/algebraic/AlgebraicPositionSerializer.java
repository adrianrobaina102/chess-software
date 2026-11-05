package software.ulpgc.chess.infrastructure.adapters.algebraic;

import software.ulpgc.chess.application.domain.model.Square;
import software.ulpgc.chess.application.ports.serializer.PositionSerializer;

public class AlgebraicPositionSerializer implements PositionSerializer {

    @Override
    public String serialize(Square square) {
        return square.file().name().toLowerCase() + square.rank().symbol();
    }
}
