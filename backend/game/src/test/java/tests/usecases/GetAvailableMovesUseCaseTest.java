package tests.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.domain.ChessMoveValidator;
import software.ulpgc.chess.architecture.usecases.board.GetAvailableMovesUseCase;
import software.ulpgc.chess.architecture.usecases.board.GetAvailableMovesUseCase.Input;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.AlgebraicPositionDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.FenBoardDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.FenGamePositionDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.FenStateDeserializer;
import software.ulpgc.chess.infrastructure.adapters.out.AlgebraicPositionSerializer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetAvailableMovesUseCaseTest {

    private GetAvailableMovesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetAvailableMovesUseCase(
                new AlgebraicPositionSerializer(),
                new AlgebraicPositionDeserializer(),
                new ChessMoveValidator(),
                new FenGamePositionDeserializer(new FenBoardDeserializer(), new FenStateDeserializer())
        );
    }

    @Test
    @DisplayName("Debe devolver e3 y e4 para el peon blanco en e2 desde la posicion inicial")
    void shouldReturnAvailableMovesForInitialWhitePawn() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq";

        List<String> moves = useCase.execute(new Input(initialFen, "e2"));

        assertThat(moves).containsExactlyInAnyOrder("e3", "e4");
    }

    @Test
    @DisplayName("Debe devolver f3 y h3 para el caballo blanco en g1 desde la posicion inicial")
    void shouldReturnAvailableMovesForInitialWhiteKnight() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq";

        List<String> moves = useCase.execute(new Input(initialFen, "g1"));

        assertThat(moves).containsExactlyInAnyOrder("f3", "h3");
    }

    @Test
    @DisplayName("Debe devolver lista vacia cuando la casilla consultada esta vacia")
    void shouldReturnEmptyListWhenSquareIsEmpty() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq";

        List<String> moves = useCase.execute(new Input(initialFen, "e4"));

        assertThat(moves).isEmpty();
    }

    @Test
    @DisplayName("Debe devolver lista vacia al consultar una pieza rival fuera de turno")
    void shouldReturnEmptyListForOpponentPieceOutOfTurn() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq";

        List<String> moves = useCase.execute(new Input(initialFen, "e7"));

        assertThat(moves).isEmpty();
    }
}
