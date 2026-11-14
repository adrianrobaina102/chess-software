package tests.process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.model.Board;
import software.ulpgc.chess.architecture.model.Move;
import software.ulpgc.chess.architecture.ports.process.MoveValidator;
import software.ulpgc.chess.infrastructure.adapters.process.ChessMoveValidator;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static software.ulpgc.chess.architecture.model.Piece.*;
import static software.ulpgc.chess.architecture.model.Square.at;

public class ChessMoveValidatorTest {

    private MoveValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ChessMoveValidator();
    }

    @Test
    @DisplayName("Debe devolver lista vacia cuando la casilla origen es null o esta vacia")
    void shouldReturnEmptyMovesWhenOriginIsNullOrEmpty() {
        Board board = Board.initial();

        assertThat(validator.validMoves(board, null)).isEmpty();
        assertThat(validator.validMoves(board, at("e4"))).isEmpty();
        assertThat(validator.isValid(board, new Move(at("e4"), at("e5")))).isFalse();
    }

    @Test
    @DisplayName("Debe permitir a la torre avanzar por rayos, parar antes de pieza propia y capturar rival")
    void shouldValidateRookMovesWithBlockingAndCapture() {
        Board board = new Board(Map.of(
                at("d4"), WhiteRook,
                at("d6"), WhitePawn,
                at("f4"), BlackPawn
        ));

        assertThat(validator.validMoves(board, at("d4")))
                .containsExactlyInAnyOrder(
                        at("d5"),
                        at("d3"), at("d2"), at("d1"),
                        at("e4"), at("f4"),
                        at("c4"), at("b4"), at("a4")
                );
        assertThat(validator.isValid(board, new Move(at("d4"), at("f4")))).isTrue();
        assertThat(validator.isValid(board, new Move(at("d4"), at("g4")))).isFalse();
        assertThat(validator.isValid(board, new Move(at("d4"), at("d6")))).isFalse();
        assertThat(validator.isValid(board, new Move(at("d4"), at("e5")))).isFalse();
    }

    @Test
    @DisplayName("Debe permitir al alfil mover en diagonal respetando bloqueos y capturas")
    void shouldValidateBishopMovesWithBlockingAndCapture() {
        Board board = new Board(Map.of(
                at("c1"), WhiteBishop,
                at("e3"), BlackPawn,
                at("b2"), WhitePawn
        ));

        assertThat(validator.validMoves(board, at("c1")))
                .containsExactlyInAnyOrder(at("d2"), at("e3"));
    }

    @Test
    @DisplayName("Debe permitir al caballo los 8 saltos en L ignorando bloqueos intermedios")
    void shouldValidateAllEightKnightJumpsAndIgnoreInterveningPieces() {
        Board board = new Board(Map.of(
                at("d4"), WhiteKnight,
                at("d5"), WhitePawn,
                at("e4"), BlackPawn,
                at("e6"), WhitePawn,
                at("c6"), BlackQueen
        ));

        assertThat(validator.validMoves(board, at("d4")))
                .containsExactlyInAnyOrder(
                        at("c6"),
                        at("f5"), at("f3"),
                        at("e2"), at("c2"),
                        at("b5"), at("b3")
                );
    }

    @Test
    @DisplayName("Debe permitir a la reina mover en las 8 direcciones respetando bloqueos")
    void shouldValidateQueenMovesInAllDirections() {
        Board board = new Board(Map.of(
                at("d1"), WhiteQueen,
                at("d2"), WhitePawn,
                at("c1"), WhitePawn,
                at("e1"), WhiteKing,
                at("c2"), WhitePawn,
                at("e2"), BlackPawn
        ));

        assertThat(validator.validMoves(board, at("d1")))
                .containsExactlyInAnyOrder(at("e2"));
    }

    @Test
    @DisplayName("Debe permitir al rey moverse una casilla salvo casillas con piezas propias o amenazadas")
    void shouldValidateKingSingleStepMoves() {
        Board board = new Board(Map.of(
                at("e1"), WhiteKing,
                at("d1"), WhiteQueen,
                at("d2"), WhitePawn,
                at("e2"), BlackPawn
        ));

        assertThat(validator.validMoves(board, at("e1")))
                .containsExactlyInAnyOrder(at("e2"), at("f2"));
    }

    @Test
    @DisplayName("Debe permitir al peon blanco avanzar 1 o 2 casillas desde fila inicial y capturar en diagonal")
    void shouldValidateWhitePawnFromInitialRank() {
        Board board = new Board(Map.of(
                at("e2"), WhitePawn,
                at("d3"), BlackPawn,
                at("f3"), WhitePawn
        ));

        assertThat(validator.validMoves(board, at("e2")))
                .containsExactlyInAnyOrder(at("e3"), at("e4"), at("d3"));
    }

    @Test
    @DisplayName("No debe permitir al peon saltar una pieza en su avance inicial de dos casillas")
    void shouldBlockWhitePawnDoubleAdvanceWhenIntermediateSquareIsOccupied() {
        Board blockedFirst = new Board(Map.of(
                at("e2"), WhitePawn,
                at("e3"), BlackPawn
        ));
        Board blockedSecond = new Board(Map.of(
                at("e2"), WhitePawn,
                at("e4"), BlackPawn
        ));

        assertThat(validator.validMoves(blockedFirst, at("e2"))).isEmpty();
        assertThat(validator.validMoves(blockedSecond, at("e2")))
                .containsExactlyInAnyOrder(at("e3"));
    }

    @Test
    @DisplayName("No debe permitir al peon avanzar dos casillas fuera de su fila inicial")
    void shouldAllowOnlySingleStepForPawnOutsideInitialRank() {
        Board open = new Board(Map.of(
                at("e3"), WhitePawn
        ));
        Board blockedAhead = new Board(Map.of(
                at("e3"), WhitePawn,
                at("e4"), BlackPawn
        ));

        assertThat(validator.validMoves(open, at("e3")))
                .containsExactlyInAnyOrder(at("e4"));
        assertThat(validator.validMoves(blockedAhead, at("e3"))).isEmpty();
    }

    @Test
    @DisplayName("Debe validar correctamente el peon negro desde fila inicial y sus capturas diagonales")
    void shouldValidateBlackPawnMoves() {
        Board board = new Board(Map.of(
                at("e7"), BlackPawn,
                at("f6"), WhiteKnight,
                at("d6"), BlackBishop
        ));

        assertThat(validator.validMoves(board, at("e7")))
                .containsExactlyInAnyOrder(at("e6"), at("e5"), at("f6"));
    }

    @Test
    @DisplayName("No debe permitir al rey moverse a una casilla amenazada por una pieza rival")
    void shouldNotAllowKingToMoveIntoCheck() {
        Board board = new Board(Map.of(
                at("e1"), WhiteKing,
                at("f8"), BlackRook
        ));

        assertThat(validator.validMoves(board, at("e1")))
                .containsExactlyInAnyOrder(at("d1"), at("d2"), at("e2"));
        assertThat(validator.isValid(board, new Move(at("e1"), at("f1")))).isFalse();
    }

    @Test
    @DisplayName("No debe permitir mover una pieza clavada si expone a su propio rey al jaque")
    void shouldRestrictPinnedPieceToPinLine() {
        Board board = new Board(Map.of(
                at("e1"), WhiteKing,
                at("e2"), WhiteRook,
                at("e8"), BlackRook
        ));

        assertThat(validator.validMoves(board, at("e2")))
                .containsExactlyInAnyOrder(at("e3"), at("e4"), at("e5"), at("e6"), at("e7"), at("e8"));
        assertThat(validator.isValid(board, new Move(at("e2"), at("d2")))).isFalse();
    }

    @Test
    @DisplayName("Cuando el rey esta en jaque solo debe permitir jugadas que eviten el jaque")
    void shouldOnlyAllowMovesThatResolveCheck() {
        Board board = new Board(Map.of(
                at("e1"), WhiteKing,
                at("d2"), WhiteRook,
                at("a1"), WhitePawn,
                at("e8"), BlackRook
        ));

        assertThat(validator.validMoves(board, at("a1"))).isEmpty();
        assertThat(validator.validMoves(board, at("d2")))
                .containsExactlyInAnyOrder(at("e2"));
    }
}
