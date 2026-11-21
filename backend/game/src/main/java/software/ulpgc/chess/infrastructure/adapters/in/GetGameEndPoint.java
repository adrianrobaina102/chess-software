package software.ulpgc.chess.infrastructure.adapters.in;

import com.google.gson.Gson;
import io.javalin.http.Context;
import software.ulpgc.chess.architecture.usecases.GetGameUseCase;

import java.util.Map;
import java.util.NoSuchElementException;

public class GetGameEndPoint implements EndPoint {
    private final Gson gson;
    private final Context context;
    private final GetGameUseCase useCase;

    public GetGameEndPoint(Gson gson, Context context, GetGameUseCase useCase) {
        this.gson = gson;
        this.context = context;
        this.useCase = useCase;
    }

    @Override
    public void execute() {
        String id = context.queryParam("id");
        if (id == null || id.isBlank()) {
            sendError(400, "Missing required query parameter: id");
            return;
        }
        try {
            context.status(200).contentType("application/json").result(gson.toJson(useCase.execute(id)));
        } catch (NoSuchElementException e) {
            sendError(404, e.getMessage());
        }
    }

    private void sendError(int status, String message) {
        context.status(status).contentType("application/json").result(gson.toJson(Map.of("error", message)));
    }
}
