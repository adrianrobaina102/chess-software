package tests.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.domain.CheckDetector;
import software.ulpgc.chess.architecture.domain.ChessMoveValidator;
import software.ulpgc.chess.architecture.domain.GameStatusEvaluator;
import software.ulpgc.chess.architecture.domain.MoveValidator;
import software.ulpgc.chess.architecture.domain.PseudoLegalMoveValidator;
import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.ports.out.GameStore;
import software.ulpgc.chess.architecture.usecases.GetGameUseCase;
import software.ulpgc.chess.infrastructure.adapters.out.AlgebraicPositionSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenBoardSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenGamePositionSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenStateSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GetGameUseCaseTest {

    private GetGameUseCase buildUseCase(Map<String, GamePosition> memory) {
        GameStore fakeStore = new GameStore() {
            @Override
            public void save(String id, GamePosition position) {
                memory.put(id, position);
            }

            @Override
            public Optional<GamePosition> load(String id) {
                return Optional.ofNullable(memory.get(id));
            }
        };
        MoveValidator pseudoLegal = new PseudoLegalMoveValidator();
        CheckDetector checkDetector = new CheckDetector(pseudoLegal);
        MoveValidator moveValidator = new ChessMoveValidator(pseudoLegal, checkDetector);
        GameStatusEvaluator statusEvaluator = new GameStatusEvaluator(moveValidator, checkDetector);
        return new GetGameUseCase(
                fakeStore,
                new FenGamePositionSerializer(new FenBoardSerializer(), new FenStateSerializer()),
                checkDetector,
                statusEvaluator,
                new AlgebraicPositionSerializer()
        );
    }

    @Test
    @DisplayName("Debe devolver id, fen, status y kingSquare de una partida existente")
    void shouldReturnExistingGame() {
        Map<String, GamePosition> memory = new HashMap<>();
        memory.put("game-1", GamePosition.initial());
        GetGameUseCase useCase = buildUseCase(memory);

        GetGameUseCase.Output output = useCase.execute("game-1");

        assertThat(output.idGame()).isEqualTo("game-1");
        assertThat(output.fen()).isEqualTo("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq");
        assertThat(output.status()).isEqualTo("Playing");
        assertThat(output.kingSquare()).isEqualTo("e1");
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando la partida no existe")
    void shouldThrowWhenGameNotFound() {
        GetGameUseCase useCase = buildUseCase(new HashMap<>());

        assertThatThrownBy(() -> useCase.execute("missing-id"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("missing-id");
    }
}
