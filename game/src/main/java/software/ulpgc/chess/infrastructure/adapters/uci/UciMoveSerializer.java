package software.ulpgc.chess.infrastructure.adapters.uci;

import software.ulpgc.chess.application.domain.model.Move;
import software.ulpgc.chess.application.ports.serializer.MoveSerializer;
import software.ulpgc.chess.infrastructure.adapters.algebraic.AlgebraicPositionSerializer;

public class UciMoveSerializer implements MoveSerializer {
    @Override
    public String serialize(Move move) {
        AlgebraicPositionSerializer positionSerializer = new AlgebraicPositionSerializer();
        return positionSerializer.serialize(move.from()) + positionSerializer.serialize(move.to());
    }
}
