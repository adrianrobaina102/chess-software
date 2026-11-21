package software.ulpgc.chess.architecture.usecases;

import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.GameStatus;
import software.ulpgc.chess.architecture.ports.out.GamePositionSerializer;
import software.ulpgc.chess.architecture.ports.out.GameStore;

import java.util.UUID;
public class CreateGameUseCase implements UseCase<Void, CreateGameUseCase.Output> {
    private final GameStore gameStore;
    private final GamePositionSerializer serializer;
    public record Output(String idGame, String fen, String status) {}
    public CreateGameUseCase(GameStore gameStore, GamePositionSerializer serializer) {
        this.gameStore = gameStore;
        this.serializer = serializer;
    }

    @Override
    public Output execute(Void input) {
        String idGame = UUID.randomUUID().toString();
        GamePosition initial = GamePosition.initial();
        gameStore.save(idGame, initial);
        return new Output(idGame, this.serializer.serialize(initial), GameStatus.Playing.name());
    }
}
