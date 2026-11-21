package software.ulpgc.chess.architecture.usecases;

import software.ulpgc.chess.architecture.domain.CheckDetector;
import software.ulpgc.chess.architecture.domain.GameStatusEvaluator;
import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.GameStatus;
import software.ulpgc.chess.architecture.ports.in.GamePositionDeserializer;
import software.ulpgc.chess.architecture.ports.out.PositionSerializer;

public class GetGameStatusUseCase implements UseCase<String, GetGameStatusUseCase.Output> {
    private final GamePositionDeserializer positionDeserializer;
    private final GameStatusEvaluator gameStatusEvaluator;
    private final CheckDetector checkDetector;
    private final PositionSerializer positionSerializer;

    public record Output(String status, String kingSquare) {}

    public GetGameStatusUseCase(
            GamePositionDeserializer positionDeserializer,
            GameStatusEvaluator gameStatusEvaluator,
            CheckDetector checkDetector,
            PositionSerializer positionSerializer) {
        this.positionDeserializer = positionDeserializer;
        this.gameStatusEvaluator = gameStatusEvaluator;
        this.checkDetector = checkDetector;
        this.positionSerializer = positionSerializer;
    }

    @Override
    public Output execute(String input) {
        GamePosition position = this.positionDeserializer.deserialize(input);
        GameStatus status = this.gameStatusEvaluator.evaluate(position);
        String kingSquare = (status == GameStatus.Check || status == GameStatus.Checkmate)
                ? checkDetector.findKingSquare(position.board(), position.state().side())
                        .map(positionSerializer::serialize)
                        .orElse(null)
                : null;
        return new Output(status.name(), kingSquare);
    }
}
