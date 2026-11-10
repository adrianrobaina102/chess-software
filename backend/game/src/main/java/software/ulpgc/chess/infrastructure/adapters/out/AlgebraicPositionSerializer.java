package software.ulpgc.chess.infrastructure.adapters.out;

import software.ulpgc.chess.architecture.model.Square;
import software.ulpgc.chess.architecture.ports.out.PositionSerializer;

public class AlgebraicPositionSerializer implements PositionSerializer {

    @Override
    public String serialize(Square square) {
        return square.file().name().toLowerCase() + square.rank().symbol();
    }
}
