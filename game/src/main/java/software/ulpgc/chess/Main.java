package software.ulpgc.chess;

import io.javalin.Javalin;
import software.ulpgc.chess.application.domain.model.Board;
import software.ulpgc.chess.application.domain.model.State;
import software.ulpgc.chess.application.ports.deserializer.BoardDeserializer;
import software.ulpgc.chess.application.ports.deserializer.StateDeserializer;
import software.ulpgc.chess.application.ports.serializer.BoardSerializer;
import software.ulpgc.chess.application.usecases.GetInitialBoardUseCase;
import software.ulpgc.chess.infrastructure.adapters.endpoints.GetInitialBoardEndPoint;
import software.ulpgc.chess.infrastructure.adapters.fen.FenBoardDeserializer;
import software.ulpgc.chess.infrastructure.adapters.fen.FenBoardSerializer;
import software.ulpgc.chess.infrastructure.adapters.fen.FenStateDeserializer;

// == compara direccion .equals Compara el objeto
public class Main {
    public static void main(String[] args) {
        BoardDeserializer boarDeserializer = new FenBoardDeserializer();
        StateDeserializer stateDeserializer = new FenStateDeserializer();
        String fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Board board = boarDeserializer.deserialize(fenString);
        State state = stateDeserializer.deserialize(fenString);
        System.out.println(board);
        System.out.println(state.toString());


        BoardSerializer boardSerializer = new FenBoardSerializer();
        GetInitialBoardUseCase getInitialBoardUseCase = new GetInitialBoardUseCase(boardSerializer);

        Javalin app = Javalin.create().start(7070);
        app.get("/api/board/initial", context -> {
            new GetInitialBoardEndPoint(getInitialBoardUseCase, context).execute();
        });
        System.out.println("Servidor iniciado en http://localhost:7070/api/board/initial");
    }
}
