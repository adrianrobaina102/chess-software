package tests.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.model.CastlingRight;
import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.board.Board;
import software.ulpgc.chess.architecture.model.board.Move;
import software.ulpgc.chess.architecture.model.board.Piece;
import software.ulpgc.chess.architecture.model.board.Square;
import software.ulpgc.chess.architecture.model.board.State;

import static org.assertj.core.api.Assertions.assertThat;

public class GamePositionTest {

    @Test
    @DisplayName("Enroque corto blanco mueve rey e1->g1, torre h1->f1 y quita derechos blancos")
    void shouldMoveRookOnWhiteKingsideCastling() {
        Board board = Board.builder()
                .put("e1", Piece.WhiteKing)
                .put("h1", Piece.WhiteRook)
                .put("e8", Piece.BlackKing)
                .build();
        GamePosition position = new GamePosition(board, State.init());

        GamePosition next = position.move(new Move(Square.at("e1"), Square.at("g1")));

        assertThat(next.board().pieceAt("g1")).isEqualTo(Piece.WhiteKing);
        assertThat(next.board().pieceAt("f1")).isEqualTo(Piece.WhiteRook);
        assertThat(next.board().pieceAt("e1")).isNull();
        assertThat(next.board().pieceAt("h1")).isNull();
        assertThat(next.state().castlingRights()).containsExactlyInAnyOrder(
                CastlingRight.BlackKingSide,
                CastlingRight.BlackQueenSide
        );
    }

    @Test
    @DisplayName("Enroque largo negro mueve rey e8->c8 y torre a8->d8")
    void shouldMoveRookOnBlackQueensideCastling() {
        Board board = Board.builder()
                .put("e1", Piece.WhiteKing)
                .put("e8", Piece.BlackKing)
                .put("a8", Piece.BlackRook)
                .build();
        GamePosition position = new GamePosition(board, State.init());

        GamePosition next = position.move(new Move(Square.at("e8"), Square.at("c8")));

        assertThat(next.board().pieceAt("c8")).isEqualTo(Piece.BlackKing);
        assertThat(next.board().pieceAt("d8")).isEqualTo(Piece.BlackRook);
        assertThat(next.board().pieceAt("a8")).isNull();
    }

    @Test
    @DisplayName("Mover el rey blanco un paso normal (e1->e2) no mueve la torre ni falla")
    void shouldNotMoveRookOnNormalKingMove() {
        Board board = Board.builder()
                .put("e1", Piece.WhiteKing)
                .put("h1", Piece.WhiteRook)
                .build();
        GamePosition position = new GamePosition(board, State.init());

        GamePosition next = position.move(new Move(Square.at("e1"), Square.at("e2")));

        assertThat(next.board().pieceAt("e2")).isEqualTo(Piece.WhiteKing);
        assertThat(next.board().pieceAt("h1")).isEqualTo(Piece.WhiteRook);
    }
}
