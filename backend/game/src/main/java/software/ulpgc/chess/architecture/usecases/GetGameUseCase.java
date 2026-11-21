package software.ulpgc.chess.architecture.usecases;

import software.ulpgc.chess.architecture.domain.CheckDetector;
import software.ulpgc.chess.architecture.domain.GameStatusEvaluator;
import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.ports.out.GamePositionSerializer;
import software.ulpgc.chess.architecture.ports.out.GameStore;
import software.ulpgc.chess.architecture.ports.out.PositionSerializer;

import java.util.NoSuchElementException;

public class GetGameUseCase implements UseCase<String, GetGameUseCase.Output> {
    private final GameStore gameStore;
    private final GamePositionSerializer serializer;
    private final CheckDetector detector;
    private final GameStatusEvaluator gameStatusEvaluator;
    private final PositionSerializer positionSerializer;

    public GetGameUseCase(GameStore gameStore, GamePositionSerializer serializer, CheckDetector detector, GameStatusEvaluator gameStatusEvaluator, PositionSerializer positionSerializer) {
        this.gameStore = gameStore;
        this.serializer = serializer;
        this.detector = detector;
        this.gameStatusEvaluator = gameStatusEvaluator;
        this.positionSerializer = positionSerializer;
    }

    public record Output(String idGame, String fen, String status, String kingSquare ) {}
    @Override
    public Output execute(String idGame) {
        return gameStore.load(idGame).map(gamePosition -> buildOutput(idGame, gamePosition)).orElseThrow(() -> new NoSuchElementException("Partida no encontrada: " + idGame));
    }
    private Output buildOutput(String idGame, GamePosition position) {
        String status = this.gameStatusEvaluator.evaluate(position).name();
        String fen = this.serializer.serialize(position);
        String kingSquare  = this.detector.findKingSquare(position.board(), position.state().side())
                .map(this.positionSerializer::serialize)
                .orElse("");
        return new Output(idGame, fen, status, kingSquare);
    }
}
