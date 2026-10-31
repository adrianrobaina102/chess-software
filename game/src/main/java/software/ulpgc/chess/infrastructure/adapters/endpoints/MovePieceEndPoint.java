package software.ulpgc.chess.infrastructure.adapters.endpoints;

import io.javalin.http.Context;
import software.ulpgc.chess.application.usecases.MovePieceUseCase;
import software.ulpgc.chess.application.usecases.MovePieceUseCase.Input;
import software.ulpgc.chess.infrastructure.adapters.EndPoint;

public class MovePieceEndPoint implements EndPoint {
    private final MovePieceUseCase useCase;
    private final Context context;

    public MovePieceEndPoint(MovePieceUseCase useCase, Context context) {
        this.useCase = useCase;
        this.context = context;
    }

    @Override
    public void execute() {
        String board = context.queryParam("board");
        String move = context.queryParam("move");

        String result = useCase.execute(new Input(board, move));

        context.result(result);
    }
}
