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
import software.ulpgc.chess.architecture.usecases.game.GetGameUseCase;
import software.ulpgc.chess.architecture.usecases.game.PlayMoveUseCase;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.UciMoveDeserializer;
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

public class PlayMoveUseCaseTest {

    private PlayMoveUseCase buildUseCase(Map<String, GamePosition> memory) {
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
        return new PlayMoveUseCase(
                statusEvaluator,
                fakeStore,
                new UciMoveDeserializer(),
                moveValidator,
                new FenGamePositionSerializer(new FenBoardSerializer(), new FenStateSerializer()),
                checkDetector,
                new AlgebraicPositionSerializer()
        );
    }

    @Test
    @DisplayName("Debe ejecutar un movimiento válido, guardar la nueva posición y devolver el nuevo estado")
    void shouldApplyValidMoveAndPersist() {
        Map<String, GamePosition> memory = new HashMap<>();
        memory.put("game-1", GamePosition.initial());
        PlayMoveUseCase useCase = buildUseCase(memory);

        GetGameUseCase.Output output = useCase.execute(new PlayMoveUseCase.Input("game-1", "e2e4"));

        assertThat(output.idGame()).isEqualTo("game-1");
        assertThat(output.fen()).isEqualTo("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq");
        assertThat(output.status()).isEqualTo("Playing");
        assertThat(output.kingSquare()).isEqualTo("e8");
        assertThat(memory.get("game-1")).isNotEqualTo(GamePosition.initial());
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando la partida no existe")
    void shouldThrowWhenGameNotFound() {
        PlayMoveUseCase useCase = buildUseCase(new HashMap<>());

        assertThatThrownBy(() -> useCase.execute(new PlayMoveUseCase.Input("missing-id", "e2e4")))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("missing-id");
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el movimiento es ilegal o no corresponde al turno")
    void shouldThrowWhenMoveIsInvalid() {
        Map<String, GamePosition> memory = new HashMap<>();
        memory.put("game-1", GamePosition.initial());
        PlayMoveUseCase useCase = buildUseCase(memory);

        assertThatThrownBy(() -> useCase.execute(new PlayMoveUseCase.Input("game-1", "e2e5")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.execute(new PlayMoveUseCase.Input("game-1", "e7e5")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(memory.get("game-1")).isEqualTo(GamePosition.initial());
    }
}
