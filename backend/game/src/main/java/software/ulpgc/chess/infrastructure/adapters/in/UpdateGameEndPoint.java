package software.ulpgc.chess.infrastructure.adapters.in;

import com.google.gson.Gson;
import io.javalin.http.Context;
import software.ulpgc.chess.architecture.usecases.PlayMoveUseCase;

import java.util.Map;
import java.util.NoSuchElementException;

public class UpdateGameEndPoint implements EndPoint{
    private final Gson gson;
    private final Context context;
    private final PlayMoveUseCase useCase;

    public UpdateGameEndPoint(Gson gson, Context context, PlayMoveUseCase useCase) {
        this.gson = gson;
        this.context = context;
        this.useCase = useCase;
    }

    @Override
    public void execute() {
        String id = context.queryParam("id");
        String move = context.queryParam("move");
        if (isMissing("id", id) || isMissing("move", move)) {
            return;
        }
        try {
            context.status(200).contentType("application/json").result(gson.toJson(useCase.execute(new PlayMoveUseCase.Input(id, move))));
        } catch (NoSuchElementException e) {
            sendError(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendError(400, e.getMessage());
        }
    }

    private boolean isMissing(String name, String value) {
        if (value == null || value.isBlank()) {
            sendError(400, "Missing required query parameter: " + name);
            return true;
        }
        return false;
    }
    private void sendError(int status, String message) {
        context.status(status).contentType("application/json").result(gson.toJson(Map.of("error", message)));
    }
}
