package tests.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.domain.*;
import software.ulpgc.chess.architecture.model.GameStatus;
import software.ulpgc.chess.architecture.usecases.GetGameStatusUseCase;
import software.ulpgc.chess.infrastructure.adapters.in.FenBoardDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.FenGamePositionDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.FenStateDeserializer;

import static org.assertj.core.api.Assertions.assertThat;

public class GetGameStatusUseCaseTest {

    private GetGameStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        MoveValidator pseudoLegal = new PseudoLegalMoveValidator();
        CheckDetector checkDetector = new CheckDetector(pseudoLegal);
        MoveValidator validator = new ChessMoveValidator(pseudoLegal, checkDetector);
        useCase = new GetGameStatusUseCase(
                new FenGamePositionDeserializer(new FenBoardDeserializer(), new FenStateDeserializer()),
                new GameStatusEvaluator(validator, checkDetector)
        );
    }

    @Test
    @DisplayName("Debe devolver Playing para el FEN inicial")
    void shouldReturnPlayingForInitialFen() {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq";

        assertThat(useCase.execute(fen)).isEqualTo(GameStatus.Playing);
    }

    @Test
    @DisplayName("Debe devolver Checkmate para el mate del loco")
    void shouldReturnCheckmateForFoolsMate() {
        String foolsMateFen = "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq";

        assertThat(useCase.execute(foolsMateFen)).isEqualTo(GameStatus.Checkmate);
    }
}
