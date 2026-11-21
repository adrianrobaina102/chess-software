package tests.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.board.Color;
import software.ulpgc.chess.architecture.model.board.Piece;
import software.ulpgc.chess.infrastructure.adapters.in.FenBoardDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.FenGamePositionDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.FenStateDeserializer;

import static org.assertj.core.api.Assertions.assertThat;
import static software.ulpgc.chess.architecture.model.CastlingRight.*;

public class FenGamePositionDeserializerTest {

    private FenGamePositionDeserializer deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new FenGamePositionDeserializer(new FenBoardDeserializer(), new FenStateDeserializer());
    }

    @Test
    @DisplayName("Debe deserializar tablero, turno y derechos de enroque en un GamePosition")
    void shouldDeserializeFullFenIntoGamePosition() {
        String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b Kq";

        GamePosition position = deserializer.deserialize(fen);

        assertThat(position.board().pieceAt("e4")).isEqualTo(Piece.WhitePawn);
        assertThat(position.board().pieceAt("e2")).isNull();
        assertThat(position.state().side()).isEqualTo(Color.Black);
        assertThat(position.state().castlingRights()).containsExactlyInAnyOrder(WhiteKingSide, BlackQueenSide);
    }
}
