package tests.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.usecases.GetInitialBoardUseCase;
import software.ulpgc.chess.infrastructure.adapters.out.FenBoardSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenGamePositionSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenStateSerializer;

import static org.assertj.core.api.Assertions.assertThat;

public class GetInitialBoardUseCaseTest {

    @Test
    @DisplayName("Debe devolver la posicion inicial en notacion FEN con turno y enroques")
    void shouldReturnInitialBoardAsFen() {
        GetInitialBoardUseCase useCase = new GetInitialBoardUseCase(
                new FenGamePositionSerializer(new FenBoardSerializer(), new FenStateSerializer())
        );

        String initialFen = useCase.execute();

        assertThat(initialFen).isEqualTo("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq");
    }
}
