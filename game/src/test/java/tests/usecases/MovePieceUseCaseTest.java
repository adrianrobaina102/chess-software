package tests.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.application.usecases.MovePieceUseCase;
import software.ulpgc.chess.application.usecases.MovePieceUseCase.Input;
import software.ulpgc.chess.infrastructure.adapters.fen.FenBoardDeserializer;
import software.ulpgc.chess.infrastructure.adapters.fen.FenBoardSerializer;
import software.ulpgc.chess.infrastructure.adapters.uci.UciMoveDeserializer;

import static org.assertj.core.api.Assertions.assertThat;

public class MovePieceUseCaseTest {

    private MovePieceUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new MovePieceUseCase(
                new FenBoardDeserializer(),
                new UciMoveDeserializer(),
                new FenBoardSerializer()
        );
    }

    @Test
    @DisplayName("Debe aplicar el movimiento e2e4 desde la posicion inicial")
    void shouldMovePawnFromE2ToE4() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        String uciMove = "e2e4";

        String resultFen = useCase.execute(new Input(initialFen, uciMove));

        // En la fila 2 el peón de 'e' deja hueco (PPPP1PPP) y en la fila 4 aparece el peón (4P3)
        assertThat(resultFen).isEqualTo("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR");
    }

    @Test
    @DisplayName("Debe aplicar una respuesta de las negras g8f6")
    void shouldMoveBlackKnightFromG8ToF6() {
        String boardFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR";
        String uciMove = "g8f6";

        String resultFen = useCase.execute(new Input(boardFen, uciMove));

        // En la fila 8 desaparece el caballo en g8 (rnbqkb1r) y en la fila 6 aparece en f6 (5n2)
        assertThat(resultFen).isEqualTo("rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR");
    }
}
