package tests.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.model.board.Board;
import software.ulpgc.chess.architecture.model.board.Piece;
import software.ulpgc.chess.architecture.model.board.Square;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardTest {

    @Test
    @DisplayName("Un peón blanco debe coronar a reina blanca al llegar a la fila 8")
    void shouldPromoteWhitePawnToQueenOnRank8() {
        Board board = new Board(Map.of(Square.at("e7"), Piece.WhitePawn));

        Board moved = board.move(Square.at("e7"), Square.at("e8"));

        assertThat(moved.pieceAt("e8")).isEqualTo(Piece.WhiteQueen);
        assertThat(moved.pieceAt("e7")).isNull();
    }

    @Test
    @DisplayName("Un peón negro debe coronar a reina negra al llegar a la fila 1")
    void shouldPromoteBlackPawnToQueenOnRank1() {
        Board board = new Board(Map.of(Square.at("d2"), Piece.BlackPawn));

        Board moved = board.move(Square.at("d2"), Square.at("d1"));

        assertThat(moved.pieceAt("d1")).isEqualTo(Piece.BlackQueen);
        assertThat(moved.pieceAt("d2")).isNull();
    }

    @Test
    @DisplayName("Un peón que no llega a la última fila conserva su tipo")
    void shouldKeepPawnBeforeLastRank() {
        Board board = new Board(Map.of(Square.at("e6"), Piece.WhitePawn));

        Board moved = board.move(Square.at("e6"), Square.at("e7"));

        assertThat(moved.pieceAt("e7")).isEqualTo(Piece.WhitePawn);
    }
}
