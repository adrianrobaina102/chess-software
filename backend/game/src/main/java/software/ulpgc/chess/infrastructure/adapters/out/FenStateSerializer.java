package software.ulpgc.chess.infrastructure.adapters.out;

import software.ulpgc.chess.architecture.model.CastlingRight;
import software.ulpgc.chess.architecture.model.board.Color;
import software.ulpgc.chess.architecture.model.board.State;
import software.ulpgc.chess.architecture.ports.out.StateSerializer;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FenStateSerializer implements StateSerializer {

    @Override
    public String serialize(State state) {
        String side = (state.side() == Color.White) ? "w" : "b";
        String castling = serializeCastlingRights(state.castlingRights());
        return side + " " + castling;
    }

    private String serializeCastlingRights(Set<CastlingRight> castlingRights) {
        String result = Arrays.stream(CastlingRight.values())
                .filter(castlingRights::contains)
                .map(right -> String.valueOf(right.symbol()))
                .collect(Collectors.joining());
        return result.isEmpty() ? "-" : result;
    }
}
