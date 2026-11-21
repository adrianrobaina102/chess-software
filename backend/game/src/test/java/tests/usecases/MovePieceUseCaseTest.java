package tests.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.domain.ChessMoveValidator;
import software.ulpgc.chess.architecture.usecases.MovePieceUseCase;
import software.ulpgc.chess.architecture.usecases.MovePieceUseCase.Input;
import software.ulpgc.chess.infrastructure.adapters.in.FenBoardDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.FenGamePositionDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.FenStateDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.UciMoveDeserializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenBoardSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenGamePositionSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenStateSerializer;

import static org.assertj.core.api.Assertions.assertThat;

public class MovePieceUseCaseTest {

    private MovePieceUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new MovePieceUseCase(
                new FenGamePositionDeserializer(new FenBoardDeserializer(), new FenStateDeserializer()),
                new UciMoveDeserializer(),
                new FenGamePositionSerializer(new FenBoardSerializer(), new FenStateSerializer()),
                new ChessMoveValidator()
        );
    }

    @Test
    @DisplayName("Debe aplicar el movimiento e2e4 desde la posicion inicial y pasar el turno a negras")
    void shouldMovePawnFromE2ToE4() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq";
        String uciMove = "e2e4";

        String resultFen = useCase.execute(new Input(initialFen, uciMove));

        assertThat(resultFen).isEqualTo("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq");
    }

    @Test
    @DisplayName("Debe aplicar una respuesta de las negras g8f6 y devolver el turno a blancas")
    void shouldMoveBlackKnightFromG8ToF6() {
        String boardFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq";
        String uciMove = "g8f6";

        String resultFen = useCase.execute(new Input(boardFen, uciMove));

        assertThat(resultFen).isEqualTo("rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq");
    }

    @Test
    @DisplayName("No debe modificar el tablero si el movimiento es invalido")
    void shouldKeepBoardUnchangedWhenMoveIsInvalid() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq";
        String illegalMove = "e2e5";

        String resultFen = useCase.execute(new Input(initialFen, illegalMove));

        assertThat(resultFen).isEqualTo(initialFen);
    }

    @Test
    @DisplayName("No debe permitir mover una pieza fuera de su turno")
    void shouldRejectMoveWhenNotPiecesTurn() {
        String blackTurnFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq";

        String resultFen = useCase.execute(new Input(blackTurnFen, "d2d4"));

        assertThat(resultFen).isEqualTo(blackTurnFen);
    }
}
