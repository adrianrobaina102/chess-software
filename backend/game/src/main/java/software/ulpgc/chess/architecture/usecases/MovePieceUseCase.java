package software.ulpgc.chess.architecture.usecases;

import software.ulpgc.chess.architecture.domain.model.Board;
import software.ulpgc.chess.architecture.domain.model.Move;
import software.ulpgc.chess.architecture.ports.in.BoardDeserializer;
import software.ulpgc.chess.architecture.ports.in.MoveDeserializer;
import software.ulpgc.chess.architecture.ports.out.BoardSerializer;

public class MovePieceUseCase implements UseCase<MovePieceUseCase.Input, String> {

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
