package software.ulpgc.chess.infrastructure.adapters.out;

import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.ports.in.GamePositionDeserializer;
import software.ulpgc.chess.architecture.ports.out.GamePositionSerializer;
import software.ulpgc.chess.architecture.ports.out.GameStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileGameStore implements GameStore {
    private final File directory;
    private final GamePositionSerializer serializer;
    private final GamePositionDeserializer deserializer;
    public FileGameStore(File directory, GamePositionSerializer serializer, GamePositionDeserializer deserializer) {
        this.directory = directory;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public void save(String id, GamePosition position) {
        Path targetPath  = fileOf(id);
        try {
            String serializedPosition = this.serializer.serialize(position);
            Files.createDirectories(directory.toPath());
            Files.writeString(targetPath, serializedPosition);
        }catch (IOException e) {
            throw new RuntimeException("Error guardando la partida: " + id, e);
        }
    }

    @Override
    public Optional<GamePosition> load(String id) {
        Path targetPath = fileOf(id);
        if (!Files.exists(targetPath)) {
            return Optional.empty();
        }
        try {
            String content = Files.readString(targetPath).trim();
            return Optional.of(this.deserializer.deserialize(content));
        } catch (IOException e) {
            throw new RuntimeException("Error cargando la partida: " + id, e);
        }
    }
    private Path fileOf(String id) {
        if (id == null || !id.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException("Invalid game id: " + id);
        }
        return directory.toPath().resolve(id + ".fen");
    }
}
