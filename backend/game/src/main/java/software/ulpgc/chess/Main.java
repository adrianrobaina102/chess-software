package software.ulpgc.chess;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import software.ulpgc.chess.architecture.domain.GetAvailableMovesUseCase;
import software.ulpgc.chess.architecture.domain.GetInitialBoardUseCase;
import software.ulpgc.chess.architecture.domain.MovePieceUseCase;
import software.ulpgc.chess.architecture.ports.in.BoardDeserializer;
import software.ulpgc.chess.architecture.ports.in.MoveDeserializer;
import software.ulpgc.chess.architecture.ports.in.PositionDeserializer;
import software.ulpgc.chess.architecture.ports.out.BoardSerializer;
import software.ulpgc.chess.architecture.ports.out.PositionSerializer;
import software.ulpgc.chess.architecture.ports.process.MoveValidator;
import software.ulpgc.chess.infrastructure.adapters.in.*;
import software.ulpgc.chess.infrastructure.adapters.out.AlgebraicPositionSerializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenBoardSerializer;
import software.ulpgc.chess.infrastructure.adapters.process.ChessMoveValidator;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        // 1. Adaptadores compartidos
        BoardSerializer boardSerializer = new FenBoardSerializer();
        BoardDeserializer boardDeserializer = new FenBoardDeserializer();
        MoveDeserializer moveDeserializer = new UciMoveDeserializer();
        PositionSerializer positionSerializer = new AlgebraicPositionSerializer();
        PositionDeserializer positionDeserializer = new AlgebraicPositionDeserializer();
        MoveValidator moveValidator = new ChessMoveValidator();
        Gson gson = new Gson();

        // 2. Casos de uso (Pure DI)
        GetInitialBoardUseCase getInitialBoardUseCase = new GetInitialBoardUseCase(boardSerializer);
        MovePieceUseCase movePieceUseCase = new MovePieceUseCase(boardDeserializer, moveDeserializer, boardSerializer, moveValidator);
        GetAvailableMovesUseCase getAvailableMovesUseCase = new GetAvailableMovesUseCase(
                positionSerializer, positionDeserializer, moveValidator, boardDeserializer
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
