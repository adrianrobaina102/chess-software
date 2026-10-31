package tests.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.application.usecases.GetInitialBoardUseCase;
import software.ulpgc.chess.infrastructure.adapters.fen.FenBoardSerializer;

import static org.assertj.core.api.Assertions.assertThat;

public class GetInitialBoardUseCaseTest {

    @Test
    @DisplayName("Debe devolver el tablero inicial en notacion FEN")
    void shouldReturnInitialBoardAsFen() {
        GetInitialBoardUseCase useCase = new GetInitialBoardUseCase(new FenBoardSerializer());

        String initialFen = useCase.execute();

        assertThat(initialFen).isEqualTo("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
    }
}
