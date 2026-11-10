package software.ulpgc.chess.infrastructure.adapters.in;

import software.ulpgc.chess.architecture.domain.model.State;
import software.ulpgc.chess.architecture.ports.in.StateDeserializer;

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
