package software.ulpgc.chess.architecture.usecases;

import software.ulpgc.chess.architecture.domain.GameStatusEvaluator;
import software.ulpgc.chess.architecture.model.GameStatus;
import software.ulpgc.chess.architecture.ports.in.GamePositionDeserializer;

public class GetGameStatusUseCase implements UseCase<String, GameStatus> {
    private final GamePositionDeserializer positionDeserializer;
    private final GameStatusEvaluator gameStatusEvaluator;

    public GetGameStatusUseCase(GamePositionDeserializer positionDeserializer, GameStatusEvaluator gameStatusEvaluator) {
        this.positionDeserializer = positionDeserializer;
        this.gameStatusEvaluator = gameStatusEvaluator;
    }

    @Override
    public GameStatus execute(String input) {
        return this.gameStatusEvaluator.evaluate(this.positionDeserializer.deserialize(input));
    }
}
