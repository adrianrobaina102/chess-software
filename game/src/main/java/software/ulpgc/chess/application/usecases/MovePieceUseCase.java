package software.ulpgc.chess.application.usecases;

import software.ulpgc.chess.application.domain.model.Board;
import software.ulpgc.chess.application.domain.model.Move;
import software.ulpgc.chess.application.ports.deserializer.BoardDeserializer;
import software.ulpgc.chess.application.ports.deserializer.MoveDeserializer;
import software.ulpgc.chess.application.ports.serializer.BoardSerializer;

public class MovePieceUseCase implements UseCases<MovePieceUseCase.Input, String> {

    public record Input(String board, String move) {}

    private final BoardDeserializer boardDeserializer;
    private final MoveDeserializer moveDeserializer;
    private final BoardSerializer boardSerializer;

    public MovePieceUseCase(BoardDeserializer boardDeserializer,
                            MoveDeserializer moveDeserializer,
                            BoardSerializer boardSerializer) {
        this.boardDeserializer = boardDeserializer;
        this.moveDeserializer = moveDeserializer;
        this.boardSerializer = boardSerializer;
    }

    @Override
    public String execute(Input input) {
        return process(input);
    }

    private String process(Input input) {
        return process(boardDeserializer.deserialize(input.board()), input.move());
    }

    private String process(Board board, String move) {
        return process(board, moveDeserializer.deserialize(move));
    }

    private String process(Board board, Move move) {
        return process(board.move(move));
    }

    private String process(Board board) {
        return boardSerializer.serialize(board);
    }
}
