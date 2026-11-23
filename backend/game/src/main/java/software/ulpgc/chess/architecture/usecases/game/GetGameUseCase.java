package software.ulpgc.chess.architecture.usecases.game;

import software.ulpgc.chess.architecture.usecases.UseCase;

import software.ulpgc.chess.architecture.domain.CheckDetector;
import software.ulpgc.chess.architecture.domain.GameStatusEvaluator;
import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.ports.out.GamePositionSerializer;
import software.ulpgc.chess.architecture.ports.out.GameStore;
import software.ulpgc.chess.architecture.ports.out.PositionSerializer;

import java.util.NoSuchElementException;
import java.util.Objects;

public class GetGameUseCase implements UseCase<String, GetGameUseCase.Output> {
    private final GameStore gameStore;
    private final GamePositionSerializer gamePositionserializer;
    private final CheckDetector checkDetector;
    private final GameStatusEvaluator gameStatusEvaluator;
    private final PositionSerializer positionSerializer;

    public GetGameUseCase(GameStore gameStore, GamePositionSerializer gamePositionserializer, CheckDetector checkDetector, GameStatusEvaluator gameStatusEvaluator, PositionSerializer positionSerializer) {
        this.gameStore = gameStore;
        this.gamePositionserializer = gamePositionserializer;
        this.checkDetector = checkDetector;
        this.gameStatusEvaluator = gameStatusEvaluator;
        this.positionSerializer = positionSerializer;
    }
    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private GameStore gameStore;
        private GamePositionSerializer serializer;
        private CheckDetector checkDetector;
        private GameStatusEvaluator gameStatusEvaluator;
        private PositionSerializer positionSerializer;
        public Builder gameStore(GameStore gameStore) {
            this.gameStore = gameStore;
            return this;
        }
        public Builder serializer(GamePositionSerializer serializer) {
            this.serializer = serializer;
            return this;
        }
        public Builder checkDetector(CheckDetector checkDetector) {
            this.checkDetector = checkDetector;
            return this;
        }
        public Builder gameStatusEvaluator(GameStatusEvaluator gameStatusEvaluator) {
            this.gameStatusEvaluator = gameStatusEvaluator;
            return this;
        }
        public Builder positionSerializer(PositionSerializer positionSerializer) {
            this.positionSerializer = positionSerializer;
            return this;
        }
        public GetGameUseCase build() {
            return new GetGameUseCase(
                    Objects.requireNonNull(gameStore, "gameStore is required"),
                    Objects.requireNonNull(serializer, "serializer is required"),
                    Objects.requireNonNull(checkDetector, "detector is required"),
                    Objects.requireNonNull(gameStatusEvaluator, "gameStatusEvaluator is required"),
                    Objects.requireNonNull(positionSerializer, "positionSerializer is required")
            );
        }
    }
    public record Output(String idGame, String fen, String status, String kingSquare ) {}
    @Override
    public Output execute(String idGame) {
        return gameStore.load(idGame).map(gamePosition -> buildOutput(idGame, gamePosition)).orElseThrow(() -> new NoSuchElementException("Partida no encontrada: " + idGame));
    }
    private Output buildOutput(String idGame, GamePosition position) {
        return toOutput(idGame, position, this.gamePositionserializer, this.gameStatusEvaluator, this.checkDetector, this.positionSerializer);
    }

    static Output toOutput(
            String idGame,
            GamePosition position,
            GamePositionSerializer serializer,
            GameStatusEvaluator gameStatusEvaluator,
            CheckDetector detector,
            PositionSerializer positionSerializer) {
        String status = gameStatusEvaluator.evaluate(position).name();
        String fen = serializer.serialize(position);
        String kingSquare = detector.findKingSquare(position.board(), position.state().side())
                .map(positionSerializer::serialize)
                .orElse("");
        return new Output(idGame, fen, status, kingSquare);
    }
}
