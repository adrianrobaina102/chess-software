package tests.out;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.board.Move;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.FenBoardDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.FenGamePositionDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.FenStateDeserializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenBoardSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenGamePositionSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenStateSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FileGameStore;

import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static software.ulpgc.chess.architecture.model.board.Square.at;

public class FileGameStoreTest {

    @TempDir
    Path tempDir;

    private FileGameStore store;

    @BeforeEach
    void setUp() {
        store = new FileGameStore(
                tempDir.resolve("games").toFile(),
                new FenGamePositionSerializer(new FenBoardSerializer(), new FenStateSerializer()),
                new FenGamePositionDeserializer(new FenBoardDeserializer(), new FenStateDeserializer())
        );
    }

    @Test
    @DisplayName("Debe guardar y recuperar una partida por su id")
    void shouldSaveAndLoadGamePosition() {
        GamePosition afterE4 = GamePosition.initial().move(new Move(at("e2"), at("e4")));

        store.save("game-1", afterE4);
        Optional<GamePosition> loaded = store.load("game-1");

        assertThat(loaded).contains(afterE4);
    }

    @Test
    @DisplayName("Debe devolver Optional vacio si la partida no existe")
    void shouldReturnEmptyWhenGameDoesNotExist() {
        assertThat(store.load("missing-id")).isEmpty();
    }

    @Test
    @DisplayName("Debe rechazar identificadores de partida inseguros")
    void shouldRejectUnsafeGameId() {
        assertThatThrownBy(() -> store.save("../hack", GamePosition.initial()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
