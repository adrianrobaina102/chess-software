package software.ulpgc.chess.application.usecases;

import software.ulpgc.chess.application.domain.model.Board;
import software.ulpgc.chess.application.ports.serializer.BoardSerializer;

public class GetInitialBoardUseCase implements UseCases<Void, String> {
    private final BoardSerializer serializer;

    public GetInitialBoardUseCase(BoardSerializer serializer) {
        this.serializer = serializer;
    }
    @Override
    public String execute(Void input) {
        return this.serializer.serialize(Board.initial());
    }
}
