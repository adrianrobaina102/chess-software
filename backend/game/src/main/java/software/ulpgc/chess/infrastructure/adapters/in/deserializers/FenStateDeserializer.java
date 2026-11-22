package software.ulpgc.chess.infrastructure.adapters.in.deserializers;

import software.ulpgc.chess.architecture.model.CastlingRight;
import software.ulpgc.chess.architecture.model.board.Color;
import software.ulpgc.chess.architecture.model.board.State;
import software.ulpgc.chess.architecture.ports.in.StateDeserializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class FenStateDeserializer implements StateDeserializer {

    @Override
    public State deserialize(String value) {
        return deserialize(getFenPart(value));
    }

    private State deserialize(String[] values) {
        Color side = values[1].equals("w") ? Color.White : Color.Black;
        Set<CastlingRight> castlingRights = parseCastlingRights(values[2]);
        return new State(side, castlingRights);
    }

    private Set<CastlingRight> parseCastlingRights(String value) {
        if (value.equals("-")) return Set.of();
        Set<CastlingRight> castlingRights = new HashSet<>();
        for (char c : value.toCharArray()) castlingRights.add(CastlingRight.from(c));
        return castlingRights;
    }

    private String[] getFenPart(String fen) {
        return fen.split(" ");
    }
}
