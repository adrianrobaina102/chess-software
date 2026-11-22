package tests.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.domain.*;
import software.ulpgc.chess.architecture.usecases.board.GetGameStatusUseCase;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.FenBoardDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.FenGamePositionDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.FenStateDeserializer;
import software.ulpgc.chess.infrastructure.adapters.out.AlgebraicPositionSerializer;

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
                new GameStatusEvaluator(validator, checkDetector),
                checkDetector,
                new AlgebraicPositionSerializer()
        );
    }

    @Test
    @DisplayName("Debe devolver Playing y kingSquare null para el FEN inicial")
    void shouldReturnPlayingForInitialFen() {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq";

        GetGameStatusUseCase.Output output = useCase.execute(fen);

        assertThat(output.status()).isEqualTo("Playing");
        assertThat(output.kingSquare()).isNull();
    }

    @Test
    @DisplayName("Debe devolver Checkmate y la casilla del rey amenazado para el mate del loco")
    void shouldReturnCheckmateAndKingSquareForFoolsMate() {
        String foolsMateFen = "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq";

        GetGameStatusUseCase.Output output = useCase.execute(foolsMateFen);

        assertThat(output.status()).isEqualTo("Checkmate");
        assertThat(output.kingSquare()).isEqualTo("e1");
    }
}
