package software.ulpgc.chess.application.domain.model;

import java.util.Arrays;

public final class State {
    private final String[] state;

    @Override
    public String toString() {
        return "State{" +
                "state=" + Arrays.toString(state) +
                '}';
    }

    public State(String[] state) {
        this.state = state;
    }
}
