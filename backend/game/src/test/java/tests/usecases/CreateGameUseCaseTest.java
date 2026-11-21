package tests.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.ports.out.GameStore;
import software.ulpgc.chess.architecture.usecases.CreateGameUseCase;
import software.ulpgc.chess.infrastructure.adapters.out.FenBoardSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenGamePositionSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenStateSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateGameUseCaseTest {

    @Test
    @DisplayName("Debe crear una partida inicial, guardarla en GameStore y devolver id, fen y estado Playing")
    void shouldCreateAndPersistInitialGame() {
        Map<String, GamePosition> memory = new HashMap<>();
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
        CreateGameUseCase useCase = new CreateGameUseCase(
                fakeStore,
                new FenGamePositionSerializer(new FenBoardSerializer(), new FenStateSerializer())
        );

        CreateGameUseCase.Output output = useCase.execute();

        assertThat(output.idGame()).isNotBlank();
        assertThat(output.fen()).isEqualTo("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq");
        assertThat(output.status()).isEqualTo("Playing");
        assertThat(fakeStore.load(output.idGame())).contains(GamePosition.initial());
    }
}
