package software.ulpgc.chess.infrastructure.adapters.fen;

import software.ulpgc.chess.application.domain.model.State;
import software.ulpgc.chess.application.ports.deserializer.StateDeserializer;

import java.util.Arrays;

public class FenStateDeserializer implements StateDeserializer {

    @Override
    public State deserialize(String value) {
        return deserialize(getFenPart(value));
    }

    private State deserialize(String[] values) {
        return new State(Arrays.copyOfRange(values, 1, values.length));
    }

    private String[] getFenPart(String fen) {
        return fen.split(" ");
    }
}
