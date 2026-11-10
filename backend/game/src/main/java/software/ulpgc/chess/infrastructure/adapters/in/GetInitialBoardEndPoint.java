package software.ulpgc.chess.infrastructure.adapters.in;

import io.javalin.http.Context;
import software.ulpgc.chess.architecture.domain.GetInitialBoardUseCase;

public class GetInitialBoardEndPoint implements EndPoint {
    private final GetInitialBoardUseCase useCase;
    private final Context context;
    public GetInitialBoardEndPoint(GetInitialBoardUseCase useCase, Context context) {
        this.useCase = useCase;
        this.context = context;
    }
    @Override
    public void execute() {
        this.context.result(useCase.execute());
    }
}
