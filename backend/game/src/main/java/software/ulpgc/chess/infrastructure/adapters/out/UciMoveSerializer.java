package software.ulpgc.chess.infrastructure.adapters.out;

import software.ulpgc.chess.architecture.domain.model.Move;
import software.ulpgc.chess.architecture.ports.out.MoveSerializer;

public class UciMoveSerializer implements MoveSerializer {
    @Override
    public String serialize(Move move) {
        AlgebraicPositionSerializer positionSerializer = new AlgebraicPositionSerializer();
        return positionSerializer.serialize(move.from()) + positionSerializer.serialize(move.to());
    }
}
