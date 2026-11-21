package tests.out;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.model.board.Color;
import software.ulpgc.chess.architecture.model.board.State;
import software.ulpgc.chess.infrastructure.adapters.out.FenStateSerializer;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static software.ulpgc.chess.architecture.model.CastlingRight.*;

public class FenStateSerializerTest {

    private FenStateSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new FenStateSerializer();
    }

    @Test
    @DisplayName("Debe serializar el estado inicial como 'w KQkq'")
    void shouldSerializeInitialState() {
        assertThat(serializer.serialize(State.init())).isEqualTo("w KQkq");
    }

    @Test
    @DisplayName("Debe serializar en orden oficial KQkq y usar '-' cuando no hay enroques")
    void shouldSerializePartialAndEmptyCastlingInOfficialOrder() {
        State partial = new State(Color.Black, Set.of(BlackQueenSide, WhiteKingSide));
        State none = new State(Color.Black, Set.of());

        assertThat(serializer.serialize(partial)).isEqualTo("b Kq");
        assertThat(serializer.serialize(none)).isEqualTo("b -");
    }
}
