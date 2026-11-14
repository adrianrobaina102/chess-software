package software.ulpgc.chess.infrastructure.adapters.in;

import com.google.gson.Gson;
import io.javalin.http.Context;
import software.ulpgc.chess.architecture.domain.GetAvailableMovesUseCase;

public class GetAvalibleEndPoint implements EndPoint{

    private final GetAvailableMovesUseCase useCase;
    private final Context context;
    private final Gson gson;
    public GetAvalibleEndPoint(GetAvailableMovesUseCase useCase, Context context, Gson gson) {
        this.useCase = useCase;
        this.context = context;
        this.gson = gson;
    }

    @Override
    public void execute() {
        GetAvailableMovesUseCase.Input input = new GetAvailableMovesUseCase.Input(context.queryParam("board"), context.queryParam("square"));
        this.context.contentType("application/json").result(gson.toJson(useCase.execute(input)));
    }
}
