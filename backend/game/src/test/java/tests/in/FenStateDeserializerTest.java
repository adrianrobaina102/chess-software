package tests.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.model.board.Color;
import software.ulpgc.chess.architecture.model.board.State;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.FenStateDeserializer;

import static org.assertj.core.api.Assertions.assertThat;
import static software.ulpgc.chess.architecture.model.CastlingRight.*;

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
        assertThat(state.side()).isEqualTo(Color.White);
        assertThat(state.castlingRights()).containsExactlyInAnyOrder(
                WhiteKingSide, WhiteQueenSide, BlackKingSide, BlackQueenSide
        );
    }

    @Test
    @DisplayName("Debe deserializar el turno negro con todos los enroques disponibles")
    void shouldDeserializeBlackTurnWithEnPassant() {
        String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

        State state = deserializer.deserialize(fen);

        assertThat(state.side()).isEqualTo(Color.Black);
        assertThat(state.castlingRights()).containsExactlyInAnyOrder(
                WhiteKingSide, WhiteQueenSide, BlackKingSide, BlackQueenSide
        );
    }

    @Test
    @DisplayName("Debe deserializar un final sin enroques")
    void shouldDeserializeEndgameWithoutCastling() {
        String fen = "8/8/8/4k3/8/8/4K3/8 b - - 50 100";

        State state = deserializer.deserialize(fen);

        assertThat(state.side()).isEqualTo(Color.Black);
        assertThat(state.castlingRights()).isEmpty();
    }

    @Test
    @DisplayName("Debe deserializar derechos de enroque asimetricos")
    void shouldDeserializeAsymmetricalCastling() {
        String fen = "r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15";

        State state = deserializer.deserialize(fen);

        assertThat(state.side()).isEqualTo(Color.White);
        assertThat(state.castlingRights()).containsExactlyInAnyOrder(WhiteQueenSide, BlackKingSide);
    }

    @Test
    @DisplayName("Debe deserializar un estado con un unico derecho de enroque")
    void shouldDeserializeSingleCastlingRight() {
        String fen = "8/8/8/8/8/8/8/4K2R w K - 10 42";

        State state = deserializer.deserialize(fen);

        assertThat(state.side()).isEqualTo(Color.White);
        assertThat(state.castlingRights()).containsExactly(WhiteKingSide);
    }

    @Test
    @DisplayName("Debe deserializar la apertura siciliana con turno blanco y todos los enroques")
    void shouldDeserializeSicilianDefenseOpening() {
        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";

        State state = deserializer.deserialize(fen);

        assertThat(state.side()).isEqualTo(Color.White);
        assertThat(state.castlingRights()).containsExactlyInAnyOrder(
                WhiteKingSide, WhiteQueenSide, BlackKingSide, BlackQueenSide
        );
    }
}
