package software.ulpgc.chess;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import software.ulpgc.chess.architecture.ports.in.BoardDeserializer;
import software.ulpgc.chess.architecture.ports.in.MoveDeserializer;
import software.ulpgc.chess.architecture.ports.out.BoardSerializer;
import software.ulpgc.chess.architecture.usecases.GetInitialBoardUseCase;
import software.ulpgc.chess.architecture.usecases.MovePieceUseCase;
import software.ulpgc.chess.infrastructure.adapters.in.GetInitialBoardEndPoint;
import software.ulpgc.chess.infrastructure.adapters.in.MovePieceEndPoint;
import software.ulpgc.chess.infrastructure.adapters.in.FenBoardDeserializer;
import software.ulpgc.chess.infrastructure.adapters.in.UciMoveDeserializer;
import software.ulpgc.chess.infrastructure.adapters.out.FenBoardSerializer;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        // 1. Adaptadores compartidos
        BoardSerializer boardSerializer = new FenBoardSerializer();
        BoardDeserializer boardDeserializer = new FenBoardDeserializer();
        MoveDeserializer moveDeserializer = new UciMoveDeserializer();

        // 2. Casos de uso (Pure DI)
        GetInitialBoardUseCase getInitialBoardUseCase = new GetInitialBoardUseCase(boardSerializer);
        MovePieceUseCase movePieceUseCase = new MovePieceUseCase(boardDeserializer, moveDeserializer, boardSerializer);

        // 3. Servidor Javalin configurado para servir el frontend estático
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

        System.out.println("Servidor iniciado:");
        System.out.println(" - Aplicación Web:  http://localhost:7070");
        System.out.println(" - Tablero inicial: http://localhost:7070/api/board/initial");
        System.out.println(" - Mover pieza:     http://localhost:7070/api/board/move?board=...&move=e2e4");
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
