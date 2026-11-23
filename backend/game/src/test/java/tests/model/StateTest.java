package tests.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.model.CastlingRight;
import software.ulpgc.chess.architecture.model.board.Color;
import software.ulpgc.chess.architecture.model.board.Move;
import software.ulpgc.chess.architecture.model.board.Square;
import software.ulpgc.chess.architecture.model.board.State;

import static org.assertj.core.api.Assertions.assertThat;

public class StateTest {

    @Test
    @DisplayName("Mover el rey blanco desde e1 pierde ambos enroques blancos y cambia el turno")
    void shouldLoseBothWhiteCastlingRightsWhenWhiteKingMoves() {
        State state = State.init();

        State next = state.nextTurn(new Move(Square.at("e1"), Square.at("e2")));

        assertThat(next.side()).isEqualTo(Color.Black);
        assertThat(next.castlingRights()).containsExactlyInAnyOrder(
                CastlingRight.BlackKingSide,
                CastlingRight.BlackQueenSide
        );
    }

    @Test
    @DisplayName("Mover la torre blanca de h1 pierde solo el enroque corto blanco")
    void shouldLoseWhiteKingSideWhenH1RookMoves() {
        State state = State.init();

        State next = state.nextTurn(new Move(Square.at("h1"), Square.at("g1")));

        assertThat(next.castlingRights()).doesNotContain(CastlingRight.WhiteKingSide);
        assertThat(next.castlingRights()).contains(
                CastlingRight.WhiteQueenSide,
                CastlingRight.BlackKingSide,
                CastlingRight.BlackQueenSide
        );
    }

    @Test
    @DisplayName("Capturar la torre negra en a8 pierde el enroque largo negro")
    void shouldLoseBlackQueenSideWhenA8RookIsCaptured() {
        State state = State.init();

        State next = state.nextTurn(new Move(Square.at("b7"), Square.at("a8")));

        assertThat(next.castlingRights()).doesNotContain(CastlingRight.BlackQueenSide);
        assertThat(next.castlingRights()).contains(
                CastlingRight.WhiteKingSide,
                CastlingRight.WhiteQueenSide,
                CastlingRight.BlackKingSide
        );
    }

    @Test
    @DisplayName("Un movimiento normal conserva todos los derechos de enroque")
    void shouldKeepAllCastlingRightsOnNormalMove() {
        State state = State.init();

        State next = state.nextTurn(new Move(Square.at("e2"), Square.at("e4")));

        assertThat(next.side()).isEqualTo(Color.Black);
        assertThat(next.castlingRights()).containsExactlyInAnyOrder(CastlingRight.values());
    }
}
