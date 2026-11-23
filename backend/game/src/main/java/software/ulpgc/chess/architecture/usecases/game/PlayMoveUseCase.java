package software.ulpgc.chess.architecture.usecases.game;

import software.ulpgc.chess.architecture.usecases.UseCase;

import software.ulpgc.chess.architecture.domain.CheckDetector;
import software.ulpgc.chess.architecture.domain.GameStatusEvaluator;
import software.ulpgc.chess.architecture.domain.MoveValidator;
import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.board.Move;
import software.ulpgc.chess.architecture.model.board.Piece;
import software.ulpgc.chess.architecture.ports.in.MoveDeserializer;
import software.ulpgc.chess.architecture.ports.out.GamePositionSerializer;
import software.ulpgc.chess.architecture.ports.out.GameStore;
import software.ulpgc.chess.architecture.ports.out.PositionSerializer;

import java.util.NoSuchElementException;
import java.util.Objects;

public class PlayMoveUseCase implements UseCase<PlayMoveUseCase.Input, GetGameUseCase.Output> {
    public record Input(String idGame, String move) {}

    private final GameStatusEvaluator gameStatusEvaluator;
    private final GameStore gameStore;
    private final MoveDeserializer moveDeserializer;
    private final MoveValidator moveValidator;

    private final GamePositionSerializer gamePositionSerializer;
    private final CheckDetector checkDetector;
    private final PositionSerializer positionSerializer;

    public PlayMoveUseCase(GameStatusEvaluator gameStatusEvaluator, GameStore gameStore, MoveDeserializer moveDeserializer, MoveValidator moveValidator, GamePositionSerializer gamePositionSerializer, CheckDetector checkDetector, PositionSerializer positionSerializer) {
        this.gameStatusEvaluator = gameStatusEvaluator;
        this.gameStore = gameStore;
        this.moveDeserializer = moveDeserializer;
        this.moveValidator = moveValidator;
        this.gamePositionSerializer = gamePositionSerializer;
        this.checkDetector = checkDetector;
        this.positionSerializer = positionSerializer;
    }
    public static PlayMoveUseCase.Builder builder() {
        return new PlayMoveUseCase.Builder();
    }
    public static class Builder {
        private GameStatusEvaluator gameStatusEvaluator;
        private GameStore gameStore;
        private MoveDeserializer moveDeserializer;
        private MoveValidator moveValidator;
        private GamePositionSerializer gamePositionSerializer;
        private CheckDetector checkDetector;
        private PositionSerializer positionSerializer;
        public Builder gameStatusEvaluator(GameStatusEvaluator gameStatusEvaluator) {
            this.gameStatusEvaluator = gameStatusEvaluator;
            return this;
        }
        public Builder gameStore(GameStore gameStore) {
            this.gameStore = gameStore;
            return this;
        }
        public Builder moveDeserializer(MoveDeserializer moveDeserializer) {
            this.moveDeserializer = moveDeserializer;
            return this;
        }
        public Builder moveValidator(MoveValidator moveValidator) {
            this.moveValidator = moveValidator;
            return this;
        }
        public Builder gamePositionSerializer(GamePositionSerializer gamePositionSerializer) {
            this.gamePositionSerializer = gamePositionSerializer;
            return this;
        }
        public Builder checkDetector(CheckDetector checkDetector) {
            this.checkDetector = checkDetector;
            return this;
        }
        public Builder positionSerializer(PositionSerializer positionSerializer) {
            this.positionSerializer = positionSerializer;
            return this;
        }
        public PlayMoveUseCase build() {
            return new PlayMoveUseCase(
                    Objects.requireNonNull(gameStatusEvaluator, "gameStatusEvaluator is required"),
                    Objects.requireNonNull(gameStore, "gameStore is required"),
                    Objects.requireNonNull(moveDeserializer, "moveDeserializer is required"),
                    Objects.requireNonNull(moveValidator, "moveValidator is required"),
                    Objects.requireNonNull(gamePositionSerializer, "gamePositionSerializer is required"),
                    Objects.requireNonNull(checkDetector, "checkDetector is required"),
                    Objects.requireNonNull(positionSerializer, "positionSerializer is required")
            );
        }
    }

    @Override
    public GetGameUseCase.Output execute(PlayMoveUseCase.Input input) {
        GamePosition position = this.gameStore.load(input.idGame())
                .orElseThrow(() -> new NoSuchElementException("Partida no encontrada: " + input.idGame()));
        Move move = this.moveDeserializer.deserialize(input.move());
        Piece piece = position.board().pieceAt(move.from());
        if (piece == null || piece.color() != position.state().side() || !this.moveValidator.isValid(position.board(), move)) {
            throw new IllegalArgumentException("Movimiento inválido: " + input.move());
        }
        GamePosition next = position.move(move);
        this.gameStore.save(input.idGame(), next);
        return GetGameUseCase.toOutput(
                input.idGame(), next, this.gamePositionSerializer, this.gameStatusEvaluator, this.checkDetector, this.positionSerializer
        );
    }
}
