package software.ulpgc.chess.infrastructure.adapters.in;

import com.google.gson.Gson;
import io.javalin.http.Context;
import software.ulpgc.chess.architecture.usecases.GetGameStatusUseCase;

public class GetGameStatusEndPoint implements EndPoint{
    private final Gson gson;
    private final GetGameStatusUseCase useCase;
    private final Context context;
    public GetGameStatusEndPoint(Gson gson, GetGameStatusUseCase useCase, Context context) {
        this.gson = gson;
        this.useCase = useCase;
        this.context = context;
    }

    @Override
    public void execute() {
        String input = this.context.queryParam("board");
        this.context.contentType("application/json").result(gson.toJson(useCase.execute(input)));
    }
}
