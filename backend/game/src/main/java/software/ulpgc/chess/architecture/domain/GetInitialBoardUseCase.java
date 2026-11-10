package software.ulpgc.chess.architecture.domain;

import software.ulpgc.chess.architecture.domain.model.Board;
import software.ulpgc.chess.architecture.ports.out.BoardSerializer;

public class GetInitialBoardUseCase implements UseCase<Void, String> {
    private final BoardSerializer serializer;

    public GetInitialBoardUseCase(BoardSerializer serializer) {
        this.serializer = serializer;
    }
    @Override
    public String execute(Void input) {
        return this.serializer.serialize(Board.initial());
    }
}
