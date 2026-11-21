package tests.process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.domain.*;
import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.GameStatus;
import software.ulpgc.chess.architecture.model.board.Board;
import software.ulpgc.chess.architecture.model.board.Color;
import software.ulpgc.chess.architecture.model.board.State;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static software.ulpgc.chess.architecture.model.board.Piece.*;
import static software.ulpgc.chess.architecture.model.board.Square.at;

public class GameStatusEvaluatorTest {

    private GameStatusEvaluator evaluator;

    @BeforeEach
    void setUp() {
        MoveValidator pseudoLegal = new PseudoLegalMoveValidator();
        CheckDetector checkDetector = new CheckDetector(pseudoLegal);
        MoveValidator validator = new ChessMoveValidator(pseudoLegal, checkDetector);
        evaluator = new GameStatusEvaluator(validator, checkDetector);
    }

    @Test
    @DisplayName("Debe devolver Playing en la posicion inicial")
    void shouldReturnPlayingForInitialPosition() {
        assertThat(evaluator.evaluate(GamePosition.initial())).isEqualTo(GameStatus.Playing);
    }

    @Test
    @DisplayName("Debe devolver Check cuando el rey esta en jaque pero puede escapar o bloquear")
    void shouldReturnCheckWhenKingCanEscape() {
        Board board = new Board(Map.of(
                at("e1"), WhiteKing,
                at("e8"), BlackRook
        ));
        GamePosition position = new GamePosition(board, new State(Color.White, Set.of()));

        assertThat(evaluator.evaluate(position)).isEqualTo(GameStatus.Check);
    }

    @Test
    @DisplayName("Debe devolver Checkmate cuando el rey esta en jaque y no tiene jugadas legales")
    void shouldReturnCheckmateWhenNoLegalMovesInCheck() {
        Board board = new Board(Map.of(
                at("h8"), BlackKing,
                at("g7"), WhiteQueen,
                at("f6"), WhiteKing
        ));
        GamePosition position = new GamePosition(board, new State(Color.Black, Set.of()));

        assertThat(evaluator.evaluate(position)).isEqualTo(GameStatus.Checkmate);
    }

    @Test
    @DisplayName("Debe devolver Stalemate cuando no hay jaque ni jugadas legales")
    void shouldReturnStalemateWhenNoCheckAndNoLegalMoves() {
        Board board = new Board(Map.of(
                at("a8"), BlackKing,
                at("c7"), WhiteQueen,
                at("b6"), WhiteKing
        ));
        GamePosition position = new GamePosition(board, new State(Color.Black, Set.of()));

        assertThat(evaluator.evaluate(position)).isEqualTo(GameStatus.Stalemate);
    }
}
