package software.ulpgc.chess;

import software.ulpgc.chess.application.domain.model.Board;
import software.ulpgc.chess.application.domain.model.State;
import software.ulpgc.chess.application.ports.deserializer.BoardDeserializer;
import software.ulpgc.chess.application.ports.deserializer.StateDeserializer;
import software.ulpgc.chess.application.ports.serializer.BoardSerializer;
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
        Board boardInitial = Board.initial();
        BoardSerializer serializer = new FenBoardSerializer();
        System.out.println(serializer.serialize(boardInitial));
    }
}
