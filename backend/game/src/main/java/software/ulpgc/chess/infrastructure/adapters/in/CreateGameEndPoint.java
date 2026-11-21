package software.ulpgc.chess.infrastructure.adapters.in;

import com.google.gson.Gson;
import io.javalin.http.Context;
import software.ulpgc.chess.architecture.usecases.CreateGameUseCase;

public class CreateGameEndPoint implements EndPoint{
    private final Gson gson;
    private final Context context;
    private final CreateGameUseCase useCase;

    public CreateGameEndPoint(Gson gson, Context context, CreateGameUseCase useCase) {
        this.gson = gson;
        this.context = context;
        this.useCase = useCase;
    }

    @Override
    public void execute() {
        context.status(201).contentType("application/json").result(gson.toJson(useCase.execute()));
    }
}
