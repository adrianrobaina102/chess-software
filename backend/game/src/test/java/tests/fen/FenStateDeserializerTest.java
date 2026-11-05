package tests.fen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.application.domain.model.State;
import software.ulpgc.chess.infrastructure.adapters.fen.FenStateDeserializer;

import static org.assertj.core.api.Assertions.assertThat;

public class FenStateDeserializerTest {

    private FenStateDeserializer deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new FenStateDeserializer();
    }

    @Test
    @DisplayName("Debe deserializar correctamente el estado inicial de la partida")
    void shouldDeserializeInitialPosition() {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        State state = deserializer.deserialize(fen);

        assertThat(state).isNotNull();
        assertThat(state.toString()).isEqualTo("State{state=[w, KQkq, -, 0, 1]}");
    }

    @Test
    @DisplayName("Debe deserializar el turno negro con captura al paso disponible")
    void shouldDeserializeBlackTurnWithEnPassant() {
        String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

        State state = deserializer.deserialize(fen);

        assertThat(state.toString()).isEqualTo("State{state=[b, KQkq, e3, 0, 1]}");
    }

    @Test
    @DisplayName("Debe deserializar un final sin enroques ni capturas al paso")
    void shouldDeserializeEndgameWithoutCastling() {
        String fen = "8/8/8/4k3/8/8/4K3/8 b - - 50 100";

        State state = deserializer.deserialize(fen);

        assertThat(state.toString()).isEqualTo("State{state=[b, -, -, 50, 100]}");
    }

    @Test
    @DisplayName("Debe deserializar derechos de enroque asimétricos")
    void shouldDeserializeAsymmetricalCastling() {
        String fen = "r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15";

        State state = deserializer.deserialize(fen);

        assertThat(state.toString()).isEqualTo("State{state=[w, Qk, -, 0, 15]}");
    }

    @Test
    @DisplayName("Debe deserializar un estado con un único derecho de enroque")
    void shouldDeserializeSingleCastlingRight() {
        String fen = "8/8/8/8/8/8/8/4K2R w K - 10 42";

        State state = deserializer.deserialize(fen);

        assertThat(state.toString()).isEqualTo("State{state=[w, K, -, 10, 42]}");
    }

    @Test
    @DisplayName("Debe deserializar la apertura con reloj a cero y segundo movimiento")
    void shouldDeserializeSicilianDefenseOpening() {
        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";

        State state = deserializer.deserialize(fen);

        assertThat(state.toString()).isEqualTo("State{state=[w, KQkq, c6, 0, 2]}");
    }
}
