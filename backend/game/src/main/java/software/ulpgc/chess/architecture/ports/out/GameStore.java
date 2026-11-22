package software.ulpgc.chess.architecture.ports.out;

import software.ulpgc.chess.architecture.model.GamePosition;

import java.util.Optional;

public interface GameStore {
    void save(String id, GamePosition position);
    Optional<GamePosition> load(String id);
}
