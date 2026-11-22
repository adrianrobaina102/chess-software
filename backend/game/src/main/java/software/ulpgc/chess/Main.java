package software.ulpgc.chess;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import software.ulpgc.chess.architecture.usecases.game.CreateGameUseCase;
import software.ulpgc.chess.architecture.usecases.board.GetAvailableMovesUseCase;
import software.ulpgc.chess.architecture.usecases.board.GetGameStatusUseCase;
import software.ulpgc.chess.architecture.usecases.game.GetGameUseCase;
import software.ulpgc.chess.architecture.usecases.board.GetInitialBoardUseCase;
import software.ulpgc.chess.architecture.usecases.board.MovePieceUseCase;
import software.ulpgc.chess.architecture.usecases.game.PlayMoveUseCase;
import software.ulpgc.chess.architecture.ports.in.BoardDeserializer;
import software.ulpgc.chess.architecture.ports.in.GamePositionDeserializer;
import software.ulpgc.chess.architecture.ports.in.MoveDeserializer;
import software.ulpgc.chess.architecture.ports.in.PositionDeserializer;
import software.ulpgc.chess.architecture.ports.in.StateDeserializer;
import software.ulpgc.chess.architecture.ports.out.BoardSerializer;
import software.ulpgc.chess.architecture.ports.out.GamePositionSerializer;
import software.ulpgc.chess.architecture.ports.out.GameStore;
import software.ulpgc.chess.architecture.ports.out.PositionSerializer;
import software.ulpgc.chess.architecture.ports.out.StateSerializer;
import software.ulpgc.chess.architecture.domain.MoveValidator;
import software.ulpgc.chess.infrastructure.adapters.in.deserializers.*;
import software.ulpgc.chess.infrastructure.adapters.in.rest.*;
import software.ulpgc.chess.infrastructure.adapters.out.AlgebraicPositionSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenBoardSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenGamePositionSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenStateSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FileGameStore;
import software.ulpgc.chess.architecture.domain.CheckDetector;
import software.ulpgc.chess.architecture.domain.ChessMoveValidator;
import software.ulpgc.chess.architecture.domain.GameStatusEvaluator;
import software.ulpgc.chess.architecture.domain.PseudoLegalMoveValidator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        // 1. Adaptadores compartidos
        BoardSerializer boardSerializer = new FenBoardSerializer();
        BoardDeserializer boardDeserializer = new FenBoardDeserializer();
        StateSerializer stateSerializer = new FenStateSerializer();
        StateDeserializer stateDeserializer = new FenStateDeserializer();
        GamePositionSerializer gamePositionSerializer = new FenGamePositionSerializer(boardSerializer, stateSerializer);
        GamePositionDeserializer gamePositionDeserializer = new FenGamePositionDeserializer(boardDeserializer, stateDeserializer);
        MoveDeserializer moveDeserializer = new UciMoveDeserializer();
        PositionSerializer positionSerializer = new AlgebraicPositionSerializer();
        PositionDeserializer positionDeserializer = new AlgebraicPositionDeserializer();
        MoveValidator pseudoLegalValidator = new PseudoLegalMoveValidator();
        CheckDetector checkDetector = new CheckDetector(pseudoLegalValidator);
        MoveValidator moveValidator = new ChessMoveValidator(pseudoLegalValidator, checkDetector);
        GameStatusEvaluator gameStatusEvaluator = new GameStatusEvaluator(moveValidator, checkDetector);
        GameStore gameStore = new FileGameStore(new File("games"), gamePositionSerializer, gamePositionDeserializer);
        Gson gson = new Gson();

        // 2. Casos de uso (Pure DI)
        GetInitialBoardUseCase getInitialBoardUseCase = new GetInitialBoardUseCase(gamePositionSerializer);
        CreateGameUseCase createGameUseCase = new CreateGameUseCase(gameStore, gamePositionSerializer);
        GetGameUseCase getGameUseCase = new GetGameUseCase(
                gameStore, gamePositionSerializer, checkDetector, gameStatusEvaluator, positionSerializer
        );
        PlayMoveUseCase playMoveUseCase = new PlayMoveUseCase(
                gameStatusEvaluator, gameStore, moveDeserializer, moveValidator, gamePositionSerializer, checkDetector, positionSerializer
        );
        MovePieceUseCase movePieceUseCase = new MovePieceUseCase(gamePositionDeserializer, moveDeserializer, gamePositionSerializer, moveValidator);
        GetAvailableMovesUseCase getAvailableMovesUseCase = new GetAvailableMovesUseCase(
                positionSerializer, positionDeserializer, moveValidator, gamePositionDeserializer
        );
        GetGameStatusUseCase getGameStatusUseCase = new GetGameStatusUseCase(
                gamePositionDeserializer, gameStatusEvaluator, checkDetector, positionSerializer
        );

        // 3. Servidor Javalin configurado para servir el frontend estÃƒÂ¡tico
        String frontendPath = resolveFrontendPath();
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(frontendPath, Location.EXTERNAL);
        }).start(7070);

        // 4. Endpoints
        app.get("/api/board/initial", context -> {
            new GetInitialBoardEndPoint(getInitialBoardUseCase, context).execute();
        });

        app.get("/api/board/move", context -> {
            new MovePieceEndPoint(movePieceUseCase, context).execute();
        });

        app.get("/api/board/moves", context -> {
            new GetAvailableMovesEndPoint(getAvailableMovesUseCase, context, gson).execute();
        });

        app.get("/api/board/status", context -> {
            new GetGameStatusEndPoint(gson, getGameStatusUseCase, context).execute();
        });

        app.post("/api/games", context -> {
            new CreateGameEndPoint(gson, context, createGameUseCase).execute();
        });

        app.get("/api/games", context -> {
            new GetGameEndPoint(gson, context, getGameUseCase).execute();
        });

        app.put("/api/games", context -> {
            new UpdateGameEndPoint(gson, context, playMoveUseCase).execute();
        });

        System.out.println("Servidor iniciado:");
        System.out.println(" - AplicaciÃƒÂ³n Web:  http://localhost:7070");
        System.out.println(" - Tablero inicial: http://localhost:7070/api/board/initial");
        System.out.println(" - Mover pieza:     http://localhost:7070/api/board/move?board=...&move=e2e4");
        System.out.println(" - Movimientos:     http://localhost:7070/api/board/moves?board=...&square=e2");
    }

    private static String resolveFrontendPath() {
        if (Files.isDirectory(Path.of("frontend"))) {
            return "frontend";
        }
        if (Files.isDirectory(Path.of("../../frontend"))) {
            return "../../frontend";
        }
        return "frontend";
    }
}
