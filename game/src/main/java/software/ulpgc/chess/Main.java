package software.ulpgc.chess;

import io.javalin.Javalin;
import software.ulpgc.chess.application.ports.deserializer.BoardDeserializer;
import software.ulpgc.chess.application.ports.deserializer.MoveDeserializer;
import software.ulpgc.chess.application.ports.serializer.BoardSerializer;
import software.ulpgc.chess.application.usecases.GetInitialBoardUseCase;
import software.ulpgc.chess.application.usecases.MovePieceUseCase;
import software.ulpgc.chess.infrastructure.adapters.endpoints.GetInitialBoardEndPoint;
import software.ulpgc.chess.infrastructure.adapters.endpoints.MovePieceEndPoint;
import software.ulpgc.chess.infrastructure.adapters.fen.FenBoardDeserializer;
import software.ulpgc.chess.infrastructure.adapters.fen.FenBoardSerializer;
import software.ulpgc.chess.infrastructure.adapters.uci.UciMoveDeserializer;

public class Main {
    public static void main(String[] args) {
        // 1. Adaptadores compartidos
        BoardSerializer boardSerializer = new FenBoardSerializer();
        BoardDeserializer boardDeserializer = new FenBoardDeserializer();
        MoveDeserializer moveDeserializer = new UciMoveDeserializer();

        // 2. Casos de uso (Pure DI)
        GetInitialBoardUseCase getInitialBoardUseCase = new GetInitialBoardUseCase(boardSerializer);
        MovePieceUseCase movePieceUseCase = new MovePieceUseCase(boardDeserializer, moveDeserializer, boardSerializer);

        // 3. Servidor Javalin
        Javalin app = Javalin.create().start(7070);

        // 4. Endpoints
        app.get("/api/board/initial", context -> {
            new GetInitialBoardEndPoint(getInitialBoardUseCase, context).execute();
        });

        app.get("/api/board/move", context -> {
            new MovePieceEndPoint(movePieceUseCase, context).execute();
        });

        System.out.println("Servidor iniciado:");
        System.out.println(" - Tablero inicial: http://localhost:7070/api/board/initial");
        System.out.println(" - Mover pieza:     http://localhost:7070/api/board/move?board=...&move=e2e4");
    }
}
