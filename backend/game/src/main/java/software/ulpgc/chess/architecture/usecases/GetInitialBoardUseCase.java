package software.ulpgc.chess.architecture.usecases;

import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.board.Board;
import software.ulpgc.chess.architecture.model.board.State;
import software.ulpgc.chess.architecture.ports.out.BoardSerializer;
import software.ulpgc.chess.architecture.ports.out.GamePositionSerializer;

public class GetInitialBoardUseCase implements UseCase<Void, String> {
    private final GamePositionSerializer serializer;

    public GetInitialBoardUseCase(GamePositionSerializer serializer) {
        this.serializer = serializer;
    }
    @Override
    public String execute(Void input) {
        return this.serializer.serialize(new GamePosition(Board.initial(), State.init()));
    }
}
