package tests.out;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.board.Move;
import software.ulpgc.chess.infrastructure.adapters.out.FenBoardSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenGamePositionSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenStateSerializer;

import static org.assertj.core.api.Assertions.assertThat;
import static software.ulpgc.chess.architecture.model.board.Square.at;

public class FenGamePositionSerializerTest {

    private FenGamePositionSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new FenGamePositionSerializer(new FenBoardSerializer(), new FenStateSerializer());
    }

    @Test
    @DisplayName("Debe serializar la posicion inicial completa con tablero, turno y enroques")
    void shouldSerializeInitialGamePosition() {
        String fen = serializer.serialize(GamePosition.initial());

        assertThat(fen).isEqualTo("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq");
    }

    @Test
    @DisplayName("Debe alternar el turno a negras tras un movimiento")
    void shouldSerializePositionAfterMoveWithBlackTurn() {
        GamePosition afterE4 = GamePosition.initial().move(new Move(at("e2"), at("e4")));

        assertThat(serializer.serialize(afterE4))
                .isEqualTo("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq");
    }
}
